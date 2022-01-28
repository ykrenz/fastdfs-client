package com.ykren.fastdfs.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 上传进度条监听器
 *
 * @author ykren
 * @date 2022/1/28
 */
public class UploadProgressListener implements ProgressListener {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadProgressListener.class);

    private long bytesWritten = 0;
    private long totalBytes = -1;
    private boolean succeed = false;

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                LOGGER.debug("Start to upload......");
                this.totalBytes = bytes;
                break;
            case REQUEST_BYTE_TRANSFER_EVENT:
                this.bytesWritten += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesWritten * 100.0 / this.totalBytes);
                    LOGGER.debug(bytes + " bytes have been written at this time, upload progress: " +
                            percent + "%(" + this.bytesWritten + "/" + this.totalBytes + ")");
                } else {
                    LOGGER.debug(bytes + " bytes have been written at this time, upload ratio: unknown" +
                            "(" + this.bytesWritten + "/...)");
                }
                break;
            case TRANSFER_COMPLETED_EVENT:
                this.succeed = true;
                LOGGER.debug("Succeed to upload, " + this.bytesWritten + " bytes have been transferred in total");
                break;
            case TRANSFER_FAILED_EVENT:
                LOGGER.debug("Failed to upload, " + this.bytesWritten + " bytes have been transferred");
                break;
            default:
                break;
        }
    }

    public boolean isSucceed() {
        return succeed;
    }
}