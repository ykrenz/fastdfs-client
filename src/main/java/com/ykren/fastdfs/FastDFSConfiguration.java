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

        public Pool() {
            setMaxWaitMillis(getMaxWaitMillis());
            setMaxTotalPerKey(getMaxTotalPerKey());
            setMaxIdlePerKey(getMaxIdlePerKey());
            setMinIdlePerKey(getMinIdlePerKey());
        }

        /**
         * 获取连接时的最大等待毫秒数(默认配置为5秒)
         */
        private long maxWaitMillis = 5000;
        /**
         * 每个key最大连接数
         */
        private int maxTotalPerKey = 200;
        /**
         * 每个key最小空闲连接数
         */
        private int minIdlePerKey = 10;
        /**
         * 每个key最大空闲连接数
         */
        private int maxIdlePerKey = 100;

        @Override
        public long getMaxWaitMillis() {
            return maxWaitMillis;
        }

        @Override
        public void setMaxWaitMillis(long maxWaitMillis) {
            this.maxWaitMillis = maxWaitMillis;
        }

        @Override
        public int getMaxTotalPerKey() {
            return maxTotalPerKey;
        }

        @Override
        public void setMaxTotalPerKey(int maxTotalPerKey) {
            this.maxTotalPerKey = maxTotalPerKey;
        }

        @Override
        public int getMinIdlePerKey() {
            return minIdlePerKey;
        }

        @Override
        public void setMinIdlePerKey(int minIdlePerKey) {
            this.minIdlePerKey = minIdlePerKey;
        }

        @Override
        public int getMaxIdlePerKey() {
            return maxIdlePerKey;
        }

        @Override
        public void setMaxIdlePerKey(int maxIdlePerKey) {
            this.maxIdlePerKey = maxIdlePerKey;
        }
    }
}
