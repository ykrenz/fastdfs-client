package com.ykren.fastdfs.model;

import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.proto.storage.enums.StorageMetadataSetType;
import com.ykren.fastdfs.model.proto.OtherConstants;
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
import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;
import static com.ykren.fastdfs.model.CodeUtils.validateNotNull;

/**
 * 修改文件请求参数
 *
 * @author ykren
 * @date 2022/1/21
 */
public class ModifyFileRequest extends AbstractGroupPathArgs {
    protected ModifyFileRequest() {
    }

    /**
     * 本地文件
     */
    protected File file;
    /**
     * 输入流
     */
    protected InputStream inputStream;
    /**
     * 文件大小
     */
    protected long fileSize;
    /**
     * 文件修改起始值
     */
    protected long fileOffset;
    /**
     * 文件元数据
     */
    protected Set<MetaData> metaData = new HashSet<>();
    /**
     * 文件元数据类型
     */
    protected StorageMetadataSetType metaType;
    /**
     * 自动关闭流
     */
    protected boolean autoClose = true;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static class Builder extends AbstractGroupPathArgs.AbstractGroupPathBuilder<Builder, ModifyFileRequest> {

        @Override
        protected void validate(ModifyFileRequest args) {
            if (args.file == null && args.inputStream == null) {
                throw new IllegalArgumentException("上传文件不能为空");
            }
            if (args.file != null && args.inputStream != null) {
                throw new IllegalArgumentException("参数file和inputStream必须唯一");
            }
            validateNotBlankString(args.path, "path");
            logWarn(args);
        }

        protected void logWarn(ModifyFileRequest args) {
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

        public Builder file(String filePath, long fileOffset) {
            validateFilename(filePath);
            File file = Paths.get(filePath).toFile();
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            operations.add(args -> args.fileOffset = fileOffset);
            return this;
        }

        /**
         * 上传文件
         *
         * @param file
         * @return
         */
        public Builder file(File file, long fileOffset) {
            validateFile(file);
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            operations.add(args -> args.fileOffset = fileOffset);
            return this;
        }


        /**
         * 追加文件流
         *
         * @param inputStream
         * @param fileSize
         * @return
         */
        public Builder stream(InputStream inputStream, long fileSize, long fileOffset) {
            validateNotNull(inputStream, "inputStream");
            validateGreaterZero(fileSize, "fileSize");
            operations.add(args -> args.inputStream = inputStream);
            operations.add(args -> args.fileSize = fileSize);
            operations.add(args -> args.fileOffset = fileOffset);
            return this;
        }

        /**
         * 元数据信息
         *
         * @param metaData
         * @param type
         * @return
         */
        public Builder metaData(Set<MetaData> metaData, StorageMetadataSetType type) {
            validateNotNull(type, "metadata type");
            operations.add(args -> args.metaData.addAll(metaData == null ? Collections.emptySet() : metaData));
            operations.add(args -> args.metaType = type);
            return this;
        }

        /**
         * 自动关闭流 false则流不关闭
         *
         * @param autoClose
         * @return
         */
        public Builder autoClose(boolean autoClose) {
            operations.add(args -> args.autoClose = autoClose);
            return this;
        }

    }

    public File file() {
        return file;
    }

    public InputStream inputStream() {
        return inputStream;
    }

    public long fileSize() {
        return fileSize;
    }

    public long offset() {
        return fileOffset < 0 ? 0 : fileOffset;
    }

    public Set<MetaData> metaData() {
        return metaData;
    }

    public StorageMetadataSetType metaType() {
        return metaType;
    }

    public boolean autoClose() {
        return autoClose;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModifyFileRequest that = (ModifyFileRequest) o;
        return fileSize == that.fileSize &&
                fileOffset == that.fileOffset &&
                autoClose == that.autoClose &&
                Objects.equals(file, that.file) &&
                Objects.equals(inputStream, that.inputStream) &&
                Objects.equals(metaData, that.metaData) &&
                metaType == that.metaType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), file, inputStream, fileSize, fileOffset, metaData, metaType, autoClose);
    }
}
