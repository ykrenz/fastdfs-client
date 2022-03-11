package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.model.AppendFileRequest;
import com.ykrenz.fastdfs.model.ModifyFileRequest;
import com.ykrenz.fastdfs.model.UploadFileRequest;
import com.ykrenz.fastdfs.model.UploadSalveFileRequest;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.proto.storage.enums.StorageMetadataSetType;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author ykren
 * @date 2022/1/28
 */
public class ArgsTest {
    File file = new File("tmp", "1.txt");

    @Before
    public void before() throws IOException {
        FileUtils.touch(file);
    }

    @Test
    public void uploadRequest() throws IOException {
        UploadFileRequest request = UploadFileRequest.builder()
//                .file(new File("notExist.txt"))
                .file(file)
                .build();

        Assert.assertNotNull(request);
        Assert.assertNotNull(request.file());
        Assert.assertNotNull(request.fileExtName());
        Assert.assertEquals(0, request.fileSize());

        String s = "123";
        request = UploadFileRequest.builder()
//                .file(new File("notExist.txt"))
                .stream(new ByteArrayInputStream(s.getBytes()), s.length(), "txt")
                .build();
        Assert.assertNotNull(request);
        Assert.assertNotNull(request.stream());
        Assert.assertNotNull(request.fileExtName());
        Assert.assertEquals(3, request.fileSize());

        request = UploadFileRequest.builder()
                .groupName("group1")
//                .file(new File("notExist.txt"))
                .stream(new ByteArrayInputStream(s.getBytes()), s.length(), "txt")
                .metaData("key", "value")
                .build();

        Assert.assertNotNull(request);
        Assert.assertEquals("group1", request.groupName());
        Assert.assertNotNull(request.stream());
        Assert.assertNotNull(request.fileExtName());
        Assert.assertEquals(3, request.fileSize());
        Assert.assertTrue(request.metaData().contains(new MetaData("key", "value")));

        UploadFileRequest.builder()
                .groupName("group1")
                .stream(new ByteArrayInputStream(s.getBytes()), s.length(), "1234567890")
                .metaData("12345678901234567890123456789012345678901234567890123456789012345",
                        "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890" +
                                "123456789012345678901234567890123456789012345678901234567890")
                .build();
    }

    @Test
    public void UploadSalveFileRequest() throws IOException {
        UploadSalveFileRequest.builder()
                //                .file(new File("notExist.txt"))
                .file(file)
                .masterPath("xxxx")
                .prefix("xxx")
                .build();


        UploadSalveFileRequest.builder()
                .file(file)
                .masterPath("xxxx")
                .prefix("12345678901234567890")
                .build();

        UploadSalveFileRequest.builder()
                .stream(new ByteArrayInputStream(new byte[]{}), 0, "txt")
                .masterPath("xxxx")
                .prefix("12345678901234567890")
                .build();
    }

    @Test
    public void ModifyFileRequest() throws IOException {
        ModifyFileRequest.builder()
//                .file(file,-1)
                .file(file, 0)
                .path("xxxx")
                .build();


        ModifyFileRequest.builder()
//                .file(file,-1)
//                .stream(new ByteArrayInputStream(new byte[]{}), -1, 0)
                .stream(new ByteArrayInputStream(new byte[]{}), 0, 0)
                .path("xxxx")
                .build();

        ModifyFileRequest.builder()
//                .file(file,-1)
//                .stream(new ByteArrayInputStream(new byte[]{}), -1, 0)
                .stream(new ByteArrayInputStream(new byte[]{}), 0, 0)
                .path("xxxx")
                .metaData("key", "value", StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE)
                .build();
    }

    @Test
    public void AppendFileRequest() throws IOException {

        AppendFileRequest.builder()
                .file(file)
                .path("xxx")
                .build();


        AppendFileRequest.builder()
                .path("xxx")
//                .stream(new ByteArrayInputStream(new byte[]{}),-1)
                .stream(new ByteArrayInputStream(new byte[]{}), 0)
                .build();

        AppendFileRequest.builder()
                .path("xxx")
                .stream(new ByteArrayInputStream(new byte[]{}), 0)
                .metaData("key", "value", StorageMetadataSetType.STORAGE_SET_METADATA_FLAG_OVERWRITE)
                .build();
    }
}
