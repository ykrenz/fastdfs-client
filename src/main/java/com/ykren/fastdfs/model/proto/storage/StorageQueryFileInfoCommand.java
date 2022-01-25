package com.ykren.fastdfs.model.proto.storage;

import com.ykren.fastdfs.model.fdfs.FileInfo;
import com.ykren.fastdfs.model.proto.FdfsResponse;
import com.ykren.fastdfs.model.proto.storage.internal.StorageQueryFileInfoRequest;

/**
 * 文件查询命令
 *
 * @author tobato
 */
public class StorageQueryFileInfoCommand extends AbstractFdfsFileNotFoundCommand<FileInfo> {

    /**
     * 文件查询命令
     *
     * @param groupName
     * @param path
     */
    public StorageQueryFileInfoCommand(String groupName, String path) {
        super();
        this.request = new StorageQueryFileInfoRequest(groupName, path);
        // 输出响应
        this.response = new FdfsResponse<FileInfo>() {
            // default response
        };
    }

}
