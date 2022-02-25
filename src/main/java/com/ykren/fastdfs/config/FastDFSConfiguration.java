package com.ykren.fastdfs.config;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.*;

/**
 * FastDfs配置类
 *
 * @author ykren
 * @date 2022/1/24
 */
public class FastDFSConfiguration {

    /**
     * 上传到固定分组 优先级大于参数
     */
    private String group;
    /**
     * http相关配置
     */
    private HttpConfiguration http;
    /**
     * 连接配置
     */
    private ConnectionConfiguration connection;
    /**
     * 连接池配置
     */
    private GenericKeyedObjectPoolConfig pool;

    public FastDFSConfiguration() {
        http = new HttpConfiguration();
        http.setHttpAntiStealToken(false);
        http.setWebServerUrlHasGroup(false);
        http.setSecretKey(DEFAULT_HTTP_SECRET_KEY);
        http.setCharset(DEFAULT_CHARSET);

        connection = new ConnectionConfiguration();
        connection.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        connection.setCharset(DEFAULT_CHARSET);
        connection.setRetryAfterSecond(DEFAULT_RETRY_AFTER_SECOND);

        pool = new GenericKeyedObjectPoolConfig();
        pool.setMaxWaitMillis(MAX_WAIT_MILLIS);
        pool.setMaxTotalPerKey(MAX_TOTAL_PER_KEY);
        pool.setMaxIdlePerKey(MAX_IDLE_PER_KEY);
        pool.setMinIdlePerKey(MIN_IDLE_PER_KEY);
        pool.setMinEvictableIdleTimeMillis(IDLE_TIME_MILLIS);
        pool.setTimeBetweenEvictionRunsMillis(EVICT_IDLE_SCHEDULE_TIME_MILLIS);
        pool.setTestOnCreate(true);
        pool.setTestOnBorrow(true);
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public HttpConfiguration getHttp() {
        return http;
    }

    public void setHttp(HttpConfiguration http) {
        this.http = http;
    }

    public ConnectionConfiguration getConnection() {
        return connection;
    }

    public void setConnection(ConnectionConfiguration connection) {
        this.connection = connection;
    }

    public GenericKeyedObjectPoolConfig getPool() {
        return pool;
    }

    public void setPool(GenericKeyedObjectPoolConfig pool) {
        this.pool = pool;
    }
}
