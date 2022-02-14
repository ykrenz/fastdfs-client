package com.ykren.fastdfs;

import com.ykren.fastdfs.conn.ConnectionConfig;
import com.ykren.fastdfs.model.fdfs.StorePath;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.DEFAULT_CHARSET;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.DEFAULT_CONNECT_TIMEOUT;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.DEFAULT_RETRY;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.DEFAULT_SOCKET_TIMEOUT;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.EVICT_IDLE_SCHEDULE_TIME_MILLIS;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.IDLE_TIME_MILLIS;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.MAX_IDLE_PER_KEY;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.MAX_TOTAL_PER_KEY;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.MAX_WAIT_MILLIS;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.MIN_IDLE_PER_KEY;

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
     * web访问路径 eg: nginx地址
     * <p>
     * 配合fastdfs-nginx-module使用
     * <p>
     * 预览地址{@link StorePath#getWebPath()}
     * 下载地址{@link StorePath#getDownLoadPath(String)} ()}
     * <p>
     * nginx参考配置
     * # 只预览不下载 例如txt log文件
     * if ($request_filename ~* ^.*?\.(txt|log)$){
     * <p>
     * add_header Content-Type 'text/plain;charset=utf-8';
     * }
     * <p>
     * # 只下载不预览 例如rar gz zip exe等
     * if ($request_filename ~* ^.*?\.(rar|gz|zip|exe)$){
     * add_header Content-Disposition attachment;
     * }
     */
    private String webUrl;

    /**
     * 连接配置
     */
    private ConnectionConfig connection;
    /**
     * 连接池配置
     */
    private GenericKeyedObjectPoolConfig pool;

    public FastDFSConfiguration() {
        connection = new ConnectionConfig();
        connection.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        connection.setCharset(DEFAULT_CHARSET);
        connection.setRetry(DEFAULT_RETRY);

        pool = new GenericKeyedObjectPoolConfig();
        pool.setMaxWaitMillis(MAX_WAIT_MILLIS);
        pool.setMaxTotalPerKey(MAX_TOTAL_PER_KEY);
        pool.setMaxIdlePerKey(MAX_IDLE_PER_KEY);
        pool.setMinIdlePerKey(MIN_IDLE_PER_KEY);
        pool.setMinEvictableIdleTimeMillis(IDLE_TIME_MILLIS);
        pool.setTimeBetweenEvictionRunsMillis(EVICT_IDLE_SCHEDULE_TIME_MILLIS);
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public ConnectionConfig getConnection() {
        return connection;
    }

    public void setConnection(ConnectionConfig connection) {
        this.connection = connection;
    }

    public GenericKeyedObjectPoolConfig getPool() {
        return pool;
    }

    public void setPool(GenericKeyedObjectPoolConfig pool) {
        this.pool = pool;
    }
}
