package com.ykrenz.fastdfs.config;

import com.ykrenz.fastdfs.model.fdfs.FastDFSConstants;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * 连接配置
 */
public class ConnectionConfiguration {
    /**
     * 读取时间
     */
    private int socketTimeout = FastDFSConstants.DEFAULT_SOCKET_TIMEOUT;
    /**
     * 连接超时时间
     */
    private int connectTimeout = FastDFSConstants.DEFAULT_CONNECT_TIMEOUT;
    /**
     * 字符集
     */
    private String charset = FastDFSConstants.DEFAULT_CHARSET;
    /**
     * tracker不可用后多少秒后重试
     */
    private int retryAfterSecond = FastDFSConstants.DEFAULT_RETRY_AFTER_SECOND;

    /**
     * 连接池配置
     */
    private ConnectionPoolConfiguration pool = new ConnectionPoolConfiguration();

    static class ConnectionPoolConfiguration extends GenericKeyedObjectPoolConfig {

        public ConnectionPoolConfiguration() {
            this.setMaxWaitMillis(FastDFSConstants.MAX_WAIT_MILLIS);
            this.setMaxTotalPerKey(FastDFSConstants.MAX_TOTAL_PER_KEY);
            this.setMaxIdlePerKey(FastDFSConstants.MAX_IDLE_PER_KEY);
            this.setMinIdlePerKey(FastDFSConstants.MIN_IDLE_PER_KEY);
            this.setMinEvictableIdleTimeMillis(FastDFSConstants.IDLE_TIME_MILLIS);
            this.setTimeBetweenEvictionRunsMillis(FastDFSConstants.EVICT_IDLE_SCHEDULE_TIME_MILLIS);
            this.setJmxNameBase(FastDFSConstants.JMX_NAME_BASE);
            this.setJmxNamePrefix(FastDFSConstants.JMX_NAME_PREFIX);
            this.setTestOnBorrow(FastDFSConstants.TEST_ON_BORROW);
        }
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public int getRetryAfterSecond() {
        return retryAfterSecond;
    }

    public void setRetryAfterSecond(int retryAfterSecond) {
        this.retryAfterSecond = retryAfterSecond;
    }

    public ConnectionPoolConfiguration getPool() {
        return pool;
    }

    public void setPool(ConnectionPoolConfiguration pool) {
        this.pool = pool;
    }
}