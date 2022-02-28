package com.ykrenz.fastdfs.event;

/**
 * 进度监听器
 *
 * @author ykren
 * @date 2022/1/28
 */
public interface ProgressListener {
    final ProgressListener NOOP = new ProgressListener() {
        @Override
        public void progressChanged(ProgressEvent progressEvent) {
        }
    };

    /**
     * 监听事件
     *
     * @param progressEvent
     */
    void progressChanged(ProgressEvent progressEvent);
}
