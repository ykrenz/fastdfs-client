package com.ykrenz.fastdfs.multipart;

import com.ykrenz.fastdfs.FastDfs;
import com.ykrenz.fastdfs.cache.FdfsCache;
import com.ykrenz.fastdfs.cache.LRUCache;

/**
 * 默认访问器
 */
public class DefaultMultipartAttachmentAccessor implements MultipartAttachmentAccessor {

    private final MultipartAttachmentAccessor delegate;

    private final FdfsCache<String, MultipartUploadAttachment> fc = new LRUCache<>(1024);

    public DefaultMultipartAttachmentAccessor(FastDfs fastDfs) {
        this.delegate = new FastDfsMetaAccessor(fastDfs);
    }

    @Override
    public void put(String groupName, String path, MultipartUploadAttachment attachment) {
        delegate.put(groupName, path, attachment);
        putCache(groupName, path, attachment);
    }

    @Override
    public MultipartUploadAttachment get(String groupName, String path) {
        MultipartUploadAttachment attachment = fc.get(cacheKey(groupName, path));
        if (attachment != null) {
            return attachment;
        }

        attachment = delegate.get(groupName, path);
        if (attachment != null) {
            putCache(groupName, path, attachment);
        }
        return attachment;
    }

    private void putCache(String groupName, String path, MultipartUploadAttachment attachment) {
        fc.put(cacheKey(groupName, path), attachment);
    }


    @Override
    public void remove(String groupName, String path) {
        delegate.remove(groupName, path);
        fc.remove(cacheKey(groupName, path));
    }

    private String cacheKey(String groupName, String path) {
        return groupName + "/" + path;
    }

}
