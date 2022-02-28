package com.ykrenz.fastdfs.event;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class ProgressInputStream extends FilterInputStream {

    public static InputStream inputStreamForRequest(InputStream is, ProgressListener listener) {
        return listener == null ? is : new RequestProgressInputStream(is, listener);
    }

    private static final int DEFAULT_NOTIFICATION_THRESHOLD = 8 * 1024;

    private final ProgressListener listener;
    private final int notifyThresHold;
    private int unnotifiedByteCount;
    private boolean hasBeenRead;
    private boolean doneEOF;
    private long notifiedByteCount;

    public ProgressInputStream(InputStream is, ProgressListener listener) {
        this(is, listener, DEFAULT_NOTIFICATION_THRESHOLD);
    }

    public ProgressInputStream(InputStream is, ProgressListener listener, int notifyThresHold) {
        super(is);
        if (is == null || listener == null) {
            throw new IllegalArgumentException();
        }
        this.listener = listener;
        this.notifyThresHold = notifyThresHold;
    }

    protected void onFirstRead() {
    }

    protected void onEOF() {
    }

    protected void onClose() {
        eof();
    }

    protected void onReset() {
    }

    protected void onNotifyBytesRead() {
    }

    private void onBytesRead(int bytesRead) {
        unnotifiedByteCount += bytesRead;
        if (unnotifiedByteCount >= notifyThresHold) {
            onNotifyBytesRead();
            notifiedByteCount += unnotifiedByteCount;
            unnotifiedByteCount = 0;
        }
    }

    @Override
    public int read() throws IOException {
        if (!hasBeenRead) {
            onFirstRead();
            hasBeenRead = true;
        }
        int ch = super.read();
        if (ch == -1)
            eof();
        else
            onBytesRead(1);
        return ch;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        onReset();
        unnotifiedByteCount = 0;
        notifiedByteCount = 0;
    }

    @Override
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (!hasBeenRead) {
            onFirstRead();
            hasBeenRead = true;
        }
        int bytesRead = super.read(b, off, len);
        if (bytesRead == -1)
            eof();
        else
            onBytesRead(bytesRead);
        return bytesRead;
    }

    private void eof() {
        if (doneEOF)
            return;
        onEOF();
        unnotifiedByteCount = 0;
        doneEOF = true;
    }

    public final InputStream getWrappedInputStream() {
        return in;
    }

    protected final int getUnnotifiedByteCount() {
        return unnotifiedByteCount;
    }

    protected final long getNotifiedByteCount() {
        return notifiedByteCount;
    }

    @Override
    public void close() throws IOException {
        onClose();
        super.close();
    }

    public final ProgressListener getListener() {
        return listener;
    }
}
