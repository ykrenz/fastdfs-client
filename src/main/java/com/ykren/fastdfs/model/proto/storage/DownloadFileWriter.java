package com.ykren.fastdfs.model.proto.storage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件下载回调方法
 *
 * @author tobato
 */
public class DownloadFileWriter implements DownloadCallback<String> {

    /**
     * 日志
     */
    protected static Logger LOGGER = LoggerFactory.getLogger(DownloadFileWriter.class);

    /**
     * 文件名称
     */
    private String fileName;

    public DownloadFileWriter(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 文件接收处理
     */
    @Override
    public String recv(InputStream ins) throws IOException {
        try (InputStream in = new BufferedInputStream(ins);
             FileOutputStream out = FileUtils.openOutputStream(new File(fileName))) {
            // 通过ioutil 对接输入输出流，实现文件下载
            IOUtils.copy(in, out);
            out.flush();
        }
        return fileName;
    }

}
