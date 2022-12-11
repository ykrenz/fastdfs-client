package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.common.FastDfsUtils;

import java.util.Objects;

import static com.ykrenz.fastdfs.common.CodeUtils.validateGreaterZero;
import static com.ykrenz.fastdfs.common.CodeUtils.validateNotLessZero;

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
     * 分片大小
     */
    protected long partSize;
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

    public long partSize() {
        return partSize;
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
            validateGreaterZero(args.partSize, "partSize");
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
         * 分片大小
         *
         * @param partSize 分片大小
         * @return
         */
        public Builder partSize(long partSize) {
            operations.add(args -> args.partSize = partSize);
            return this;
        }

        /**
         * 文件后缀名
         *
         * @param fileExtName
         * @return
         */
        public Builder fileExtName(String fileExtName) {
            String handlerFileExtName = FastDfsUtils.handlerFilename(fileExtName);
            operations.add(args -> args.fileExtName = handlerFileExtName);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InitMultipartUploadRequest that = (InitMultipartUploadRequest) o;
        return fileSize == that.fileSize && partSize == that.partSize && Objects.equals(fileExtName, that.fileExtName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileSize, partSize, fileExtName);
    }
}
