package com.ykren.fastdfs.model.fdfs;

/**
 * @author ykren
 * @date 2022/2/11
 */
public final class FastDFSConstants {

    public static final int KB = 1024;
    public static final int DEFAULT_BUFFER_SIZE = 8 * KB;
    public static final int DEFAULT_STREAM_BUFFER_SIZE = 512 * KB;

    /**
     * 读取时间 30s
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;
    /**
     * 连接超时时间 5s
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    /**
     * 字符集
     */
    public static final String DEFAULT_CHARSET = "UTF-8";
    /**
     * tracker不可用后默认重试连接时长 30s
     */
    public static final int DEFAULT_RETRY_AFTER_SECOND = 30;
    /**
     * 获取连接时的最大等待毫秒数 5s
     */
    public static final long MAX_WAIT_MILLIS = 5000;
    /**
     * 每个key最大连接数 500
     */
    public static final int MAX_TOTAL_PER_KEY = 500;
    /**
     * 每个key最大空闲连接数 100
     */
    public static final int MAX_IDLE_PER_KEY = 100;
    /**
     * 每个key最小空闲连接数 10
     */
    public static final int MIN_IDLE_PER_KEY = 10;
    /**
     * 空闲连接存活时长 30min
     */
    public static final long IDLE_TIME_MILLIS = 1000L * 60L * 30L;
    /**
     * 清理空闲连接任务时长 1min
     */
    public static final long EVICT_IDLE_SCHEDULE_TIME_MILLIS = 1000L * 60L;
}
