package com.zhao.common.utils;

/**
 * 视频信息，包含比特率、视频格式、分辨率、时长等
 * @Author: zhaolianqi
 */
public class MyVideoInfo extends MyMediaInfo {

    /** 编码格式 */
    private String encoder;
    /** 视频格式 */
    private String videoType;
    /** 分辨率：宽 */
    private int width;
    /** 分辨率：高 */
    private int height;

    public String getEncoder() {
        return encoder;
    }

    public void setEncoder(String encoder) {
        this.encoder = encoder;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
