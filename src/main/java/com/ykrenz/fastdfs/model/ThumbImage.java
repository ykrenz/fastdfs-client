package com.ykrenz.fastdfs.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 缩略图配置
 * <pre>
 *     支持三种配置
 *     1. 支持按比例缩放
 *     2. 按长宽缩放
 *     如果配置按比例缩放，则按比例计算
 *     如果没有配置按比例缩放，则按长宽缩放
 * </pre>
 *
 * @author ykren
 */
public class ThumbImage {
    /**
     * 缩放长度
     */
    private int width;
    /**
     * 缩放高度
     */
    private int height;
    /**
     * 缩放比例
     */
    private double percent;

    private String prefixName;

    /**
     * 按长宽缩放
     *
     * @param width
     * @param height
     */
    public ThumbImage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 按比例缩放
     *
     * @param percent
     */
    public ThumbImage(double percent) {
        this.percent = percent;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getPercent() {
        return percent;
    }

    /**
     * 设置缩略图文件路径前缀
     *
     * @param prefixName
     */
    public void setPrefixName(String prefixName) {
        this.prefixName = prefixName;
    }

    /**
     * 生成前缀如:_150x150
     */
    public String getPrefixName() {
        if (StringUtils.isNotBlank(prefixName)) {
            return prefixName;
        }
        if (percent != 0) {
            return getPrefixNameByPercent();
        }
        return getPrefixNameBySize();
    }

    /**
     * 按缩放尺寸获取前缀
     *
     * @return
     */
    private String getPrefixNameBySize() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("_").append(width).append("x").append(height);
        return new String(buffer);
    }

    /**
     * 按缩放尺寸获取前缀
     *
     * @return
     */
    private String getPrefixNameByPercent() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("_").append(Math.round(100 * percent)).append("p_");
        return new String(buffer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThumbImage that = (ThumbImage) o;
        return width == that.width &&
                height == that.height &&
                Double.compare(that.percent, percent) == 0 &&
                Objects.equals(prefixName, that.prefixName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, percent, prefixName);
    }
}