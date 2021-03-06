package com.mtl.hulk.configuration;

import com.mtl.hulk.serializer.KryoSerializer;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("mtl.hulk")
public class HulkProperties {

    private String loggerStorage = "mysql";
    private List<PoolProperties> logMasters;
    private List<PoolProperties> logSlaves;
    private int recoverySize = 20;
    private int logThreadPoolSize = Integer.MAX_VALUE;
    private Class<?> logSerialize = KryoSerializer.class;
    private String transIdSequence = "timestamp";
    private String retryTranactionCount = "3";

    public void setRetryTranactionCount(String retryTranactionCount) {
        this.retryTranactionCount = retryTranactionCount;
    }

    public String getRetryTranactionCount() {
        return retryTranactionCount;
    }

    public void setLoggerStorage(String loggerStorage) {
        this.loggerStorage = loggerStorage;
    }

    public String getLoggerStorage() {
        return loggerStorage;
    }

    public void setRecoverySize(int recoverySize) {
        this.recoverySize = recoverySize;
    }

    public int getRecoverySize() {
        return recoverySize;
    }

    public void setLogMasters(List<PoolProperties> logMasters) {
        this.logMasters = logMasters;
    }

    public List<PoolProperties> getLogMasters() {
        return logMasters;
    }

    public void setLogSlaves(List<PoolProperties> logSlaves) {
        this.logSlaves = logSlaves;
    }

    public List<PoolProperties> getLogSlaves() {
        return logSlaves;
    }

    public void setLogThreadPoolSize(int logThreadPoolSize) {
        this.logThreadPoolSize = logThreadPoolSize;
    }

    public int getLogThreadPoolSize() {
        return logThreadPoolSize;
    }

    public void setLogSerialize(Class<?> logSerialize) {
        this.logSerialize = logSerialize;
    }

    public Class<?> getLogSerialize() {
        return logSerialize;
    }

    public String getTransIdSequence() {
        return transIdSequence;
    }

}
