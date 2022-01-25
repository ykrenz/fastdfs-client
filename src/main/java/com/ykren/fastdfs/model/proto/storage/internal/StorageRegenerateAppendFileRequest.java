package com.ykren.fastdfs.model.proto.storage.internal;

import com.ykren.fastdfs.model.proto.CmdConstants;
import com.ykren.fastdfs.model.proto.FdfsRequest;
import com.ykren.fastdfs.model.proto.ProtoHead;
import com.ykren.fastdfs.model.proto.mapper.DynamicFieldType;
import com.ykren.fastdfs.model.proto.mapper.FdfsColumn;

/**
 * appender类型文件改为普通文件命令
 *
 * @author ykren
 * @date 2022/1/20
 */
public class StorageRegenerateAppendFileRequest extends FdfsRequest {

    /**
     * 文件路径
     */
    @FdfsColumn(index = 1, dynamicField = DynamicFieldType.allRestByte)
    private String path;

    /**
     * 文件Truncate命令
     *
     * @param path
     */
    public StorageRegenerateAppendFileRequest(String path) {
        super();
        this.path = path;
        head = new ProtoHead(CmdConstants.STORAGE_PROTO_CMD_REGENERATE_APPENDER_FILENAME);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
