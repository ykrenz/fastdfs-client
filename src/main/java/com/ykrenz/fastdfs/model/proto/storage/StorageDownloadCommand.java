package com.ykrenz.fastdfs.model.proto.storage;

import com.ykrenz.fastdfs.model.proto.AbstractFdfsCommand;
import com.ykrenz.fastdfs.model.proto.storage.internal.StorageDownloadRequest;
import com.ykrenz.fastdfs.model.proto.storage.internal.StorageDownloadResponse;

/**
 * 文件下载命令
 *
 * @param <T>
 * @author tobato
 */
public class StorageDownloadCommand<T> extends AbstractFdfsCommand<T> {

    /**
     * 下载部分文件
     *
     * @param groupName
     * @param path
     * @param fileOffset
     * @param downloadBytes
     */
    public StorageDownloadCommand(String groupName, String path, long fileOffset, long downloadBytes,
                                  DownloadCallback<T> callback) {
        super();
        this.request = new StorageDownloadRequest(groupName, path, fileOffset, downloadBytes);
        // 输出响应
        this.response = new StorageDownloadResponse<T>(callback);
    }

    /**
     * 下载文件
     *
     * @param groupName
     * @param path
     */
    public StorageDownloadCommand(String groupName, String path, DownloadCallback<T> callback) {
        super();
        this.request = new StorageDownloadRequest(groupName, path, 0, 0);
        // 输出响应
        this.response = new StorageDownloadResponse<T>(callback);
    }
}
