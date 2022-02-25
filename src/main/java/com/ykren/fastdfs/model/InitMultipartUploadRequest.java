package com.ykren.fastdfs.model;

import java.util.Objects;

import static com.ykren.fastdfs.common.CodeUtils.validateNotLessZero;

/**
 * 初始化分片上传
 *
 * @author ykren
 * @date 2022/1/25
 */
public class InitMultipartUploadRequest extends GroupArgs {
    /**
     * 文件大小
     */
    protected long fileSize;
    /**
     * 文件后缀
     */
    protected String fileExtName;

    public String fileExtName() {
        return fileExtName;
    }

    public long fileSize() {
        return fileSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends GroupArgs.Builder<InitMultipartUploadRequest.Builder, InitMultipartUploadRequest> {
        @Override
        protected void validate(InitMultipartUploadRequest args) {
            validateNotLessZero(args.fileSize, "fileSize");
        }

        /**
         * 文件大小
         *
         * @param fileSize 文件大小
         * @return
         */
        public Builder fileSize(long fileSize) {
            operations.add(args -> args.fileSize = fileSize);
            return this;
        }

        /**
         * 文件后缀名
         *
         * @param fileExtName
         * @return
         */
        public Builder fileExtName(String fileExtName) {
            operations.add(args -> args.fileExtName = fileExtName);
            return this;
        }
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
