package com.ykren.fastdfs.conn;

import com.ykren.fastdfs.exception.FdfsClientException;
import com.ykren.fastdfs.model.proto.FdfsCommand;

import java.net.InetSocketAddress;

public abstract class RetryStrategy {

    public abstract <T> boolean shouldRetry(Exception e, InetSocketAddress address, FdfsCommand<T> command, int retries);

    public void delay(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new FdfsClientException(e.getMessage(), e);
        }
    }
}
