package com.mtl.demo.serviceC.service.impl;

import com.mtl.demo.serviceC.service.HulkServiceC;
import com.mtl.hulk.annotation.MTLDTransaction;
import com.mtl.hulk.context.BusinessActivityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HulkServcieCImpl implements HulkServiceC {

    private final static Logger logger = LoggerFactory.getLogger(HulkServcieCImpl.class);

    @MTLDTransaction(confirmMethod = "confirmMysqlSaveAssetCCard", cancelMethod = "cancelMysqlSaveAssetCCard")
    @Override
    public String getHulkServiceC(int a) {
        return "HulkServiceCXXXXXXXXXXXXXXXXXXXXXXXs";
    }
    public boolean confirmMysqlSaveAssetCCard(BusinessActivityContext context) {
        logger.info("confirm C params: {}", context.getParams().get("getHulkServiceC"));
        return true;
    }

    public boolean cancelMysqlSaveAssetCCard(BusinessActivityContext context) {
        logger.info("cancel C params: {}", context.getParams().get("getHulkServiceC"));
        return true;
    }

}
