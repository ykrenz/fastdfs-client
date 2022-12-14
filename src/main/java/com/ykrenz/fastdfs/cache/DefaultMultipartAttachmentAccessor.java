package com.ykrenz.fastdfs.cache;

import com.ykrenz.fastdfs.FastDfs;
import com.ykrenz.fastdfs.model.UploadSalveFileRequest;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayInputStream;
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

    private static final String ATTACHMENT_METADATA_KEY = "MultipartAttachment";

    private static final String ATTACHMENT_PREFIX_KEY = "_att";

    private void setAttachmentMetaData(String groupName, String path, MultipartUploadAttachment attachment) {
        String attachmentValue = createValue(attachment);
        UploadSalveFileRequest attachmentRequest = UploadSalveFileRequest.builder()
                .groupName(groupName).masterPath(path)
                .prefix(ATTACHMENT_PREFIX_KEY)
                .stream(new ByteArrayInputStream(new byte[0]), 0, "")
                .metaData(ATTACHMENT_METADATA_KEY, attachmentValue).build();
        fastDfs.uploadSlaveFile(attachmentRequest);
    }

    private MultipartUploadAttachment getFromMetaData(String groupName, String path) {
        MetaData attachmentMetaData = getAttachmentMetaData(groupName, path);
        return attachmentMetaData == null ? null : parseValue(attachmentMetaData.getValue());
    }

    private MetaData getAttachmentMetaData(String groupName, String path) {
        Set<MetaData> metadata = fastDfs.getMetadata(groupName, getAttPath(path));
        for (MetaData metaData : metadata) {
            if (ATTACHMENT_METADATA_KEY.equals(metaData.getName())) {
                return metaData;
            }
        }
        return null;
    }

    private String getAttPath(String masterPath) {
        return FilenameUtils.removeExtension(masterPath) + ATTACHMENT_PREFIX_KEY;
    }


    private void setAttachmentCache(String groupName, String path, MultipartUploadAttachment attachment) {
        cache.put(createCacheKey(groupName, path), attachment);
    }


    @Override
    public void remove(String groupName, String path) {
        fastDfs.deleteFile(groupName, getAttPath(path));
        cache.remove(createCacheKey(groupName, path));
    }

    private static final String delimiter = "_";

    private String createCacheKey(String groupName, String path) {
        return groupName + delimiter + path;
    }

    private String createValue(MultipartUploadAttachment attachment) {
        return attachment.getFileSize() + delimiter + attachment.getPartSize();
    }

    private MultipartUploadAttachment parseValue(String value) {
        String[] arr = value.split(delimiter);
        return new MultipartUploadAttachment(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
    }

}
