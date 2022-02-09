package com.ykren.fastdfs.model.fdfs;

/**
 * 图片path
 *
 * @author ykren
 * @date 2022/2/9
 */
public class ImageStorePath {

    private StorePath imgPath;

    private StorePath thumbPath;

    public ImageStorePath(StorePath imgPath) {
        this.imgPath = imgPath;
    }

    public ImageStorePath(StorePath imgPath, StorePath thumbPath) {
        this.imgPath = imgPath;
        this.thumbPath = thumbPath;
    }

    public StorePath getImgPath() {
        return imgPath;
    }

    public void setImgPath(StorePath imgPath) {
        this.imgPath = imgPath;
    }

    public StorePath getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(StorePath thumbPath) {
        this.thumbPath = thumbPath;
    }
}
