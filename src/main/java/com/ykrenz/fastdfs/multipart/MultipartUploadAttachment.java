package com.ykrenz.fastdfs.multipart;

import java.io.Serializable;
import java.util.Objects;

public class MultipartUploadAttachment implements Serializable {
    private final long fileSize;
    private final long partSize;
    private final long partCount;

    public MultipartUploadAttachment(long fileSize, long partSize) {
        this.fileSize = fileSize;
        this.partSize = partSize;
        if (fileSize == 0) {
            this.partCount = 1L;
        } else if (fileSize > 0 && partSize > 0) {
            this.partCount = (long) Math.ceil((double) fileSize / partSize);
        } else {
            // 无效
            this.partCount = -1L;
        }
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getPartSize() {
        return partSize;
    }

    public long getPartCount() {
        return partCount;
    }

    public long[] offset(int partNumber) {
        long offset = (partNumber - 1) * partSize;
        if (partNumber < partCount) {
            return new long[]{offset, partSize};
        }
        return new long[]{offset, fileSize - offset};
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultipartUploadAttachment that = (MultipartUploadAttachment) o;
        return fileSize == that.fileSize && partSize == that.partSize && partCount == that.partCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileSize, partSize, partCount);
    }
}
