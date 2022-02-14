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
     * 重试次数
     */
    private int retry;

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

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }
}