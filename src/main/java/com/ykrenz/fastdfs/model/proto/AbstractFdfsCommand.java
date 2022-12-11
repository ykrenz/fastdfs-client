package com.ykrenz.fastdfs.model.proto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import com.ykrenz.fastdfs.conn.Connection;
import com.ykrenz.fastdfs.event.ProgressEventType;
import com.ykrenz.fastdfs.event.ProgressInputStream;
import com.ykrenz.fastdfs.event.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ykrenz.fastdfs.exception.FdfsIOException;

import static com.ykrenz.fastdfs.event.ProgressPublisher.publishProgress;
import static com.ykrenz.fastdfs.model.fdfs.FastDFSConstants.DEFAULT_BUFFER_SIZE;

/**
 * 交易命令抽象类
 *
 * @param <T>
 * @author tobato
 */
public abstract class AbstractFdfsCommand<T> implements FdfsCommand<T> {

    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFdfsCommand.class);

    /**
     * 表示请求消息
     */
    protected FdfsRequest request;

    /**
     * 解析反馈消息对象
     */
    protected FdfsResponse<T> response;

    /**
     * 对服务端发出请求然后接收反馈
     */
    @Override
    public T execute(Connection conn) {
        // 封装socket交易 send
        try {
            send(conn.getOutputStream(), conn.getCharset());
        } catch (IOException e) {
            LOGGER.error("send conent error", e);
            throw new FdfsIOException("socket io exception occured while sending cmd", e);
        }

        try {
            return receive(conn.getInputStream(), conn.getCharset());
        } catch (IOException e) {
            LOGGER.error("receive conent error", e);
            throw new FdfsIOException("socket io exception occured while receive content", e);
        }

    }

    /**
     * 将报文输出规范为模板方法
     * <p>
     * <pre>
     * 1.输出报文头
     * 2.输出报文参数
     * 3.输出文件内容
     * </pre>
     *
     * @param out
     * @throws IOException
     */
    protected void send(OutputStream out, Charset charset) throws IOException {
        // 报文分为三个部分
        // 报文头
        byte[] head = request.getHeadByte(charset);
        // 交易参数
        byte[] param = request.encodeParam(charset);
        // 交易文件流
        InputStream inputFile = request.getInputFile();
        long fileSize = request.getFileSize();
        LOGGER.debug("发出交易请求..报文头为{}", request.getHead());
        LOGGER.debug("交易参数为{}", param);
        // 输出报文头
        out.write(head);
        // 输出交易参数
        if (null != param) {
            out.write(param);
        }
        // 输出文件流
        ProgressListener listener = ProgressListener.NOOP;
        if (null != inputFile) {
            try {
                if (inputFile instanceof ProgressInputStream) {
                    listener = ((ProgressInputStream) inputFile).getListener();
                }
                publishProgress(listener, ProgressEventType.UPLOAD_STARTED, fileSize);
                sendFileContent(inputFile, fileSize, out);
                publishProgress(listener, ProgressEventType.UPLOAD_COMPLETED);
            } catch (RuntimeException e) {
                publishProgress(listener, ProgressEventType.UPLOAD_FAILED);
                throw e;
            } finally {
                // Close the request stream as well after the request is completed.
                inputFile.close();
            }
        }
    }

    /**
     * 接收这里只能确切知道报文头，报文内容(参数+文件)只能靠接收对象分析
     *
     * @param in
     * @return
     * @throws IOException
     */
    protected T receive(InputStream in, Charset charset) throws IOException {

        // 解析报文头
        ProtoHead head = ProtoHead.createFromInputStream(in);
        LOGGER.debug("服务端返回报文头{}", head);
        // 校验报文头
        head.validateResponseHead();

        // 解析报文体
        return response.decode(head, in, charset);

    }

    /**
     * 发送文件
     *
     * @param ins
     * @param size
     * @param ous
     * @throws IOException
     */
    protected void sendFileContent(InputStream ins, long size, OutputStream ous) throws IOException {
        int l;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        if (size < 0) {
            // consume until EOF
            while ((l = ins.read(buffer)) != -1) {
                ous.write(buffer, 0, l);
            }
        } else {
            long remaining = size;
            while (remaining > 0) {
                l = ins.read(buffer, 0, (int) Math.min(DEFAULT_BUFFER_SIZE, remaining));
                if (l == -1) {
                    throw new IOException("the end of the stream has been reached. not match the expected size ");
                }
                ous.write(buffer, 0, l);
                remaining -= l;
            }
        }
    }

}
