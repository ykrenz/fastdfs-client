package com.ykrenz.fastdfs.cache;

import com.ykrenz.fastdfs.FastDfs;
import com.ykrenz.fastdfs.model.MetaDataRequest;
import com.ykrenz.fastdfs.model.fdfs.MetaData;

import java.util.Set;

public class DefaultMultipartAttachmentAccessor implements MultipartAttachmentAccessor {

    private FastDfs fastDfs;

    private FdfsCache<String, MultipartUploadAttachment> cache = new LRUCache<>(1024);

    public DefaultMultipartAttachmentAccessor(FastDfs fastDfs) {
        this.fastDfs = fastDfs;
    }

    public FastDfs getFastDfs() {
        return fastDfs;
    }

    public FdfsCache<String, MultipartUploadAttachment> getCache() {
        return cache;
    }

    public void setFastDfs(FastDfs fastDfs) {
        this.fastDfs = fastDfs;
    }

    public void setCache(FdfsCache<String, MultipartUploadAttachment> cache) {
        this.cache = cache;
    }

    @Override
    public void put(String groupName, String path, MultipartUploadAttachment attachment) {
        setAttachmentMetaData(groupName, path, attachment);
        setAttachmentCache(groupName, path, attachment);
    }

    @Override
    public MultipartUploadAttachment get(String groupName, String path) {
        MultipartUploadAttachment attachment = cache.get(createCacheKey(groupName, path));
        if (attachment != null) {
            return attachment;
        }

        attachment = getFromMetaData(groupName, path);
        if (attachment != null) {
            setAttachmentCache(groupName, path, attachment);
        }
        return attachment;
    }

    private void setAttachmentMetaData(String groupName, String path, MultipartUploadAttachment attachment) {
        fastDfs.mergeMetadata(MetaDataRequest.builder()
                .groupName(groupName).path(path)
                .metaData(createMetaDataKey(groupName, path), createValue(attachment)).build());
    }

    private void setAttachmentCache(String groupName, String path, MultipartUploadAttachment attachment) {
        cache.put(createCacheKey(groupName, path), attachment);
    }


    private MultipartUploadAttachment getFromMetaData(String groupName, String path) {
        MetaData attachmentMetaData = getAttachmentMetaData(groupName, path);
        return attachmentMetaData == null ? null : parseValue(attachmentMetaData.getValue());
    }

    private MetaData getAttachmentMetaData(String groupName, String path) {
        String key = createMetaDataKey(groupName, path);
        Set<MetaData> metadata = fastDfs.getMetadata(groupName, path);
        for (MetaData metaData : metadata) {
            if (key.equals(metaData.getName())) {
                return metaData;
            }
        }
        return null;
    }

    @Override
    public void remove(String groupName, String path) {
        Set<MetaData> metadata = fastDfs.getMetadata(groupName, path);
        String key = createMetaDataKey(groupName, path);
        metadata.removeIf(meta -> key.equals(meta.getName()));
        fastDfs.overwriteMetadata(MetaDataRequest.builder()
                .groupName(groupName).path(path)
                .metaData(metadata).build());
        cache.remove(createCacheKey(groupName, path));
    }

    private static final String metaDataKey = "MultipartAttachment";
    private static final String delimiter = "_";

    private String createCacheKey(String groupName, String path) {
        return groupName + delimiter + path;
    }

    private String createMetaDataKey(String groupName, String path) {
        return metaDataKey;
    }

    private String createValue(MultipartUploadAttachment attachment) {
        return attachment.getFileSize() + delimiter + attachment.getPartSize();
    }

    private MultipartUploadAttachment parseValue(String value) {
        String[] arr = value.split(delimiter);
        return new MultipartUploadAttachment(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
    }

}
