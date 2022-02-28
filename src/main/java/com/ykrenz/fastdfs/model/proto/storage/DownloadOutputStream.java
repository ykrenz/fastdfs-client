package com.ykrenz.fastdfs.model.proto.storage;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Web环境下文件下载回调方法,默认按4K循环读取，防止下载时内存溢出
 * <pre>
 *
 * refactor:
 * 将HttpServletResponse调整为OutputStream对象，
 * 注意：使用时候在外层做response.getOutputStream()，使用完毕后，在外层做 os.close()
 * 如：
 *  os = response.getOutputStream();
 *  DownloadFileStream stream = new DownloadFileStream(os);
 *  ...
 *  os.close();
 *
 * </pre>
 *
 * @author xulb
 */
public class DownloadOutputStream implements DownloadCallback<Void> {

    /**
     * 默认缓存长度
     */
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    /**
     * 输出流
     * HttpServletResponse对象response.getOutputStream()
     */
    private OutputStream outputStream;
    /**
     * 默认缓存长度
     */
    private int bufferLength;
    /**
     * 关闭流
     */
    private boolean close;


    /**
     * 从HttpServletResponse对象response.getOutputStream()构造
     *
     * @param responseOutputStream 输出流
     */
    public DownloadOutputStream(OutputStream responseOutputStream) {
        this(responseOutputStream, DEFAULT_BUFFER_SIZE, true);
    }

    /**
     * 从HttpServletResponse对象response.getOutputStream()构造
     *
     * @param responseOutputStream 输出流
     * @param bufferLength         缓存长度
     */
    public DownloadOutputStream(OutputStream responseOutputStream, int bufferLength) {
        this(responseOutputStream, bufferLength, true);
    }

    public DownloadOutputStream(OutputStream responseOutputStream, boolean close) {
        this(responseOutputStream, DEFAULT_BUFFER_SIZE, close);
    }

    public DownloadOutputStream(OutputStream outputStream, int bufferLength, boolean close) {
        this.outputStream = outputStream;
        this.bufferLength = bufferLength;
        this.close = close;
    }

    /**
     * 文件接收处理
     *
     * @return
     */
    @Override
    public Void recv(InputStream ins) throws IOException {
        // 实现文件下载
        byte[] buffer = new byte[bufferLength];
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(ins);) {
            IOUtils.copyLarge(bufferedInputStream, outputStream, buffer);
        } finally {
            if (close) {
                outputStream.close();
            }
        }
        return null;
    }
}
