package com.ykrenz.fastdfs;

/**
 * @author ykren
 * @date 2022/3/15
 */
public interface HttpServerClient {

    /**
     * 文件访问路径url
     *
     * @param groupName
     * @param path
     * @return
     */
    String accessUrl(String groupName, String path);

    /**
     * 自定义下载文件名地址
     *
     * @param groupName
     * @param path
     * @param downLoadName
     * @return
     */
    String downLoadUrl(String groupName, String path, String downLoadName);

    /**
     * 自定义下载文件名 参数名地址
     *
     * @param groupName
     * @param path
     * @param urlArgName
     * @param downLoadName
     * @return
     */
    String downLoadUrl(String groupName, String path, String urlArgName, String downLoadName);
}
