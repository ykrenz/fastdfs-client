package com.ykrenz.fastdfs.model.proto.storage;

import com.ykrenz.fastdfs.model.proto.AbstractFdfsCommand;
import com.ykrenz.fastdfs.model.proto.ErrorCodeConstants;
import com.ykrenz.fastdfs.model.proto.ProtoHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * 交易命令抽象类 忽略文件找不到异常
 *
 * @param <T>
 * @author ykren
 */
public abstract class AbstractFdfsFileNotFoundCommand<T> extends AbstractFdfsCommand<T> {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFdfsFileNotFoundCommand.class);

    /**
     * 优化文件找不到异常
     *
     * @param in
     * @param charset
     * @return
     * @throws IOException
     */
    @Override
    protected T receive(InputStream in, Charset charset) throws IOException {
        // 解析报文头
        ProtoHead head = ProtoHead.createFromInputStream(in);
        LOGGER.debug("服务端返回报文头{}", head);
        // 忽略异常
        if (ErrorCodeConstants.ERR_NO_ENOENT == head.getStatus()) {
            return handlerNotFoundFile();
        }
        // 校验报文头
        head.validateResponseHead();
        // 解析报文体
        return response.decode(head, in, charset);
    }

    /**
     * 返回值
     *
     * @return
     */
    protected T handlerNotFoundFile() {
        return null;
    }
}
