package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.common.Crc32;
import com.ykrenz.fastdfs.config.FastDfsConfiguration;
import com.ykrenz.fastdfs.event.UploadProgressListener;
import com.ykrenz.fastdfs.model.AppendFileRequest;
import com.ykrenz.fastdfs.model.UploadFileRequest;
import com.ykrenz.fastdfs.model.fdfs.FileInfo;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import com.ykrenz.fastdfs.model.proto.storage.DownloadByteArray;
import com.ykrenz.fastdfs.model.proto.storage.DownloadFileWriter;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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
        int length = 100;
        RandomTextFile file = new RandomTextFile(length);
        try {
            FileUtils.copyToFile(file.getInputStream(), sampleFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final List<String> trackerServers = Arrays.asList("192.168.100.200:22122", "192.168.100.201:22122");

    public static void main(String[] args) {
        FastDfs fastDfs = fastDfs();
        fastDfs.shutdown();
        fastDfs = configDfs();
        fastDfs.shutdown();

        StorePath storePath;

        storePath = uploadLocalFile();
        delete(storePath);

        storePath = uploadStream();
        delete(storePath);

        storePath = uploadFileWithMetaData();
        delete(storePath);

        storePath = uploadFileProgress();
        delete(storePath);

        storePath = uploadAppendFile();
        delete(storePath);

        storePath = uploadMultipart();
        delete(storePath);

        downLoadFile();
        path();
    }

    private static void delete(StorePath path) {
        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers);
        fastDfs.deleteFile(path.getGroup(), path.getPath());
        fastDfs.shutdown();
    }

    public static void path() {
        // 配置构建
        String httpUrl = "http://192.168.100.200:8888";
        FastDfsConfiguration configuration = new FastDfsConfiguration();
//        configuration.setDefaultGroup("group1");
        configuration.getHttp().getWebServers().add(httpUrl);
        configuration.getHttp().setUrlHaveGroup(true);
//        configuration.getHttp().setHttpAntiStealToken(true);
//        configuration.getHttp().setSecretKey("FastDFS1234567890");

        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers, configuration);
        StorePath storePath = fastDfs.uploadFile(sampleFile);
        fastDfs.shutdown();
        System.out.println("上传文件成功" + storePath);

        // 配合fastdfs-nginx-module 支持token防盗链 具体查看http配置
        String webPath1 = fastDfs.accessUrl(storePath.getGroup(), storePath.getPath());
        String webPath2 = fastDfs.accessUrl(storePath.getGroup(), storePath.getPath());
        System.out.println("web访问路径1 " + webPath1);
        System.out.println("web访问路径2 " + webPath2);

        String downLoadPath1 = fastDfs.downLoadUrl(storePath.getGroup(), storePath.getPath(), sampleFile.getName());
        System.out.println("web下载路径1 " + downLoadPath1);
        String downLoadPath2 = fastDfs.downLoadUrl(storePath.getGroup(), storePath.getPath(), "attachment", sampleFile.getName());
        System.out.println("web下载路径自定义参数名2 " + downLoadPath2);
        fastDfs.shutdown();
    }

    public static void downLoadFile() {
        StorePath storePath = uploadLocalFile();
        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers);
        //本地文件
        fastDfs.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadFileWriter("tmp/test.txt"));
        //bytes
        byte[] bytes = fastDfs.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
        System.out.println(bytes.length);
        //下载文件片段
        fastDfs.downloadFile(storePath.getGroup(), storePath.getPath(), 10, 10, new DownloadFileWriter("tmp/test_10_1024.txt"));

        //OutputStream 例如web下载 默认构造会自动关闭OutputStream
