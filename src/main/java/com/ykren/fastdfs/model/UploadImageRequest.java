package com.ykren.fastdfs.model;

import com.ykren.fastdfs.model.fdfs.MetaData;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 图片缩略图上传参数
 *
 * @author ykren
 * @date 2022/2/9
 */
public class UploadImageRequest extends AbstractFileArgs {

    /**
     * 缩略图
     */
    protected Set<ThumbImageRequest> thumbImages = new LinkedHashSet<>();
    /**
     * 缩略图文件元数据
     */
    protected Set<MetaData> thumbMetaData = new HashSet<>();

    public String fileExtName() {
        return fileExtName;
    }

    public Set<MetaData> metaData() {
        return metaData;
    }

    public long crc32() {
        return crc32;
    }

    public Set<ThumbImageRequest> thumbImages() {
        return thumbImages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class ThumbImageRequest {
        /**
         * 缩略图
         */
        protected ThumbImage thumbImage;
        /**
         * 缩略图文件元数据
         */
        protected Set<MetaData> thumbMetaData;

        public ThumbImageRequest(ThumbImage thumbImage, Set<MetaData> thumbMetaData) {
            this.thumbImage = thumbImage;
            this.thumbMetaData = thumbMetaData;
        }

        public ThumbImage thumbImage() {
            return thumbImage;
        }

        public Set<MetaData> thumbMetaData() {
            return thumbMetaData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ThumbImageRequest that = (ThumbImageRequest) o;
            return Objects.equals(thumbImage, that.thumbImage) &&
                    Objects.equals(thumbMetaData, that.thumbMetaData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(thumbImage, thumbMetaData);
        }
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends AbstractFileArgs.Builder<Builder, UploadImageRequest> {
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
            ThumbImageRequest request = new ThumbImageRequest(thumbImage, Collections.emptySet());
            operations.add(args -> args.thumbImages.add(request));
            return this;
        }

        public Builder thumbImage(ThumbImage thumbImage, Set<MetaData> metaData) {
            ThumbImageRequest request = new ThumbImageRequest(thumbImage, metaData);
            operations.add(args -> args.thumbImages.add(request));
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UploadImageRequest that = (UploadImageRequest) o;
        return Objects.equals(thumbImages, that.thumbImages) &&
                Objects.equals(thumbMetaData, that.thumbMetaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), thumbImages, thumbMetaData);
    }
}
