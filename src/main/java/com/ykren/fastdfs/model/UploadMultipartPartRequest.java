package com.ykren.fastdfs.model;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Objects;

import static com.ykren.fastdfs.model.CodeUtils.validateFile;
import static com.ykren.fastdfs.model.CodeUtils.validateFilename;
import static com.ykren.fastdfs.model.CodeUtils.validateGreaterZero;
import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;

/**
 * @author ykren
 * @date 2022/1/25
 */
public class UploadMultipartPartRequest extends AbstractGroupPathArgs {

    /**
     * 流信息
     */
    protected InputStream stream;
    /**
     * 流文件
     */
    protected File file;
    /**
     * 分片索引
     */
    protected int partNumber;
    /**
     * 当前分片的大小
     */
    protected long partSize;

    protected UploadMultipartPartRequest() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public InputStream stream() {
        return stream;
    }

    public File file() {
        return file;
    }

    public int partNumber() {
        return partNumber;
    }

    public long partSize() {
        return partSize;
    }

    /**
     * 参数构建类
     */
    public static class Builder extends AbstractGroupPathArgs.AbstractGroupPathBuilder<Builder, UploadMultipartPartRequest> {

        @Override
        protected void validate(UploadMultipartPartRequest args) {
            if (args.file == null && args.stream == null) {
                throw new IllegalArgumentException("上传文件不能为空");
            }
            if (args.file != null && args.stream != null) {
                throw new IllegalArgumentException("参数file和inputStream必须唯一");
            }
            validateGreaterZero(args.partNumber, "partNumber");
            validateNotBlankString(args.path, "path");
        }

        public Builder file(String filePath, int partNumber) {
            validateFilename(filePath);
            File file = Paths.get(filePath).toFile();
            operations.add(args -> args.file = file);
            operations.add(args -> args.partSize = file.length());
            operations.add(args -> args.partNumber = partNumber);
            return this;
        }

        /**
         * 上传文件
         *
         * @param file
         * @return
         */
        public Builder file(File file, int partNumber) {
            validateFile(file);
            operations.add(args -> args.file = file);
            operations.add(args -> args.partSize = file.length());
            operations.add(args -> args.partNumber = partNumber);
            return this;
        }


        /**
         * 追加文件流
         *
         * @param inputStream
         * @return
         */
        public Builder stream(InputStream inputStream, int partNumber, long partSize) {
            validateGreaterZero(partSize, "partSize");
            operations.add(args -> args.stream = inputStream);
            operations.add(args -> args.partNumber = partNumber);
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
                partSize == that.partSize &&
                Objects.equals(stream, that.stream) &&
                Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), stream, file, partNumber, partSize);
    }
}
