package com.ykren.fastdfs.event;

import java.io.InputStream;

import static com.ykren.fastdfs.event.ProgressPublisher.publishUploadIng;

class RequestProgressInputStream extends ProgressInputStream {

    public RequestProgressInputStream(InputStream is, ProgressListener listener) {
        super(is, listener);
    }

    @Override
    protected void onEOF() {
        onNotifyBytesRead();
    }

    @Override
    protected void onNotifyBytesRead() {
        publishUploadIng(getListener(), getUnnotifiedByteCount());
    }
}