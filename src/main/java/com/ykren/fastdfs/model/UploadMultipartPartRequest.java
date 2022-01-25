package com.ykren.fastdfs.model;

import java.io.InputStream;

/**
 * @author ykren
 * @date 2022/1/25
 */
public class UploadMultipartPartRequest extends AbstractGroupPathArgs {

    private InputStream is;
    private long fileSize;
    private int partNumber;
    private long partSize;
}
