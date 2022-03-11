package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.config.FastDfsConfiguration;

import java.util.List;

/**
 * FastDFS构建类
 *
 * @author ykren
 * @date 2022/1/24
 */
public final class FastDfsClientBuilder implements FastDfsBuilder {
    /**
     * 构建FastDFS
     *
     * @param trackerServers
     * @return
     */
    @Override
    public FastDfs build(List<String> trackerServers) {
        return build(trackerServers, getFastDFSConfiguration());
    }

    /**
     * 构建FastDFS
     *
     * @param trackerServers
     * @return
     */
    @Override
    public FastDfs build(List<String> trackerServers, FastDfsConfiguration configuration) {
        return new FastDfsClient(trackerServers, getFastDFSConfiguration(configuration));
    }

    /**
     * 获取config
     *
     * @return
     */
    private static FastDfsConfiguration getFastDFSConfiguration() {
        return new FastDfsConfiguration();
    }

    private static FastDfsConfiguration getFastDFSConfiguration(FastDfsConfiguration config) {
        if (config == null) {
            config = new FastDfsConfiguration();
        }
        return config;
    }
}
