package com.ykren.fastdfs.model;

import com.ykren.fastdfs.model.fdfs.MetaData;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.ykren.fastdfs.common.CodeUtils.validateNotLessZero;
import static com.ykren.fastdfs.common.CodeUtils.validateNotNull;

/**
 * 图片上传参数
 *
 * @author ykren
 * @date 2022/2/9
 */
public class UploadImageRequest extends AbstractFileArgs {

    /**
     * 缩略图
     */
    protected ThumbImage thumbImage;

    /**
     * 只上传缩略图
     */
    protected boolean thumb = false;

    /**
     * 缩略图文件元数据
     */
    protected Set<MetaData> thumbMetaData = new HashSet<>();

    /**
     * 是否只上传缩略图
     *
     * @return
     */
    public boolean onlyThumb() {
        return thumb;
    }

    public String fileExtName() {
        return fileExtName;
    }

    public Set<MetaData> metaData() {
        return metaData;
    }

    public ThumbImage thumbImage() {
        return thumbImage;
    }

    public Set<MetaData> thumbMetaData() {
        return thumbMetaData;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends AbstractFileArgs.Builder<Builder, UploadImageRequest> {
        @Override
        protected void validate(UploadImageRequest args) {
            super.validate(args);
            if (args.onlyThumb()) {
                validateNotNull(args.thumbImage, "thumbImage");
            }
            if (args.thumbImage != null) {
                validateNotLessZero(args.thumbImage.getPercent(), "percent");
                validateNotLessZero(args.thumbImage.getWidth(), "percent");
                validateNotLessZero(args.thumbImage.getHeight(), "percent");
            }
        }

        /**
         * 上传文件
         *
         * @param filePath
         * @return
         */
        public Builder filePath(String filePath) {
            return file(new File(filePath));
        }

        /**
         * 上传文件
         *
         * @param file
         * @return
         */
        public Builder file(File file) {
            operations.add(args -> args.file = file);
            operations.add(args -> args.fileSize = file.length());
            operations.add(args -> args.fileExtName = getExtension(file.getName()));
            return this;
        }

        /**
         * 上传文件流
         *
         * @param stream
         * @param fileSize
         * @param fileExtName
         * @return
         */
        public Builder stream(InputStream stream, long fileSize, String fileExtName) {
            operations.add(args -> args.stream = stream);
            operations.add(args -> args.fileSize = fileSize);
            operations.add(args -> args.fileExtName = fileExtName);
            return this;
        }


        /**
         * 元数据信息
         *
         * @return
         */
        public Builder metaData(String name, String value) {
            operations.add(args -> args.metaData.add(new MetaData(name, value)));
            return this;
        }

        /**
         * 元数据信息
         *
         * @param metaData
         * @return
         */
        public Builder metaData(Set<MetaData> metaData) {
            operations.add(args -> args.metaData.addAll(metaData == null ? Collections.emptySet() : metaData));
            return this;
        }

        /**
         * 缩略图信息
         *
         * @param thumbImage
         * @return
         */
        public Builder thumbImage(ThumbImage thumbImage) {
            operations.add(args -> args.thumbImage = thumbImage);
            return this;
        }

        /**
         * 缩略图元数据信息
         *
         * @return
         */
        public Builder thumbMetaData(String name, String value) {
            operations.add(args -> args.thumbMetaData.add(new MetaData(name, value)));
            return this;
        }

        /**
         * 缩略图元数据信息
         *
         * @param metaData
         * @return
         */
        public Builder thumbMetaData(Set<MetaData> metaData) {
            operations.add(args -> args.thumbMetaData.addAll(metaData == null ? Collections.emptySet() : metaData));
            return this;
        }

        /**
         * 只生成缩略图
         *
         * @param thumbImage
         * @return
         */
        public Builder onlyThumbImage(ThumbImage thumbImage) {
            operations.add(args -> args.thumbImage = thumbImage);
            operations.add(args -> args.thumb = true);
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UploadImageRequest that = (UploadImageRequest) o;
        return Objects.equals(thumbImage, that.thumbImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thumbImage);
    }
}
