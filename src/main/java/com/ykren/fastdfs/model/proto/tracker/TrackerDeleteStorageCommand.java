package com.ykren.fastdfs.model.proto.tracker;

import com.ykren.fastdfs.model.proto.AbstractFdfsCommand;
import com.ykren.fastdfs.model.proto.FdfsResponse;
import com.ykren.fastdfs.model.proto.tracker.internal.TrackerDeleteStorageRequest;

/**
 * 移除存储服务器命令
 *
 * @author tobato
 */
public class TrackerDeleteStorageCommand extends AbstractFdfsCommand<Void> {

    public TrackerDeleteStorageCommand(String groupName, String storageIpAddr) {
        super.request = new TrackerDeleteStorageRequest(groupName, storageIpAddr);
        super.response = new FdfsResponse<Void>() {
            // default response
        };
    }

}
