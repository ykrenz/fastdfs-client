package com.ykren.fastdfs.model;

import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;

/**
 * 文件元数据信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class MetaDataInfoRequest extends AbstractGroupPathArgs {

    protected MetaDataInfoRequest() {
    }

    public static Builder builder() {
        return new MetaDataInfoRequest.Builder();
    }

    /**
     * 一般文件上传参数构建类
     */
    public static class Builder extends AbstractGroupPathBuilder<MetaDataInfoRequest.Builder, MetaDataInfoRequest> {
        @Override
        protected void validate(MetaDataInfoRequest args) {
            validateNotBlankString(args.path, "path");
        }
    }
}
