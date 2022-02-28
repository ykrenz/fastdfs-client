
package com.ykrenz.fastdfs.event;

/**
 * 进度事件类型
 */
public enum ProgressEventType {
    /**
     * 开始上传
     */
    UPLOAD_STARTED,
    /**
     * 上传中
     */
    UPLOADING,
    /**
     * 上传完毕
     */
    UPLOAD_COMPLETED,
    /**
     * 上传失败
     */
    UPLOAD_FAILED
}
