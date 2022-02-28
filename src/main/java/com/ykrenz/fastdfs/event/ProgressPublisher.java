package com.ykrenz.fastdfs.event;

import static com.ykrenz.fastdfs.event.ProgressEventType.UPLOADING;

/**
 * ProgressPublisher
 *
 * @author ykren
 * @date 2022/1/28
 */
public class ProgressPublisher {

    public static void publishProgress(final ProgressListener listener, final ProgressEventType eventType) {
        if (listener == ProgressListener.NOOP || listener == null || eventType == null) {
            return;
        }
        listener.progressChanged(new ProgressEvent(eventType));
    }

    /**
     * 发布监听任务
     *
     * @param listener
     * @param eventType
     * @param scannedBytes
     */
    public static void publishProgress(final ProgressListener listener, final ProgressEventType eventType, final long scannedBytes) {
        if (listener == ProgressListener.NOOP || listener == null || eventType == null) {
            return;
        }
        listener.progressChanged(new ProgressEvent(eventType, scannedBytes));
    }

    public static void publishUploadIng(final ProgressListener listener, final long bytes) {
        publishByteCountEvent(listener, UPLOADING, bytes);
    }

    private static void publishByteCountEvent(final ProgressListener listener, final ProgressEventType eventType,
                                              final long bytes) {
        if (listener == ProgressListener.NOOP || listener == null || bytes <= 0) {
            return;
        }
        listener.progressChanged(new ProgressEvent(eventType, bytes));
    }

}