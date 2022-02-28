package com.ykrenz.fastdfs.event;

import java.io.InputStream;

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
        ProgressPublisher.publishUploadIng(getListener(), getUnnotifiedByteCount());
    }
}