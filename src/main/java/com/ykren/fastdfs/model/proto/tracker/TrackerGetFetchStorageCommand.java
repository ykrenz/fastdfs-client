package com.ykren.fastdfs.model.proto.tracker;

import com.ykren.fastdfs.model.fdfs.StorageNodeInfo;
import com.ykren.fastdfs.model.proto.AbstractFdfsCommand;
import com.ykren.fastdfs.model.proto.FdfsResponse;
import com.ykren.fastdfs.model.proto.tracker.internal.TrackerGetFetchStorageRequest;

/**
 * 获取源服务器
 *
 * @author tobato
 */
public class TrackerGetFetchStorageCommand extends AbstractFdfsCommand<StorageNodeInfo> {

    public TrackerGetFetchStorageCommand(String groupName, String path, boolean toUpdate) {
        super.request = new TrackerGetFetchStorageRequest(groupName, path, toUpdate);
        super.response = new FdfsResponse<StorageNodeInfo>() {
            // default response
        };
    }

}
