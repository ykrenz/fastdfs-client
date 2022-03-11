package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.common.Crc32;
import com.ykrenz.fastdfs.config.FastDFSConfiguration;
import com.ykrenz.fastdfs.event.UploadProgressListener;
import com.ykrenz.fastdfs.model.AppendFileRequest;
import com.ykrenz.fastdfs.model.DownloadFileRequest;
import com.ykrenz.fastdfs.model.UploadFileRequest;
import com.ykrenz.fastdfs.model.UploadMultipartPartRequest;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import com.ykrenz.fastdfs.model.proto.storage.DownloadByteArray;
import com.ykrenz.fastdfs.model.proto.storage.DownloadFileWriter;
import com.ykrenz.fastdfs.model.proto.storage.DownloadOutputStream;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author ykren
 * @date 2022/3/11
 */
public class App {

    static File sampleFile = new File("tmp", "sampleFile.txt");

    static {
        int length = 1024 * 1024 * 100; // 100M
        RandomTextFile file = new RandomTextFile(length);
        try {
            FileUtils.copyToFile(file.getInputStream(), sampleFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FastDfs fastDfs = fastDfs();
        fastDfs.shutdown();
//        FastDfs configDfs = configDfs();
//        configDfs.shutdown();
//
//        uploadLocalFile();
//
//        uploadStream();
//
//        uploadFileWithMetaData();
//
//        uploadFileProgress();
//
//        uploadAppendFile();

//        try {
//            uploadMultipart();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        downLoadFile();
    }

    private static void downLoadFile() {
        StorePath storePath = uploadLocalFile();
        List<String> trackerServers = new ArrayList<>();
        trackerServers.add("192.168.24.130:22122");
        FastDfs fastDFS = new FastDfsClientBuilder().build(trackerServers);
        //本地文件
        fastDFS.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadFileWriter("tmp/test.txt"));
        //bytes
        byte[] bytes = fastDFS.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
        System.out.println(bytes.length);
        //下载文件片段
        fastDFS.downloadFile(storePath.getGroup(), storePath.getPath(), 10, 1024, new DownloadFileWriter("tmp/test_10_1024.txt"));

        //OutputStream 例如web下载 默认构造会自动关闭OutputStream
//        OutputStream ous = response.getOutputStream();
//        fastDFS.downloadFile(request, new DownloadOutputStream(ous));
        fastDFS.shutdown();
    }

    private static void uploadMultipart() throws IOException {
        List<String> trackerServers = new ArrayList<>();
        trackerServers.add("192.168.24.130:22122");
        FastDfs fastDFS = new FastDfsClientBuilder().build(trackerServers);
        final long partSize = 5 * 1024 * 1024L;   // 5MB
        long fileSize = sampleFile.length();
        long partCount = fileSize > 0 ? (long) Math.ceil((double) fileSize / partSize) : 1;

        StorePath storePath = fastDFS.initMultipartUpload(sampleFile.length(), "txt");
        System.out.println("初始化分片成功" + storePath);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= partCount; i++) {
            long startPos = (i - 1) * partSize;
            long curPartSize = (i == partCount) ? (fileSize - startPos) : partSize;
            InputStream ins = new FileInputStream(sampleFile);
            ins.skip(startPos);
            int partNumber = i;
            executorService.execute(() -> {
                // offset方式
//                UploadMultipartPartRequest offsetPartRequest = UploadMultipartPartRequest.builder()
//                        .streamOffset(ins, curPartSize, startPos)
//                        .groupName(storePath.getGroup())
//                        .path(storePath.getPath())
//                        .build();
//                fastDFS.uploadMultipart(offsetPartRequest);
                // partSize方式
                UploadMultipartPartRequest partRequest = UploadMultipartPartRequest.builder()
                        .streamPart(ins, curPartSize, partNumber, partSize)
                        .groupName(storePath.getGroup())
                        .path(storePath.getPath())
                        .build();
                fastDFS.uploadMultipart(partRequest);
            });
        }
        /*
         * Waiting for all parts finished
         */
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long crc32 = Crc32.file(sampleFile);
        // 6.0.2版本以上支持regenerate=true
        StorePath path = fastDFS.completeMultipartUpload(storePath.getGroup(), storePath.getPath(), true);

        // crc32校验
        FileInfo fileInfo = fastDFS.queryFileInfo(path.getGroup(), path.getPath());
        Assert.assertEquals(crc32, Crc32.convertUnsigned(fileInfo.getCrc32()));
        System.out.println("上传文件成功" + path);
        fastDFS.shutdown();
    }

