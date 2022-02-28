package com.ykrenz.fastdfs.config;

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
    private String groupName;
    /**
     * http相关配置
     */
    private HttpConfiguration http = new HttpConfiguration();
    /**
     * 连接配置
     */
    private ConnectionConfiguration connection = new ConnectionConfiguration();

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
