package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.multipart.DefaultMultipartAttachmentAccessor;
import com.ykrenz.fastdfs.multipart.MultipartAttachmentAccessor;
import com.ykrenz.fastdfs.multipart.MultipartUploadAttachment;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import org.junit.Assert;
import org.junit.Test;

public class MultipartAttachmentAccessorTest extends BaseClientTest {


    @Test
    public void testPut() {
        MultipartAttachmentAccessor attachmentAccessor = new DefaultMultipartAttachmentAccessor(fastDFS);
        StorePath storePath = uploadRandomFile();
        MultipartUploadAttachment attachment = new MultipartUploadAttachment(1024, 1024);
        attachmentAccessor.put(storePath.getGroup(), storePath.getPath(), attachment);
        MultipartUploadAttachment cacheAttachment = attachmentAccessor.get(storePath.getGroup(), storePath.getPath());
        Assert.assertEquals(attachment, cacheAttachment);

        attachmentAccessor.remove(storePath.getGroup(), storePath.getPath());
        cacheAttachment = attachmentAccessor.get(storePath.getGroup(), storePath.getPath());
        Assert.assertNull(cacheAttachment);
        delete(storePath);
    }

//    @Test
//    public void testLRUCache() {
//        DefaultMultipartAttachmentAccessor attachmentAccessor = new DefaultMultipartAttachmentAccessor(fastDFS);
//        attachmentAccessor.setCache(new LRUCache<>(1));
//
//        MultipartUploadAttachment attachment = new MultipartUploadAttachment(1024, 1024);
//        StorePath storePath1 = uploadRandomFile();
//        StorePath storePath2 = uploadRandomFile();
//        StorePath storePath3 = uploadRandomFile();
//
//        String group1 = storePath1.getGroup(), path1 = storePath1.getPath(), cacheKey1 = createCacheKey(group1, path1);
//        String group2 = storePath2.getGroup(), path2 = storePath2.getPath(), cacheKey2 = createCacheKey(group2, path2);
//        String group3 = storePath3.getGroup(), path3 = storePath3.getPath(), cacheKey3 = createCacheKey(group3, path3);
//
//        attachmentAccessor.put(group1, path1, attachment);
//        MultipartUploadAttachment cache1 = attachmentAccessor.getCache().get(cacheKey1);
//        Assert.assertNotNull(cache1);
//        Assert.assertNotNull(attachmentAccessor.get(group1, path1));
//
//        attachmentAccessor.put(group2, path2, attachment);
//        MultipartUploadAttachment cache2 = attachmentAccessor.getCache().get(cacheKey2);
//        Assert.assertNotNull(cache2);
//        cache1 = attachmentAccessor.getCache().get(cacheKey1);
//        Assert.assertNull(cache1);
//
//        cache1 = attachmentAccessor.get(group1, path1);
//        Assert.assertNotNull(cache1);
//        cache1 = attachmentAccessor.getCache().get(cacheKey1);
//        Assert.assertNotNull(cache1);
//
//        attachmentAccessor.put(group3, path3, attachment);
//        MultipartUploadAttachment cache3 = attachmentAccessor.getCache().get(cacheKey3);
//        Assert.assertNotNull(cache3);
//        Assert.assertNotNull(attachmentAccessor.get(group3, path3));
//        cache1 = attachmentAccessor.getCache().get(cacheKey1);
//        cache2 = attachmentAccessor.getCache().get(cacheKey2);
//        Assert.assertNull(cache1);
//        Assert.assertNull(cache2);
//
//        delete(storePath1);
//        delete(storePath2);
//        delete(storePath3);
//    }

    private String createCacheKey(String groupName, String path) {
        return groupName + "/" + path;
    }

}