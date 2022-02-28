package com.ykrenz.fastdfs.model;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import static com.ykrenz.fastdfs.common.CodeUtils.validateGreaterZero;
import static com.ykrenz.fastdfs.common.CodeUtils.validateNotLessZero;
import static com.ykrenz.fastdfs.common.CodeUtils.validateNotBlankString;

/**
 * @author ykren
 * @date 2022/1/25
 */
public class UploadMultipartPartRequest extends AbstractFileArgs {
    /**
     * 分片索引
     */
    protected int partNumber;
    /**
     * 分片大小
     */
    protected long partSize;

    /**
     * 初始化文件path
     */
    public String path() {
        return path;
    }

    public int partNumber() {
        return partNumber;
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
    public static final class Builder extends AbstractFileArgs.Builder<Builder, UploadMultipartPartRequest> {

        @Override
        protected void validate(UploadMultipartPartRequest args) {
            super.validate(args);
            validateNotLessZero(args.partNumber, "partNumber");
            validateGreaterZero(args.partSize, "partSize");
            validateNotLessZero(args.fileSize, "fileSize");
            validateNotBlankString(args.path, "path");
        }

        /**
         * 初始化文件path
         *
         * @param path
         * @return
         */
        public Builder path(String path) {
            operations.add(args -> args.path = path);
            return this;
        }

        /**
         * 文件路径
         *
         * @param filePath
         * @param partNumber
         * @return
         */
        public Builder file(String filePath, int partNumber) {
            return file(new File(filePath), partNumber);
        }

        /**
         * 上传文件
         *
         * @param file
         * @return
         */
        public Builder file(File file, int partNumber) {
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            operations.add(args -> args.partNumber = partNumber);
            return this;
        }


        /**
         * 追加文件流
         *
         * @param inputStream
         * @return
         */
        public Builder stream(InputStream inputStream, int partNumber, long fileSize) {
            operations.add(args -> args.stream = inputStream);
            operations.add(args -> args.partNumber = partNumber);
            operations.add(args -> args.fileSize = fileSize);
            return this;
        }

        /**
         * 分片大小
         *
         * @param partSize
         * @return
         */
        public Builder partSize(long partSize) {
            operations.add(args -> args.partSize = partSize);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UploadMultipartPartRequest that = (UploadMultipartPartRequest) o;
        return partNumber == that.partNumber &&
                partSize == that.partSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), partNumber, partSize);
    }
}
