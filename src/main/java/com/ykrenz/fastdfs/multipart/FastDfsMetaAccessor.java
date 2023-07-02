package com.ykrenz.fastdfs.multipart;

import com.ykrenz.fastdfs.FastDfs;
import com.ykrenz.fastdfs.model.UploadSalveFileRequest;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayInputStream;
import java.util.Set;

/**
 * 使用fastdfs meta存储
 */
public class FastDfsMetaAccessor implements MultipartAttachmentAccessor {

    private final FastDfs fastDfs;

    private static final String ATTACHMENT_METADATA_KEY = "MultipartAttachment";

    private static final String ATTACHMENT_PREFIX_KEY = "_att";

    private static final String DELIMITER = "_";

    public FastDfsMetaAccessor(FastDfs fastDfs) {
        this.fastDfs = fastDfs;
    }

    @Override
    public void put(String groupName, String path, MultipartUploadAttachment attachment) {
        String attachmentValue = createMeta(attachment);
        UploadSalveFileRequest attachmentRequest = UploadSalveFileRequest.builder()
                .groupName(groupName).masterPath(path)
                .prefix(ATTACHMENT_PREFIX_KEY)
                .stream(new ByteArrayInputStream(new byte[0]), 0, "")
                .metaData(ATTACHMENT_METADATA_KEY, attachmentValue).build();
        fastDfs.uploadSlaveFile(attachmentRequest);
    }

    @Override
    public MultipartUploadAttachment get(String groupName, String path) {
        Set<MetaData> metadata = fastDfs.getMetadata(groupName, getAttPath(path));
        for (MetaData metaData : metadata) {
            if (ATTACHMENT_METADATA_KEY.equals(metaData.getName())) {
                return parseMeta(metaData.getValue());
            }
        }
        return null;
    }

    private String getAttPath(String masterPath) {
        return FilenameUtils.removeExtension(masterPath) + ATTACHMENT_PREFIX_KEY;
    }

    @Override
    public void remove(String groupName, String path) {
        fastDfs.deleteFile(groupName, getAttPath(path));
    }

    private String createMeta(MultipartUploadAttachment attachment) {
        return attachment.getFileSize() + DELIMITER + attachment.getPartSize();
    }

    private MultipartUploadAttachment parseMeta(String value) {
        String[] arr = value.split(DELIMITER);
        return new MultipartUploadAttachment(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
    }
}
