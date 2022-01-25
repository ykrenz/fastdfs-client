package com.ykren.fastdfs.model;


import com.ykren.fastdfs.model.proto.OtherConstants;

import java.util.Objects;

import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;

/**
 * 上传从文件参数
 *
 * @author ykren
 * @date 2022/1/21
 */
public class UploadSalveFileRequest extends AbstractFileArgs {
    protected UploadSalveFileRequest() {
    }

    /**
     * 主文件 path
     */
    protected String masterFilePath;

    /**
     * 从文件前缀
     */
    protected String prefix;


    public String masterFilePath() {
        return masterFilePath;
    }

    public String prefix() {
        return prefix;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends AbstractUploadBuilder<Builder, UploadSalveFileRequest> {

        private static final String PREFIX = "prefix";

        @Override
        protected void logWarn(UploadSalveFileRequest args) {
            super.logWarn(args);
            if (args.prefix != null && args.prefix.length() > OtherConstants.FDFS_FILE_PREFIX_MAX_LEN) {
                String msg = String.format("参数prefix有误 prefix length > %d", OtherConstants.FDFS_FILE_PREFIX_MAX_LEN);
                LOGGER.warn(msg);
            }
        }

        @Override
        protected void validate(UploadSalveFileRequest args) {
            super.validate(args);
            //主文件group不能为空
            validateNotBlankString(args.groupName, "masterFile group");
            validateNotBlankString(args.masterFilePath, "masterFilePath");
            validateNotBlankString(args.prefix, PREFIX);
        }

        public Builder masterFilePath(String masterFilePath) {
            validateNotBlankString(masterFilePath, "masterFilePath");
            operations.add(args -> args.masterFilePath = masterFilePath);
            return this;
        }

        public Builder prefix(String prefix) {
            validateNotBlankString(prefix, PREFIX);
            operations.add(args -> {
                String prefixResult = handlerFilename(prefix);
                args.prefix = prefix.isEmpty() ? "." : prefixResult;
            });
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UploadSalveFileRequest that = (UploadSalveFileRequest) o;
        return Objects.equals(masterFilePath, that.masterFilePath) &&
                Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), masterFilePath, prefix);
    }
}
