package com.ykrenz.fastdfs.multipart;

/**
 * 分片信息访问器
 */
public interface MultipartAttachmentAccessor {

    void put(String groupName, String path, MultipartUploadAttachment attachment);

    MultipartUploadAttachment get(String groupName, String path);

    void remove(String groupName, String path);
}
