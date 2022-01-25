package com.ykren.fastdfs.model;

import java.util.Objects;

/**
 * 初始化分片上传
 *
 * @author ykren
 * @date 2022/1/25
 */
public class InitMultipartUploadRequest extends GroupArgs {

    protected InitMultipartUploadRequest() {
    }

    protected long fileSize;

    protected String fileExtName;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static class Builder extends GroupArgs.Builder<InitMultipartUploadRequest.Builder, InitMultipartUploadRequest> {
        @Override
        protected void validate(InitMultipartUploadRequest args) {
            // no validate
        }

        public Builder fileSize(long fileSize) {
            operations.add(args -> args.fileSize = fileSize);
            return this;
        }

        public Builder fileExtName(String fileExtName) {
            operations.add(args -> args.fileExtName = handlerFilename(fileExtName));
            return this;
        }
    }

    public long fileSize() {
        return fileSize;
    }

    public String fileExtName() {
        return fileExtName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InitMultipartUploadRequest that = (InitMultipartUploadRequest) o;
        return fileSize == that.fileSize &&
                Objects.equals(fileExtName, that.fileExtName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileSize, fileExtName);
    }
}
