package com.ykren.fastdfs;

import java.util.List;

import static com.ykren.fastdfs.common.CodeUtils.validateCollectionNotEmpty;
import static com.ykren.fastdfs.common.CodeUtils.validateNotNull;

/**
 * FastDFS构建类
 *
 * @author ykren
 * @date 2022/1/24
 */
public final class FastDFSClientBuilder implements FastDFSBuilder {
    /**
     * 构建FastDFS
     *
     * @param trackerServers
     * @return
     */
    @Override
    public FastDFS build(List<String> trackerServers) {
        return build(trackerServers, null, getFastDFSConfiguration());
    }

    @Override
    public FastDFS build(List<String> trackerServers, String group) {
        return build(trackerServers, group, getFastDFSConfiguration());
    }

    /**
     * 构建FastDFS
     *
     * @param trackerServers
     * @return
     */
    @Override
    public FastDFS build(List<String> trackerServers, String group, FastDFSConfiguration configuration) {
        validateCollectionNotEmpty(trackerServers, "trackerServers");
        validateNotNull(configuration, "configuration");
        return new FastDFSClient(trackerServers, group, configuration);
    }

    /**
     * 获取config
     *
     * @return
     */
    private FastDFSConfiguration getFastDFSConfiguration() {
        return new FastDFSConfiguration();
    }
}
