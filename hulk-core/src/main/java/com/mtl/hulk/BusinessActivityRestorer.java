package com.mtl.hulk;

import com.mtl.hulk.bam.BusinessActivityManagerImpl;
import com.mtl.hulk.configuration.HulkProperties;
import com.mtl.hulk.context.*;
import com.mtl.hulk.model.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mtl.hulk.BusinessActivityLogger.getBusinessActivityIdStr;

@Component
public class BusinessActivityRestorer extends AbstractHulk {

    private Logger logger = LoggerFactory.getLogger(BusinessActivityRestorer.class);

    @Autowired
    private BusinessActivityManagerImpl bam;
    @Autowired
    private HulkDataSource ds;
    @Autowired
    private HulkProperties properties;
    private final Map<String, AtomicInteger> map = new HashMap<String, AtomicInteger>();

    public void run() {

        BusinessActivityLogger businessActivityLogger = BusinessActivityLoggerFactory.getStorage(ds, properties);
        int retryTranactionCount = Integer.parseInt(properties.getRetryTranactionCount());
        List<String> businessActivityIds = new ArrayList<>();
        try {
            List<HulkTransactionActivity> hulkTransactionActivityList = businessActivityLogger.read(properties.getRecoverySize());
            if (CollectionUtils.isEmpty(hulkTransactionActivityList)) {
                logger.info("HulkJob businessActivityList is null");
                return;
            }
            for (HulkTransactionActivity hulkTransactionActivity : hulkTransactionActivityList) {
                businessActivityIds.add(getBusinessActivityIdStr(hulkTransactionActivity.getBusinessActivity().getId()));
                BusinessActivityContextHolder.setContext(hulkTransactionActivity.getHulkContext().getBac());
                RuntimeContextHolder.setContext(hulkTransactionActivity.getHulkContext().getRc());
                String businessActivityIdStr = getBusinessActivityIdStr(hulkTransactionActivity.getBusinessActivity().getId());
                int retryCount = getRetryCount(businessActivityIdStr);
                if (retryCount > retryTranactionCount) {
                    logger.error(String.format("recover failed with max retry count,will not try again" + "retried count:%d", retryCount));
                    continue;
                }
                if (hulkTransactionActivity.getBusinessActivity().getStatus() != BusinessActivityStatus.COMPLETE) {
                    if (hulkTransactionActivity.getBusinessActivity().getStatus() == BusinessActivityStatus.ROLLBACKED) {
                        bam.commit();
                    } else if (hulkTransactionActivity.getBusinessActivity().getStatus() == BusinessActivityStatus.ROLLBACKING_FAILED) {
                        bam.rollback();
                    }
                }
            }
            businessActivityLogger.remove(businessActivityIds);
        } catch (SQLException ex) {
            logger.error("Hulk Retry Exception", ex);
        }
    }

    private int getRetryCount(String businessActivityIdStr) {
        if (!map.containsKey(businessActivityIdStr)) {
            synchronized (map) {
                if (!map.containsKey(businessActivityIdStr))
                    map.put(businessActivityIdStr, new AtomicInteger(0));
            }
        }
        return map.get(businessActivityIdStr).incrementAndGet();
    }
}
