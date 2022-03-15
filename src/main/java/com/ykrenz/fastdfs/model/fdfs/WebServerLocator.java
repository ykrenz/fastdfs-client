package com.ykrenz.fastdfs.model.fdfs;

import com.ykrenz.fastdfs.exception.FdfsUnavailableException;

import java.util.Collections;
import java.util.List;

/**
 * @author ykren
 * @date 2022/3/15
 */
public class WebServerLocator {

    /**
     * 轮询圈
     */
    private final CircularList<String> circularList = new CircularList<>();

    private List<String> webUrls;

    public WebServerLocator(final List<String> webUrls) {
        this.webUrls = webUrls;
        init();
    }

    private void init() {
        if (webUrls != null) {
            circularList.addAll(webUrls);
        }
    }

    public List<String> getWebUrls() {
        return Collections.unmodifiableList(webUrls);
    }

    public String getWebUrl() {
        if (circularList.isEmpty()) {
            throw new FdfsUnavailableException("找不到可用的webUrl");
        }
        return circularList.next();
    }
}
