package com.ykren.fastdfs.model.fdfs;

import java.util.List;

/**
 * 图片path
 *
 * @author ykren
 * @date 2022/2/9
 */
public class ImageStorePath {

    /**
     * 原始图片path
     */
    private StorePath img;

    /**
     * 缩略图path
     */
    private List<StorePath> thumbs;

    public StorePath getImg() {
        return img;
    }

    public void setImg(StorePath img) {
        this.img = img;
    }

    public List<StorePath> getThumbs() {
        return thumbs;
    }

    public void setThumbs(List<StorePath> thumbs) {
        this.thumbs = thumbs;
    }
}
