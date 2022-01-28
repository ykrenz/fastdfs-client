package com.ykren.fastdfs.model;

import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.proto.OtherConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.ykren.fastdfs.model.CodeUtils.validateFile;
import static com.ykren.fastdfs.model.CodeUtils.validateFilename;
import static com.ykren.fastdfs.model.CodeUtils.validateGreaterZero;
import static com.ykren.fastdfs.model.CodeUtils.validateNotNull;

/**
 * 文件请求参数抽象类
 *
 * @author ykren
 * @date 2022/1/21
 */
public abstract class AbstractFileArgs extends GroupArgs {

    /**
     * 本地文件
     */
    protected File file;
    /**
     * 输入流
     */
    protected InputStream stream;
    /**
     * 文件大小
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
     * 文件上传参数构建类
     *
     * @param <B>
     * @param <A>
     */
    public abstract static class AbstractUploadBuilder<B extends AbstractFileArgs.AbstractUploadBuilder<B, A>, A extends AbstractFileArgs>
            extends AbstractFileArgs.AbstractBuilder<B, A> {

        /**
         * 参数异常打印 服务器不会报错 但是可能造成和预期不符
         *
         * @param args
         */
        protected void logWarn(A args) {
            if (args.fileExtName != null && args.fileExtName.length() > OtherConstants.FDFS_FILE_EXT_NAME_MAX_LEN) {
                String msg = String.format("参数fileExtName有误 fileExtName length > %d", OtherConstants.FDFS_FILE_EXT_NAME_MAX_LEN);
                LOGGER.warn(msg);
            }
            if (!CollectionUtils.isEmpty(args.metaData)) {
                for (MetaData metadata : args.metaData) {
                    String name = metadata.getName();
                    if (name.length() > OtherConstants.FDFS_MAX_META_NAME_LEN || metadata.getValue().length() > OtherConstants.FDFS_MAX_META_VALUE_LEN) {
                        String msg = String.format("参数metadata有误 name length > %d or value length > %d",
                                OtherConstants.FDFS_MAX_META_NAME_LEN, OtherConstants.FDFS_MAX_META_VALUE_LEN);
                        LOGGER.warn(msg);
                    }
                }
            }
        }

        @Override
        protected void validate(A args) {
            if (args.file == null && args.stream == null) {
                throw new IllegalArgumentException("上传文件不能为空");
            }
            if (args.file != null && args.stream != null) {
                throw new IllegalArgumentException("参数file和stream必须唯一");
            }
            logWarn(args);
        }

        @Override
        public B filePath(String filePath) {
            validateFilename(filePath);
            return super.filePath(filePath);
        }

        @Override
        public B file(File file) {
            validateFile(file);
            return super.file(file);
        }

        @Override
        public B stream(InputStream stream, long fileSize, String fileExtName) {
            validateGreaterZero(fileSize, "fileSize");
            return super.stream(stream, fileSize, fileExtName);
        }

        @Override
        public B metaData(String name, String value) {
            validateNotNull(name, "metaData name");
            validateNotNull(value, "metaData value");
            return super.metaData(name, value);
        }

        @Override
        public B metaData(Set<MetaData> metaData) {
            return super.metaData(metaData == null ? Collections.emptySet() : metaData);
        }
    }

    /**
     * 文件参数构造抽象类
     *
     * @param <B>
     * @param <A>
     */
    public abstract static class AbstractBuilder<B extends AbstractFileArgs.AbstractBuilder<B, A>, A extends AbstractFileArgs>
            extends GroupArgs.Builder<B, A> {

        @SuppressWarnings("unchecked")
        public B filePath(String filePath) {
            File file = Paths.get(filePath).toFile();
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            String ext = handlerFilename(getExtension(file.getName()));
            operations.add(args -> args.fileExtName = ext);
            return (B) this;
        }

        /**
         * 上传文件
         *
         * @param file
         * @return
         */
        @SuppressWarnings("unchecked")
        public B file(File file) {
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            String ext = handlerFilename(getExtension(file.getName()));
            operations.add(args -> args.fileExtName = ext);
            return (B) this;
        }


        /**
         * 上传文件流
         *
         * @param stream
         * @param fileSize
         * @param fileExtName
         * @return
         */
        @SuppressWarnings("unchecked")
        public B stream(InputStream stream, long fileSize, String fileExtName) {
            operations.add(args -> args.stream = stream);
            operations.add(args -> args.fileSize = fileSize);
            operations.add(args -> args.fileExtName = (fileExtName == null ? StringUtils.EMPTY : handlerFilename(fileExtName)));
            return (B) this;
        }

        /**
         * 元数据信息
         *
         * @return
         */
        @SuppressWarnings("unchecked")
        public B metaData(String name, String value) {
            operations.add(args -> args.metaData.add(new MetaData(name, value)));
            return (B) this;
        }

        /**
         * 元数据信息
         *
         * @param metaDataSet
         * @return
         */
        @SuppressWarnings("unchecked")
        public B metaData(Set<MetaData> metaDataSet) {
            operations.add(args -> args.metaData.addAll(metaDataSet));
            return (B) this;
        }

    }

    public File file() {
        return file;
    }

    public InputStream stream() {
        return stream;
    }

    public long fileSize() {
        return fileSize;
    }

    public String fileExtName() {
        return fileExtName;
    }

    public Set<MetaData> metaData() {
        return metaData;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AbstractFileArgs that = (AbstractFileArgs) o;
        return fileSize == that.fileSize &&
                Objects.equals(file, that.file) &&
                Objects.equals(stream, that.stream) &&
                Objects.equals(fileExtName, that.fileExtName) &&
                Objects.equals(metaData, that.metaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), file, stream, fileSize, fileExtName, metaData);
    }
}
