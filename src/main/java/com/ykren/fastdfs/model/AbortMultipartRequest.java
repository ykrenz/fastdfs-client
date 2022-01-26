package com.ykren.fastdfs.model;

import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;

/**
 * 终止分片上传参数
 *
 * @author ykren
 * @date 2022/1/25
 */
public class AbortMultipartRequest extends AbstractGroupPathArgs {

    protected AbortMultipartRequest() {
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static class Builder extends AbstractGroupPathBuilder<Builder, AbortMultipartRequest> {
        @Override
        protected void validate(AbortMultipartRequest args) {
            validateNotBlankString(args.path, "path");
        }
    }
}
