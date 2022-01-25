package com.ykren.fastdfs.model.proto.storage;

import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.proto.storage.internal.StorageGetMetadataRequest;
import com.ykren.fastdfs.model.proto.storage.internal.StorageGetMetadataResponse;

import java.util.HashSet;
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
    protected Set<MetaData> getResult() {
        return new HashSet<>();
    }
}
