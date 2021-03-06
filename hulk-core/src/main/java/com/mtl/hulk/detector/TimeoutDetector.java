package com.mtl.hulk.detector;

import com.mtl.hulk.AbstractHulk;
import com.mtl.hulk.BusinessActivityLogger;
import com.mtl.hulk.BusinessActivityLoggerFactory;
import com.mtl.hulk.HulkDataSource;
import com.mtl.hulk.configuration.HulkProperties;
import com.mtl.hulk.context.RuntimeContextHolder;
import com.mtl.hulk.model.BusinessActivityStatus;
import com.mtl.hulk.model.HulkTransactionActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class TimeoutDetector extends AbstractHulk implements Callable<Boolean> {

    private final static Logger logger = LoggerFactory.getLogger(TimeoutDetector.class);

    public TimeoutDetector(HulkDataSource ds, HulkProperties properties) {
        super(ds, properties);
    }

    @Override
    public Boolean call() throws Exception {
        logger.info("Transaction timeout detecting...: {} {} {} {} ",
                        RuntimeContextHolder.getContext().getActivity().getId().getBusinessDomain(),
                        RuntimeContextHolder.getContext().getActivity().getId().getBusinessActivity(),
                        RuntimeContextHolder.getContext().getActivity().getId().getEntityId(),
                        RuntimeContextHolder.getContext().getActivity().getId().getSequence());
        BusinessActivityLogger bal = BusinessActivityLoggerFactory.getStorage(dataSource, properties);
        HulkTransactionActivity hta = bal.getTranactionBusinessActivity(RuntimeContextHolder.getContext().getActivity().getId());
        if (hta.getBusinessActivity() == null) {
            logger.info("Transaction timeout detected: timeouted!");
            return Boolean.FALSE;
        }
        if (hta.getBusinessActivity().getStatus() == BusinessActivityStatus.ROLLBACKING
                || hta.getBusinessActivity().getStatus() == BusinessActivityStatus.COMMITTING) {
            logger.info("Transaction timeout detected: normal!");
            return Boolean.TRUE;
        }
        logger.info("Transaction timeout detected: timeouted!");
        return Boolean.FALSE;
    }

}
