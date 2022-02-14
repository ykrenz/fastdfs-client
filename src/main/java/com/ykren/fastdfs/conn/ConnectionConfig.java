package com.ykren.fastdfs.conn;

public class ConnectionConfig {
    /**
     * 读取时间
     */
    private int socketTimeout;
    /**
     * 连接超时时间
     */
    private int connectTimeout;
    /**
     * 字符集
     */
    private String charset;
    /**
     * 经过多长时间后重试
     */
    private int retryTimeMills;
    /**
     * tracker不可用多少次被踢出
     */
    private int cullAfterCount;
    /**
     * tracker被踢出后多少秒后重试
     */
    private int cullRetryAfterSecond;

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

    public int getRetryTimeMills() {
        return retryTimeMills;
    }

    public void setRetryTimeMills(int retryTimeMills) {
        this.retryTimeMills = retryTimeMills;
    }

    public int getCullAfterCount() {
        return cullAfterCount;
    }

    public void setCullAfterCount(int cullAfterCount) {
        this.cullAfterCount = cullAfterCount;
    }

    public int getRetryAfterSecond() {
        return cullRetryAfterSecond;
    }

    public void setRetryAfterSecond(int retryAfterSecond) {
        this.cullRetryAfterSecond = retryAfterSecond;
    }
}