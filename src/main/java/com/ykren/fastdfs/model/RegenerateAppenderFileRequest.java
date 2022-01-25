package com.ykren.fastdfs.model;

import static com.ykren.fastdfs.model.CodeUtils.validateNotBlankString;

/**
 * appender文件改普通文件信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class RegenerateAppenderFileRequest extends AbstractGroupPathArgs {

    protected RegenerateAppenderFileRequest() {
    }

    public static Builder builder() {
        return new RegenerateAppenderFileRequest.Builder();
    }

    /**
     * 参数构建类
     */
    public static class Builder extends AbstractGroupPathBuilder<RegenerateAppenderFileRequest.Builder, RegenerateAppenderFileRequest> {
        @Override
        protected void validate(RegenerateAppenderFileRequest args) {
            validateNotBlankString(args.path, "path");
        }
    }
}
