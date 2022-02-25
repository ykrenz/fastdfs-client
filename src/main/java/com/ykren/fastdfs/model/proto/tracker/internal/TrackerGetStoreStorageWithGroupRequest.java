package com.ykren.fastdfs.model.proto.tracker.internal;

import com.ykren.fastdfs.model.proto.CmdConstants;
import com.ykren.fastdfs.model.proto.FdfsRequest;
import com.ykren.fastdfs.model.proto.OtherConstants;
import com.ykren.fastdfs.model.proto.ProtoHead;
import com.ykren.fastdfs.model.proto.mapper.FdfsColumn;
import org.apache.commons.lang3.Validate;

/**
 * 按分组获取存储节点
 *
 * @author tobato
 */
public class TrackerGetStoreStorageWithGroupRequest extends FdfsRequest {

    private static final byte withGroupCmd = CmdConstants.TRACKER_PROTO_CMD_SERVICE_QUERY_STORE_WITH_GROUP_ONE;

    /**
     * 分组定义
     */
    @FdfsColumn(index = 0, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
    private final String groupName;

    /**
     * 获取存储节点
     *
     * @param groupName
     */
    public TrackerGetStoreStorageWithGroupRequest(String groupName) {
        Validate.notBlank(groupName, "分组不能为空");
        this.groupName = groupName;
        this.head = new ProtoHead(withGroupCmd);
    }

    public String getGroupName() {
        return groupName;
    }

}
