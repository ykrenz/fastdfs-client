package com.ykrenz.fastdfs.model;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import static com.ykrenz.fastdfs.common.CodeUtils.validateNotLessZero;
import static com.ykrenz.fastdfs.common.CodeUtils.validateNotBlankString;

/**
 * @author ykren
 * @date 2022/1/25
 */
@Deprecated
public class UploadMultipartPartRequest extends AbstractFileArgs {
    /**
     * 分片起始位置
     */
    protected long offset;

    /**
     * 初始化文件path
     */
    public String path() {
        return path;
    }

    public long offset() {
        return offset;
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
            validateNotLessZero(args.offset, "offset");
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
         * offset方式
         *
         * @param offset 文件起始值
         * @return
         */
        public Builder fileOffset(String filePath, long offset) {
            return fileOffset(new File(filePath), offset);
        }

        public Builder fileOffset(File file, long offset) {
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            operations.add(args -> args.offset = offset);
            return this;
        }

        public Builder streamOffset(InputStream inputStream, long fileSize, long offset) {
            operations.add(args -> args.stream = inputStream);
            operations.add(args -> args.fileSize = fileSize);
            operations.add(args -> args.offset = offset);
            return this;
        }

        /**
         * partSize方式
         *
         * @param partNumber 分片索引 起始值为1
         * @param partSize   分片大小
         * @return
         */
        public Builder filePart(String filePath, int partNumber, long partSize) {
            return filePart(new File(filePath), partNumber, partSize);
        }

        public Builder filePart(File file, int partNumber, long partSize) {
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            // 计算offset
            if (partNumber > 1) {
                operations.add(args -> args.offset = (partNumber - 1) * partSize);
            }
            return this;
        }

        public Builder streamPart(InputStream inputStream, long fileSize, int partNumber, long partSize) {
            operations.add(args -> args.stream = inputStream);
            operations.add(args -> args.fileSize = fileSize);
            // 计算offset
            if (partNumber > 1) {
                operations.add(args -> args.offset = (partNumber - 1) * partSize);
            }
            return this;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UploadMultipartPartRequest that = (UploadMultipartPartRequest) o;
        return offset == that.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), offset);
    }
}
