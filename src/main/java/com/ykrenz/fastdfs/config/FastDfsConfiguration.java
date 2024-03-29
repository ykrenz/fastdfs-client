package com.ykrenz.fastdfs.config;

/**
 * FastDfs配置类
 *
 * @author ykren
 * @date 2022/1/24
 */
public class FastDfsConfiguration {

    /**
     * 默认分组 固定客户端上传分组 若已设置其他分组 则配置失效
     */
    private String defaultGroup;
    /**
     * http相关配置
     */
    private HttpConfiguration http = new HttpConfiguration();
    /**
     * 连接配置
     */
    private ConnectionConfiguration connection = new ConnectionConfiguration();

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
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

}
