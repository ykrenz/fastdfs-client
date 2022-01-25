package com.ykren.fastdfs.model;

import com.ykren.fastdfs.model.proto.storage.DownloadCallback;
import com.ykren.fastdfs.model.proto.storage.DownloadFileWriter;

import java.util.Objects;

import static com.ykren.fastdfs.model.CodeUtils.validateGreaterZero;
import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;

/**
 * 下载文件信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class DownloadFileRequest<T> extends AbstractGroupPathArgs {

    protected DownloadFileRequest() {
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    protected long fileOffset;

    protected long fileSize;

    protected DownloadCallback<T> callback;

    public long offset() {
        return fileOffset < 0 ? 0 : fileOffset;
    }

    public long fileSize() {
        return fileSize;
    }

    public DownloadCallback<T> callback() {
        return callback;
    }

    /**
     * 参数构建类
     */
    public static class Builder<T> extends AbstractGroupPathBuilder<Builder<T>, DownloadFileRequest<T>> {


        @Override
        protected void validate(DownloadFileRequest<T> args) {
            validateNotBlankString(args.path, "path");
            validateGreaterZero(args.fileSize, "fileSize");
        }

        @SuppressWarnings("unchecked")
        public Builder<String> file(String filePath) {
            DownloadFileWriter fileRequest = new DownloadFileWriter(filePath);
            operations.add(args -> args.callback = (DownloadCallback<T>) fileRequest);
            return (Builder<String>) this;
        }

        public Builder<T> callback(DownloadCallback<T> callback) {
            operations.add(args -> args.callback = callback);
            return this;
        }

        public Builder<T> offset(long fileOffset) {
            operations.add(args -> args.fileOffset = fileOffset);
            return this;
        }

        public Builder<T> fileSize(long fileSize) {
            operations.add(args -> args.fileSize = fileSize);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DownloadFileRequest<?> that = (DownloadFileRequest<?>) o;
        return fileOffset == that.fileOffset &&
                fileSize == that.fileSize &&
                Objects.equals(callback, that.callback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fileOffset, fileSize, callback);
    }
}
