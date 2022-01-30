package com.ykren.fastdfs.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 上传进度条监听器
 *
 * @author ykren
 * @date 2022/1/28
 */
public abstract class UploadProgressListener implements ProgressListener {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadProgressListener.class);

    private long bytesWritten = 0;
    private long totalBytes = -1;

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case UPLOAD_STARTED:
                this.totalBytes = bytes;
                start(totalBytes);
                break;
            case UPLOADING:
                this.bytesWritten += bytes;
                uploading(totalBytes, bytesWritten);
                break;
            case UPLOAD_COMPLETED:
                completed(totalBytes, bytesWritten);
                break;
            case UPLOAD_FAILED:
                failed(totalBytes, bytesWritten);
                break;
            default:
                break;
        }
    }

    /**
     * 开始
     *
     * @param totalBytes
     */
    public abstract void start(long totalBytes);

    /**
     * 上传中
     *
     * @param totalBytes
     * @param bytesWritten
     */
    public abstract void uploading(long totalBytes, long bytesWritten);

    /**
     * 上传完毕
     *
     * @param totalBytes
     * @param bytesWritten
     */
    public abstract void completed(long totalBytes, long bytesWritten);

    /**
     * 上传失败
     *
     * @param totalBytes
     * @param bytesWritten
     */
    public abstract void failed(long totalBytes, long bytesWritten);

}