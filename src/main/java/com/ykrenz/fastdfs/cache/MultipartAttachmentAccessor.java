package com.ykrenz.fastdfs.cache;

public interface MultipartAttachmentAccessor {

    void put(String groupName, String path, MultipartUploadAttachment attachment);

    MultipartUploadAttachment get(String groupName, String path);

    void remove(String groupName, String path);
}
