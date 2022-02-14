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
     */
//    server {
//        listen 8888;
//        server_name localhost;
//        #location ~/group([0-9])/M00 {
//            location ~/M00 {
//                root /home/data/fastdfs/storage;
//                if ($arg_attname ~ "^(.+)") {
//                    add_header Content-Disposition "attachment;filename=$arg_attname";
//                }
//                ngx_fastdfs_module;
//            }
//        }
    private String webServerUrl;
    /**
     * web路径是否包含Group
     */
    private boolean webServerUrlHasGroup;
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

    public String getWebServerUrl() {
        return webServerUrl;
    }

    public void setWebServerUrl(String webServerUrl) {
        this.webServerUrl = webServerUrl;
    }

    public boolean isWebServerUrlHasGroup() {
        return webServerUrlHasGroup;
    }

    public void setWebServerUrlHasGroup(boolean webServerUrlHasGroup) {
        this.webServerUrlHasGroup = webServerUrlHasGroup;
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
