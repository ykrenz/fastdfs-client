package com.ykren.fastdfs;

import com.ykren.fastdfs.model.fdfs.StorePath;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

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
     *
     * nginx参考配置
     *          # 只预览不下载 例如txt log文件
     *          if ($request_filename ~* ^.*?\.(txt|log)$){
     *
     *                     add_header Content-Type 'text/plain;charset=utf-8';
     *          }
     *
     *          # 只下载不预览 例如rar gz zip exe等
     *         if ($request_filename ~* ^.*?\.(rar|gz|zip|exe)$){
     *                       add_header Content-Disposition attachment;
     *          }
     *
     */
    private String webUrl;

    /**
     * 读取时间 30s
     */
    private int socketTimeout = 30000;
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
         * 每个key最大空闲连接数 100
         */
        private static final int MAX_IDLE_PER_KEY = 100;
        /**
         * 每个key最小空闲连接数 10
         */
        private static final int MIN_IDLE_PER_KEY = 10;
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
            setMaxIdlePerKey(MAX_IDLE_PER_KEY);
            setMinIdlePerKey(MIN_IDLE_PER_KEY);
            setMinEvictableIdleTimeMillis(IDLE_TIME_MILLIS);
            setTimeBetweenEvictionRunsMillis(EVICT_IDLE_SCHEDULE_TIME_MILLIS);
        }
    }
}
