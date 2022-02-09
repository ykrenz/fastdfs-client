package com.ykren.fastdfs;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * FastDfs配置类
 *
 * @author ykren
 * @date 2022/1/24
 */
public class FastDFSConfiguration {

    /**
     * 读取时间 5s
     */
    private int socketTimeout = 5000;
    /**
     * 连接超时时间 5s
     */
    private int connectTimeout = 5000;
    /**
     * 字符集
     */
    private String charset = "UTF-8";

    /**
     * 连接池配置
     */
    private Pool pool = new Pool();

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

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    protected static class Pool extends GenericKeyedObjectPoolConfig {
        /**
         * 获取连接时的最大等待毫秒数 5s
         */
        private static final long MAX_WAIT_MILLIS = 5000;
        /**
         * 每个key最大连接数 500
         */
        private static final int MAX_TOTAL_PER_KEY = 500;
        /**
         * 每个key最小空闲连接数 10
         */
        private static final int MIN_IDLE_PER_KEY = 10;
        /**
         * 每个key最大空闲连接数 100
         */
        private static final int MAX_IDLE_PER_KEY = 100;
        /**
         * 空闲连接存活时长 30min
         */
        private static final long IDLE_TIME_MILLIS = 1000L * 60L * 30L;
        /**
         * 清理空闲连接任务时长 1min
         */
        private static final long EVICT_IDLE_SCHEDULE_TIME_MILLIS = 1000L * 60L;

        public Pool() {
            setMaxWaitMillis(MAX_WAIT_MILLIS);
            setMaxTotalPerKey(MAX_TOTAL_PER_KEY);
            setMaxIdlePerKey(MIN_IDLE_PER_KEY);
            setMinIdlePerKey(MAX_IDLE_PER_KEY);
            setMinEvictableIdleTimeMillis(IDLE_TIME_MILLIS);
            setTimeBetweenEvictionRunsMillis(EVICT_IDLE_SCHEDULE_TIME_MILLIS);
        }
    }
}
