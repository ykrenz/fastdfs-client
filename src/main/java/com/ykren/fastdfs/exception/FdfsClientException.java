package com.ykren.fastdfs.exception;

/**
 * 客户端异常 非fastdfs异常
 *
 * @author yuqihuang
 * @author tobato
 */
public class FdfsClientException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     */
    public FdfsClientException(String message) {
        super(message);
    }

    /**
     * @param message
     */
    public FdfsClientException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * @param t
     */
    public FdfsClientException(Throwable t) {
        super("客户端出现异常", t);
    }


}
