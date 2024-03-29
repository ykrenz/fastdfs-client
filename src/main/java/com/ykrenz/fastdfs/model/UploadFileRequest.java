package com.ykrenz.fastdfs.model;

import com.ykrenz.fastdfs.model.fdfs.MetaData;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * 一般文件上传参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class UploadFileRequest extends AbstractFileExtHandlerArgs {

    public Set<MetaData> metaData() {
        return metaData;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends AbstractFileExtHandlerArgs.Builder<Builder, UploadFileRequest> {

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
            super.fileExtName(getExtension(file.getName()));
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
            super.fileExtName(fileExtName);
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
    }
}
