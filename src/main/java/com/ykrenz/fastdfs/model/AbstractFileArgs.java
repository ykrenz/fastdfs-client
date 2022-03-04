package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.event.ProgressListener;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.common.CodeUtils;
import com.ykrenz.fastdfs.model.proto.OtherConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * 文件请求参数抽象类
 *
 * @author ykren
 * @date 2022/1/21
 */
public abstract class AbstractFileArgs extends GroupArgs {
    /**
     * 进度条监听器
     */
    protected ProgressListener listener;
    /**
     * 本地文件
     */
    protected File file;
    /**
     * 输入流
     */
    protected InputStream stream;
    /**
     * 上传内容长度
     */
    protected long fileSize;
    /**
     * 文件扩展名
     */
    protected String fileExtName;
    /**
     * 文件元数据
     */
    protected Set<MetaData> metaData = new HashSet<>();
    /**
     * 文件路径 path
     */
    protected String path;

    public File file() {
        return file;
    }

    public InputStream stream() {
        return stream;
    }

    public long fileSize() {
        return fileSize;
    }

    public ProgressListener listener() {
        return listener;
    }

    /**
     * 文件参数构造抽象类
     *
     * @param <B>
     * @param <A>
     */
    public abstract static class Builder<B extends Builder<B, A>, A extends AbstractFileArgs>
            extends GroupArgs.Builder<B, A> {
        /**
         * 日志
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

        @SuppressWarnings("unchecked")
        public B listener(ProgressListener listener) {
            operations.add(args -> args.listener = listener);
            return (B) this;
        }

        @Override
        protected void validate(A args) {
            if (args.file == null && args.stream == null) {
                throw new IllegalArgumentException("upload content cannot be empty. ");
            }
            if (args.file != null && args.stream != null) {
                throw new IllegalArgumentException("parameters file and stream must be unique. ");
            }
            if (args.file != null) {
                CodeUtils.validateFile(args.file);
            }
            CodeUtils.validateNotLessZero(args.fileSize, "fileSize");

            if (args.fileExtName != null && args.fileExtName.length() > OtherConstants.FDFS_FILE_EXT_NAME_MAX_LEN) {
                String msg = String.format("fileExtName length > %d", OtherConstants.FDFS_FILE_EXT_NAME_MAX_LEN);
                LOGGER.warn(msg);
            }
            if (args.metaData != null && !args.metaData.isEmpty()) {
                for (MetaData metadata : args.metaData) {
                    String name = metadata.getName();
                    String value = metadata.getValue();
                    if (name != null && name.length() > OtherConstants.FDFS_MAX_META_NAME_LEN) {
                        String msg = String.format("metadata name length > %d ", OtherConstants.FDFS_MAX_META_NAME_LEN);
                        LOGGER.warn(msg);
                    }
                    if (value != null && value.length() > OtherConstants.FDFS_MAX_META_VALUE_LEN) {
                        String msg = String.format("metadata value length > %d ", OtherConstants.FDFS_MAX_META_VALUE_LEN);
                        LOGGER.warn(msg);
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractFileArgs that = (AbstractFileArgs) o;
        return fileSize == that.fileSize &&
                Objects.equals(listener, that.listener) &&
                Objects.equals(file, that.file) &&
                Objects.equals(stream, that.stream) &&
                Objects.equals(fileExtName, that.fileExtName) &&
                Objects.equals(metaData, that.metaData) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), listener, file, stream, fileSize, fileExtName, metaData, path);
    }
}
