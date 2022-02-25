package com.ykren.fastdfs.model.proto.storage.internal;

import com.ykren.fastdfs.model.proto.CmdConstants;
import com.ykren.fastdfs.model.proto.FdfsRequest;
import com.ykren.fastdfs.model.proto.OtherConstants;
import com.ykren.fastdfs.model.proto.ProtoHead;
import com.ykren.fastdfs.model.proto.mapper.DynamicFieldType;
import com.ykren.fastdfs.model.proto.mapper.FdfsColumn;

/**
 * 文件下载请求
 *
 * @author tobato
 */
public class StorageDownloadRequest extends FdfsRequest {

    /**
     * 开始位置
     */
    @FdfsColumn(index = 0)
    private long fileOffset;
    /**
     * 读取文件长度
     */
    @FdfsColumn(index = 1)
    private long downloadBytes;
    /**
     * 组名
     */
    @FdfsColumn(index = 2, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
    private String groupName;
    /**
     * 文件路径
     */
    @FdfsColumn(index = 3, dynamicField = DynamicFieldType.allRestByte)
    private String path;

    /**
     * 文件下载请求
     *
     * @param groupName
     * @param path
     * @param fileOffset
     * @param downloadBytes
     */
    public StorageDownloadRequest(String groupName, String path, long fileOffset, long downloadBytes) {
        super();
        this.groupName = groupName;
        this.downloadBytes = downloadBytes;
        this.path = path;
        this.fileOffset = fileOffset;
        head = new ProtoHead(CmdConstants.STORAGE_PROTO_CMD_DOWNLOAD_FILE);

    }

    public long getFileOffset() {
        return fileOffset;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getPath() {
        return path;
    }

    public long getDownloadBytes() {
        return downloadBytes;
    }
}
