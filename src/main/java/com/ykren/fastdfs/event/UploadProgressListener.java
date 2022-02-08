package com.ykren.fastdfs.event;

/**
 * 上传进度条监听器
 *
 * @author ykren
 * @date 2022/1/28
 */
public abstract class UploadProgressListener implements ProgressListener {

    protected long bytesWritten = 0;
    protected long totalBytes = -1;

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case UPLOAD_STARTED:
                this.totalBytes = bytes;
                start();
                break;
            case UPLOADING:
                this.bytesWritten += bytes;
                uploading();
                break;
            case UPLOAD_COMPLETED:
                completed();
                break;
            case UPLOAD_FAILED:
                failed();
                break;
            default:
                break;
        }
    }

    /**
     * 获取上传进度百分比
     *
     * @return
     */
    public int percent() {
        if (totalBytes < 0) {
            return -1;
        }
        return (int) (this.bytesWritten * 100.0 / this.totalBytes);
    }

    /**
     * 开始
     */
    public abstract void start();

    /**
     * 上传中
     */
    public abstract void uploading();

    /**
     * 上传完毕
     */
    public abstract void completed();

    /**
     * 上传失败
     */
    public abstract void failed();

}