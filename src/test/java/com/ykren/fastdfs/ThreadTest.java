package com.ykren.fastdfs;

import com.ykren.fastdfs.model.DownloadFileRequest;
import com.ykren.fastdfs.model.FileInfoRequest;
import com.ykren.fastdfs.model.UploadFileRequest;
import com.ykren.fastdfs.model.fdfs.StorePath;
import com.ykren.fastdfs.model.proto.storage.DownloadByteArray;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author ykren
 * @date 2022/2/10
 */
public class ThreadTest extends BaseClientTest {

    @Test
    public void uploadThreadTest() throws InterruptedException, ExecutionException {
        int thread = 500;
        ExecutorService service = Executors.newFixedThreadPool(thread);
        FastDFS fastDFS = new FastDFSClientBuilder().build(TRACKER_LIST);
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < thread; i++) {
            tasks.add(new uploadTask(fastDFS));
        }
        List<Future<Void>> futures = service.invokeAll(tasks);
        for (Future f : futures) {
            f.get();
        }
        fastDFS.close();
    }


    class uploadTask implements Callable<Void> {

        FastDFS fastDFS;

        public uploadTask(FastDFS fastDFS) {
            this.fastDFS = fastDFS;
        }

        @Override
        public Void call() throws Exception {
            String data = UUID.randomUUID().toString();
            UploadFileRequest fileRequest = UploadFileRequest.builder()
                    .stream(new ByteArrayInputStream(data.getBytes()), data.length(), "txt")
                    .build();
            StorePath storePath = fastDFS.uploadFile(fileRequest);
            DownloadFileRequest request = DownloadFileRequest.builder()
                    .group(storePath.getGroup())
                    .path(storePath.getPath())
                    .build();
            byte[] bytes = fastDFS.downloadFile(request, new DownloadByteArray());
            FileInfoRequest fileInfoRequest = FileInfoRequest.builder()
                    .group(storePath.getGroup())
                    .path(storePath.getPath())
                    .build();
            if (!data.equals(new String(bytes))) {
                throw new RuntimeException("ssssssssssssss");
            }
            fastDFS.deleteFile(fileInfoRequest);
            return null;
        }
    }
}
