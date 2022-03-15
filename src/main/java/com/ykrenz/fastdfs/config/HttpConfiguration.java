package com.ykrenz.fastdfs.config;

import com.ykrenz.fastdfs.model.fdfs.FastDFSConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * http相关配置
 *
 * @author ykren
 * @date 2022/2/15
 */
public class HttpConfiguration {

    /**
     * web访问url 默认为负载均衡轮询
     * nginx参考配置 配合fastdfs-nginx-module使用
     *       server {
     *         listen 8888;
     *         server_name localhost;
     *         #location ~/group([0-9])/M00 {
     *             location ~/M00 {
     *                 root /home/data/fastdfs/storage;
     *                 if ($arg_attname ~ "^(.+)") {
     *                     add_header Content-Disposition "attachment;filename=$arg_attname";
     *                 }
     *                 ngx_fastdfs_module;
     *             }
     *         }
     */
    private List<String> webServers = new ArrayList<>();
    /**
     * web路径是否包含Group
     * 关联mod_fastdfs.conf url_have_group_name
     */
    private boolean urlHaveGroup;
    /**
     * 是否开启http防盗链
     * 关联http.config http.anti_steal.check_token
     */
    private boolean httpAntiStealToken;
    /**
     * http防盗链 secretKey
     * 关联http.config http.anti_steal.secret_key
     */
    private String secretKey = FastDFSConstants.DEFAULT_HTTP_SECRET_KEY;
    /**
     * 字符集
     */
    private String secretKeyCharset = FastDFSConstants.DEFAULT_CHARSET;

    public List<String> getWebServers() {
        return webServers;
    }

    public void setWebServers(List<String> webServers) {
        this.webServers = webServers;
    }

    public boolean isUrlHaveGroup() {
        return urlHaveGroup;
    }

    public void setUrlHaveGroup(boolean urlHaveGroup) {
        this.urlHaveGroup = urlHaveGroup;
    }

    public boolean isHttpAntiStealToken() {
        return httpAntiStealToken;
    }

    public void setHttpAntiStealToken(boolean httpAntiStealToken) {
        this.httpAntiStealToken = httpAntiStealToken;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKeyCharset() {
        return secretKeyCharset;
    }

    public void setSecretKeyCharset(String secretKeyCharset) {
        this.secretKeyCharset = secretKeyCharset;
    }
}