    private static void uploadAppendFile() {
        List<String> trackerServers = new ArrayList<>();
        trackerServers.add("192.168.24.130:22122");
        FastDfs fastDFS = new FastDfsClientBuilder().build(trackerServers);
        StorePath storePath = null;
        for (int i = 1; i < 10; i++) {
            String appendStr = String.valueOf(i);
            if (i == 1) {
                storePath = fastDFS.uploadAppenderFile(new ByteArrayInputStream(appendStr.getBytes()), appendStr.length(), "txt");
            } else {
                AppendFileRequest appendRequest = AppendFileRequest.builder()
                        .groupName(storePath.getGroup())
                        .path(storePath.getPath())
                        .stream(new ByteArrayInputStream(appendStr.getBytes()), appendStr.length())
                        .build();
                fastDFS.appendFile(appendRequest);
            }
        }
        // 修改为普通文件 6.0.2版本以上支持该特性
        StorePath reStorePath = fastDFS.regenerateAppenderFile(storePath.getGroup(), storePath.getPath());
        System.out.println("上传Append文件成功" + reStorePath);
        fastDFS.shutdown();
    }

    private static void uploadFileProgress() {
        List<String> trackerServers = new ArrayList<>();
        trackerServers.add("192.168.24.130:22122");
        FastDfs fastDFS = new FastDfsClientBuilder().build(trackerServers);
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .file(sampleFile)
                .listener(new UploadProgressListener() {
                    @Override
                    public void start() {
                        System.out.println("开始上传...文件总大小" + totalBytes);
                    }

                    @Override
                    public void uploading() {
                        System.out.println("上传中 上传进度为" + percent());
                    }

                    @Override
                    public void completed() {
                        System.out.println("上传完成...");
                    }

                    @Override
                    public void failed() {
                        System.out.println("上传失败...已经上传的字节数" + bytesWritten);
                    }
                })
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        System.out.println("上传文件成功" + storePath);
        fastDFS.shutdown();
    }

    private static void uploadFileWithMetaData() {
        List<String> trackerServers = new ArrayList<>();
        trackerServers.add("192.168.24.130:22122");
        FastDfs fastDFS = new FastDfsClientBuilder().build(trackerServers);
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .file(sampleFile)
                .metaData("MetaKey", "MetaValue")
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        System.out.println("上传文件成功" + storePath);
        fastDFS.shutdown();
    }

    private static void uploadStream() {
        List<String> trackerServers = new ArrayList<>();
        trackerServers.add("192.168.24.130:22122");
        FastDfs fastDFS = new FastDfsClientBuilder().build(trackerServers);
        String str = "123";
        StorePath storePath = fastDFS.uploadFile(new ByteArrayInputStream(str.getBytes()), str.length(), "txt");
        System.out.println("上传文件成功" + storePath);
        fastDFS.shutdown();
    }

    public static FastDfs fastDfs() {
        // 默认配置构建
        List<String> trackerServers = new ArrayList<>();
        trackerServers.add("192.168.24.130:22122");
        FastDfs fastDFS = new FastDfsClientBuilder().build(trackerServers);
        return fastDFS;
    }

    public static FastDfs configDfs() {
        // 配置构建
        List<String> trackerServers = new ArrayList<>();
        trackerServers.add("192.168.24.130:22122");
        FastDFSConfiguration configuration = new FastDFSConfiguration();
        configuration.setDefaultGroup("group1");
        configuration.getHttp().setWebServerUrl("http://192.168.24.130:8888");
        configuration.getHttp().setWebServerUrlHasGroup(true);
        configuration.getHttp().setHttpAntiStealToken(true);
        configuration.getHttp().setSecretKey("FastDFS1234567890");
        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers, configuration);
        return fastDfs;
    }

    private static StorePath uploadLocalFile() {
        List<String> trackerServers = new ArrayList<>();
        trackerServers.add("192.168.24.130:22122");
        FastDfs fastDFS = new FastDfsClientBuilder().build(trackerServers);
        StorePath storePath = fastDFS.uploadFile(sampleFile);
        fastDFS.shutdown();
        System.out.println("上传文件成功" + storePath);
        return storePath;
    }
}