//        OutputStream ous = response.getOutputStream();
//        fastDFS.downloadFile(request, new DownloadOutputStream(ous));
        delete(storePath);
        fastDfs.shutdown();
    }

    public static StorePath uploadMultipart() {
        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers);
        try {
            final long partSize = 5 * 1024 * 1024L;   // 5MB
            long fileSize = sampleFile.length();
            long partCount = fileSize > 0 ? (long) Math.ceil((double) fileSize / partSize) : 1;

            StorePath storePath = fastDfs.initMultipartUpload(sampleFile.length(), partSize, "txt");
            System.out.println("初始化分片成功" + storePath);
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            for (int i = 1; i <= partCount; i++) {
                long startPos = (i - 1) * partSize;
                InputStream ins = new FileInputStream(sampleFile);
                ins.skip(startPos);
                int partNumber = i;
                executorService.execute(() -> {
                    fastDfs.uploadMultipart(storePath.getGroup(), storePath.getPath(), ins, partNumber);
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

            FileInfo fileInfo1 = fastDfs.queryFileInfo(storePath.getGroup(), storePath.getPath());
            System.out.println(fileInfo1);

            // 6.0.2版本以上支持regenerate=true
            StorePath path = fastDfs.completeMultipartUpload(storePath.getGroup(), storePath.getPath(), true);

            // crc32校验
            FileInfo fileInfo = fastDfs.queryFileInfo(path.getGroup(), path.getPath());
            Assert.assertEquals(crc32, Crc32.convertUnsigned(fileInfo.getCrc32()));
            System.out.println("上传文件成功" + path);
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            fastDfs.shutdown();
        }
    }

    public static StorePath uploadAppendFile() {
        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers);
        StorePath storePath = null;
        for (int i = 1; i < 10; i++) {
            String appendStr = String.valueOf(i);
            if (i == 1) {
                storePath = fastDfs.uploadAppenderFile(new ByteArrayInputStream(appendStr.getBytes()), appendStr.length(), "txt");
            } else {
                AppendFileRequest appendRequest = AppendFileRequest.builder()
                        .groupName(storePath.getGroup())
                        .path(storePath.getPath())
                        .stream(new ByteArrayInputStream(appendStr.getBytes()), appendStr.length())
                        .build();
                fastDfs.appendFile(appendRequest);
            }
        }
        // 修改为普通文件 6.0.2版本以上支持该特性
        StorePath reStorePath = fastDfs.regenerateAppenderFile(storePath.getGroup(), storePath.getPath());
        System.out.println("上传Append文件成功" + reStorePath);
        fastDfs.shutdown();
        return reStorePath;
    }

    public static StorePath uploadFileProgress() {
        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers);
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
        StorePath storePath = fastDfs.uploadFile(fileRequest);
        System.out.println("上传文件成功" + storePath);
        fastDfs.shutdown();
        return storePath;
    }

    public static StorePath uploadFileWithMetaData() {
        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers);
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .file(sampleFile)
                .metaData("MetaKey", "MetaValue")
                .build();
        StorePath storePath = fastDfs.uploadFile(fileRequest);
        System.out.println("上传文件成功" + storePath);
        fastDfs.shutdown();
        return storePath;
    }

    public static StorePath uploadStream() {
        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers);
        String str = "123";
        StorePath storePath = fastDfs.uploadFile(new ByteArrayInputStream(str.getBytes()), str.length(), "txt");
        System.out.println("上传文件成功" + storePath);
        fastDfs.shutdown();
        return storePath;
    }

    private static StorePath uploadLocalFile() {
        FastDfs fastDfs = new FastDfsClientBuilder().build(trackerServers);
        StorePath storePath = fastDfs.uploadFile(sampleFile);
        fastDfs.shutdown();
        System.out.println("上传文件成功" + storePath);
        return storePath;
    }

    public static FastDfs fastDfs() {
        // 默认配置构建
        return new FastDfsClientBuilder().build(trackerServers);
    }

    public static FastDfs configDfs() {
        // 配置构建
        FastDfsConfiguration configuration = new FastDfsConfiguration();
        configuration.setDefaultGroup("group1");
        configuration.getHttp().getWebServers().add("http://192.168.100.200:8888");
        configuration.getHttp().setUrlHaveGroup(true);
        configuration.getHttp().setHttpAntiStealToken(true);
        configuration.getHttp().setSecretKey("FastDFS1234567890");
        return new FastDfsClientBuilder().build(trackerServers, configuration);
    }

}
