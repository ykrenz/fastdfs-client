package com.ykrenz.fastdfs.model.proto.storage;

import com.ykrenz.fastdfs.model.fdfs.StorePath;
import com.ykrenz.fastdfs.model.proto.AbstractFdfsCommand;
import com.ykrenz.fastdfs.model.proto.FdfsResponse;
import com.ykrenz.fastdfs.model.proto.storage.internal.StorageRegenerateAppendFileRequest;

/**
 * appender类型文件改为普通文件命令
 *
 * @author ykren
 * @date 2022/1/20
 */
public class StorageRegenerateAppendFileCommand extends AbstractFdfsCommand<StorePath> {


    /**
     * StorageRegenerateAppendFileCommand
     *
     * @param path
     */
    public StorageRegenerateAppendFileCommand(String path) {
        super();
        this.request = new StorageRegenerateAppendFileRequest(path);
        // 输出响应
        this.response = new FdfsResponse<StorePath>() {
            // default response
        };
    }
}
