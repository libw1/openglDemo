package com.example.opengldemo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * created by libowen
 * on 2023/3/8
 */
public class SimpleRender implements GLSurfaceView.Renderer {

    private IDrawer mDrawer;

    public SimpleRender(IDrawer drawer) {
        this.mDrawer = drawer;

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0f,0f,0f,0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mDrawer.setTextureId(createTexturesIds(1)[0]);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        mDrawer.draw();
    }

    private int[] createTexturesIds(int count) {
        int[] texture = new int[count];
        GLES20.glGenTextures(count, texture, 0);//生成纹理
        return texture;
    }
}
