package com.ykren.fastdfs.model.proto.storage;

import com.ykren.fastdfs.model.fdfs.StorePath;
import com.ykren.fastdfs.model.proto.AbstractFdfsCommand;
import com.ykren.fastdfs.model.proto.FdfsResponse;
import com.ykren.fastdfs.model.proto.storage.internal.StorageRegenerateAppendFileRequest;

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
