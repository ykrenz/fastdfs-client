package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.common.FastDfsUtils;

import java.nio.charset.Charset;

/**
 * @author ykren
 * @date 2022/3/14
 */
public final class FastDfsWeb {
    /**
     * 分组名称
     */
    private String groupName;
    /**
     * path路径
     */
    private String path;
    /**
     * web url
     */
    private String url;
    /**
     * web路径是否包含Group
     */
    private boolean haveGroupName;
    /**
     * 防盗链
     */
    private boolean httpAntiStealToken;
    /**
     * http防盗链 secretKey
     */
    private String secretKey;
    /**
     * 字符集
     */
    private Charset secretKeyCharset;

    private static final String SLASH = "/";

    /**
     * 获取Web访问路径
     *
     * @return
     */
    public String accessUrl() {
        return getBaseWebPath().concat(getTokenPath());
    }

    /**
     * 获取文件下载地址
     *
     * @param filename
     * @return
     */
    public String downLoadUrl(String filename) {
        String tokenPath = getTokenPath();
        String webPath = getBaseWebPath();
        return tokenPath.isEmpty() ?
                webPath.concat("?filename=").concat(filename) :
                webPath.concat(tokenPath).concat("&").concat("filename=").concat(filename);
    }

    /**
     * 获取文件下载地址
     *
     * @param attachmentArgName attachment参数名称
     * @param filename
     * @return
     */
    public String downLoadUrl(String attachmentArgName, String filename) {
        String tokenPath = getTokenPath();
        String webPath = getBaseWebPath();
        return tokenPath.isEmpty() ?
                webPath.concat("?").concat(attachmentArgName).concat("=").concat(filename) :
                webPath.concat(tokenPath).concat("&").concat(attachmentArgName).concat("=").concat(filename);
    }

    private String getBaseWebPath() {
        return haveGroupName ? url.concat(SLASH).concat(getFullPath()) : url.concat(SLASH).concat(path);
    }

    private String getFullPath() {
        return groupName.concat(SLASH).concat(path);
    }

    private String getTokenPath() {
        if (!httpAntiStealToken) {
            return "";
        }
        return getTokenSuffix();
    }

    private String getTokenSuffix() {
        int ts = (int) (System.currentTimeMillis() / 1000);
        String token = FastDfsUtils.getToken(path, ts, secretKey, secretKeyCharset);
        return "?token=" + token + "&ts=" + ts;
    }


    public static Builder builder(String path, String url) {
        return new Builder(path, url);
    }

    private FastDfsWeb(Builder builder) {
        this.groupName = builder.groupName;
        this.path = builder.path;
        this.url = builder.url;
        this.haveGroupName = builder.haveGroupName;
        this.httpAntiStealToken = builder.httpAntiStealToken;
        this.secretKey = builder.secretKey;
        this.secretKeyCharset = builder.secretKeyCharset;
    }

    public static class Builder {
        private String groupName;
        private String path;
        private String url;
        private boolean haveGroupName;
        private boolean httpAntiStealToken;
        private String secretKey;
        private Charset secretKeyCharset;

        public Builder(String path, String url) {
            this.path = path;
            this.url = url;
        }

        public Builder haveGroupName(boolean haveGroupName) {
            this.haveGroupName = haveGroupName;
            return this;
        }

        public Builder groupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public Builder httpAntiStealToken(boolean httpAntiStealToken) {
            this.httpAntiStealToken = httpAntiStealToken;
            return this;
        }

        public Builder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public Builder secretKeyCharset(Charset charset) {
            this.secretKeyCharset = charset;
            return this;
        }

        public FastDfsWeb build() {
            return new FastDfsWeb(this);
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public boolean isHaveGroupName() {
        return haveGroupName;
    }

    public boolean isHttpAntiStealToken() {
        return httpAntiStealToken;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Charset getSecretKeyCharset() {
        return secretKeyCharset;
    }
}
