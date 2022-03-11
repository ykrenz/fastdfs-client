package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.exception.FdfsIOException;
import com.ykrenz.fastdfs.model.fdfs.FastDFSConstants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public final class InputStreamWrapper {

    public static InputStream wrap(InputStream is) {
        if (is instanceof FileInputStream) {
            return is;
        }
        return new BufferedInputStream(is, FastDFSConstants.DEFAULT_STREAM_BUFFER_SIZE);
    }

    public static InputStream wrap(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new FdfsIOException(e);
        }
    }
}