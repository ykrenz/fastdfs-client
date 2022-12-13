package com.ykrenz.fastdfs.model.fdfs;

import com.ykrenz.fastdfs.exception.FdfsUnavailableException;

import java.util.Collections;
import java.util.List;

/**
 * @author ykren
 * @date 2022/3/15
 */
public class HttpServerLocator {

    /**
     * 轮询圈
     */
    private final CircularList<String> circularList = new CircularList<>();

    private List<String> httpUrls;

    public HttpServerLocator(final List<String> httpUrls) {
        this.httpUrls = httpUrls;
        init();
    }

    private void init() {
        if (httpUrls != null) {
            circularList.addAll(httpUrls);
        }
    }

    public List<String> getHttpUrls() {
        return Collections.unmodifiableList(httpUrls);
    }

    public String getHttpUrl() {
        if (circularList.isEmpty()) {
            throw new FdfsUnavailableException("找不到可用的httpUrl");
        }
        return circularList.next();
    }
}
