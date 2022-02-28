package com.ykrenz.fastdfs.model.proto.tracker;

import com.ykrenz.fastdfs.model.fdfs.StorageNode;
import com.ykrenz.fastdfs.model.proto.AbstractFdfsCommand;
import com.ykrenz.fastdfs.model.proto.FdfsResponse;
import com.ykrenz.fastdfs.model.proto.tracker.internal.TrackerGetStoreStorageRequest;
import com.ykrenz.fastdfs.model.proto.tracker.internal.TrackerGetStoreStorageWithGroupRequest;

/**
 * 获取存储节点命令
 *
 * @author tobato
 */
public class TrackerGetStoreStorageCommand extends AbstractFdfsCommand<StorageNode> {

    public TrackerGetStoreStorageCommand(String groupName) {
        super.request = new TrackerGetStoreStorageWithGroupRequest(groupName);
        super.response = new FdfsResponse<StorageNode>() {
            // default response
        };
    }

    public TrackerGetStoreStorageCommand() {
        super.request = new TrackerGetStoreStorageRequest();
        super.response = new FdfsResponse<StorageNode>() {
            // default response
        };
    }

}
