package com.ykren.fastdfs.model.proto.storage;

import com.ykren.fastdfs.exception.FdfsUnavailableException;
import com.ykren.fastdfs.model.fdfs.StorePath;
import com.ykren.fastdfs.model.proto.AbstractFdfsCommand;
import com.ykren.fastdfs.model.proto.ErrorCodeConstants;
import com.ykren.fastdfs.model.proto.FdfsResponse;
import com.ykren.fastdfs.model.proto.ProtoHead;
import com.ykren.fastdfs.model.proto.storage.internal.StorageRegenerateAppendFileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 分片上传appender类型文件改为普通文件命令
 *
 * @author ykren
 * @date 2022/1/26
 */
public class CompleteMultipartRegenerateFileCommand extends AbstractFdfsCommand<StorePath> {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CompleteMultipartRegenerateFileCommand.class);

    /**
     * StorageRegenerateAppendFileCommand
     *
     * @param path
     */
    public CompleteMultipartRegenerateFileCommand(String path) {
        super();
        this.request = new StorageRegenerateAppendFileRequest(path);
        // 输出响应
        this.response = new FdfsResponse<StorePath>() {
            // default response
        };
    }


    /**
     * 优化文件找不到异常
     *
     * @param in
     * @param charset
     * @return
     * @throws IOException
     */
    @Override
    protected StorePath receive(InputStream in, Charset charset) throws IOException {
        // 解析报文头
        ProtoHead head = ProtoHead.createFromInputStream(in);
        LOGGER.debug("服务端返回报文头{}", head);
        // 忽略异常
        if (ErrorCodeConstants.ERR_NO_ENOENT == head.getStatus()) {
            throw new FdfsUnavailableException("File does not exist maybe abort upload or has been completed. ");
        }
        // 校验报文头
        head.validateResponseHead();
        // 解析报文体
        return response.decode(head, in, charset);
    }

}
