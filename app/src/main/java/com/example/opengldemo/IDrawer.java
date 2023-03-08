package com.example.opengldemo;

/**
 * created by libowen
 * on 2023/3/8
 */
public interface IDrawer {

    //设置视频的原始宽高
    void setVideoSize(int videoW, int videoH);
    //设置OpenGL窗口宽高
    void setWorldSize(int worldW, int worldH);

    void setInitListener(OnInitListener listener);

    void draw();
    void setTextureId(int id);
    void release();
}
