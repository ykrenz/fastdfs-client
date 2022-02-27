package com.ykren.fastdfs.config;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.*;

/**
 * 连接配置
 */
public class ConnectionConfiguration {
    /**
     * 读取时间
     */
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    /**
     * 连接超时时间
     */
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    /**
     * 字符集
     */
    private String charset = DEFAULT_CHARSET;
    /**
     * tracker不可用后多少秒后重试
     */
    private int retryAfterSecond = DEFAULT_RETRY_AFTER_SECOND;

    /**
     * 连接池配置
     */
    private ConnectionPoolConfiguration pool = new ConnectionPoolConfiguration();

    static class ConnectionPoolConfiguration extends GenericKeyedObjectPoolConfig {

        public ConnectionPoolConfiguration() {
            this.setMaxWaitMillis(MAX_WAIT_MILLIS);
            this.setMaxTotalPerKey(MAX_TOTAL_PER_KEY);
            this.setMaxIdlePerKey(MAX_IDLE_PER_KEY);
            this.setMinIdlePerKey(MIN_IDLE_PER_KEY);
            this.setMinEvictableIdleTimeMillis(IDLE_TIME_MILLIS);
            this.setTimeBetweenEvictionRunsMillis(EVICT_IDLE_SCHEDULE_TIME_MILLIS);
            this.setJmxNameBase("com.ykren.fastdfs.conn:type=FdfsConnectionPool");
            this.setJmxNamePrefix("fdfsPool");
            this.setTestOnCreate(false);
            this.setTestOnBorrow(true);
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