package com.ykrenz.fastdfs.model.proto.storage;

import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.proto.storage.internal.StorageGetMetadataRequest;
import com.ykrenz.fastdfs.model.proto.storage.internal.StorageGetMetadataResponse;

import java.util.Collections;
import java.util.Set;

/**
 * 设置文件标签
 *
 * @author tobato
 */
public class StorageGetMetadataCommand extends AbstractFdfsFileNotFoundCommand<Set<MetaData>> {


    /**
     * 设置文件标签(元数据)
     *
     * @param groupName
     * @param path
     */
    public StorageGetMetadataCommand(String groupName, String path) {
        this.request = new StorageGetMetadataRequest(groupName, path);
        // 输出响应
        this.response = new StorageGetMetadataResponse();
    }

    @Override
    protected Set<MetaData> handlerNotFoundFile() {
        return Collections.emptySet();
    }
}
