package com.ykren.fastdfs.model;

/**
 * appender文件改普通文件信息参数
 *
 * @author ykren
 * @date 2022/1/22
 */
public class RegenerateAppenderFileRequest extends GroupPathArgs {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 参数构建类
     */
    public static final class Builder extends GroupPathArgs.Builder<Builder, RegenerateAppenderFileRequest> {
    }
}
