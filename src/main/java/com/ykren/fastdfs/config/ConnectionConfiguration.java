package com.ykren.fastdfs.config;

/**
 * 连接配置
 */
public class ConnectionConfiguration {
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
     * tracker不可用后多少秒后重试
     */
    private int retryAfterSecond;

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
}