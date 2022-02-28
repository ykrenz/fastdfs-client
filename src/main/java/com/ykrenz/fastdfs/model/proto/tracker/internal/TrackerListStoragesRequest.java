package com.ykrenz.fastdfs.model.proto.tracker.internal;

import com.ykrenz.fastdfs.model.proto.CmdConstants;
import com.ykrenz.fastdfs.model.proto.FdfsRequest;
import com.ykrenz.fastdfs.model.proto.OtherConstants;
import com.ykrenz.fastdfs.model.proto.ProtoHead;
import com.ykrenz.fastdfs.model.proto.mapper.DynamicFieldType;
import com.ykrenz.fastdfs.model.proto.mapper.FdfsColumn;
import org.apache.commons.lang3.Validate;

/**
 * 列出存储状态
 *
 * @author tobato
 */
public class TrackerListStoragesRequest extends FdfsRequest {

    /**
     * 组名
     */
    @FdfsColumn(index = 0, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
    private String groupName;
    /**
     * 存储服务器ip地址
     */
    @FdfsColumn(index = 1, max = OtherConstants.FDFS_IPADDR_SIZE - 1, dynamicField = DynamicFieldType.nullable)
    private String storageIpAddr;

    public TrackerListStoragesRequest() {
        head = new ProtoHead(CmdConstants.TRACKER_PROTO_CMD_SERVER_LIST_STORAGE);
    }

    /**
     * 列举存储服务器状态
     *
     * @param groupName
     * @param storageIpAddr
     */
    public TrackerListStoragesRequest(String groupName, String storageIpAddr) {
        this();
        Validate.notBlank(groupName, "分组不能为空");
        this.groupName = groupName;
        this.storageIpAddr = storageIpAddr;
    }

    /**
     * 列举组当中存储节点状态
     *
     * @param groupName
     */
    public TrackerListStoragesRequest(String groupName) {
        this();
        this.groupName = groupName;
        Validate.notBlank(groupName, "分组不能为空");
    }

    public String getGroupName() {
        return groupName;
    }

    public String getStorageIpAddr() {
        return storageIpAddr;
    }

}
