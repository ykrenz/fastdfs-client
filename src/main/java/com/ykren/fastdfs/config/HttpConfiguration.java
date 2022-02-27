package com.ykren.fastdfs.config;

import com.ykren.fastdfs.model.fdfs.StorePath;

import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.DEFAULT_CHARSET;
import static com.ykren.fastdfs.model.fdfs.FastDFSConstants.DEFAULT_HTTP_SECRET_KEY;

/**
 * http相关配置
 *
 * @author ykren
 * @date 2022/2/15
 */
public class HttpConfiguration {
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
     * 关联mod_fastdfs.conf url_have_group_name
     */
    private boolean webServerUrlHasGroup;
    /**
     * 是否开启http防盗链
     * 关联http.config http.anti_steal.check_token
     */
    private boolean httpAntiStealToken;
    /**
     * http防盗链 secretKey
     * 关联http.config http.anti_steal.secret_key
     */
    private String secretKey = DEFAULT_HTTP_SECRET_KEY;
    /**
     * 字符集
     */
    private String charset = DEFAULT_CHARSET;

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

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
