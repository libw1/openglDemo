package com.example.opengldemo;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * created by libowen
 * on 2023/3/8
 */
public class TriangleDrawer implements IDrawer{

    //顶点坐标 世界坐标系
    private float[] mVertexCoors = new float[]{
            -1f, -1f,
            1f, -1f,
            0f, 1f
    };

    //纹理坐标 Android坐标系
    private float[] mTextureCoors = new float[]{
            0f, 1f,
            1f, 1f,
            0.5f, 0f
    };

    private int mTextureId = -1;//纹理id
    private int mProgram = -1;//opengl程序id
    private int mVertextPosHandler = -1;//顶点坐标接受者
    private int mTexturePosHandler = -1;//纹理坐标接受者

    private FloatBuffer mVertextBuffer;//顶点坐标
    private FloatBuffer mTextureBuffer;//纹理坐标

    public TriangleDrawer() {
        initPos();
    }

    private void initPos() {
        ByteBuffer bb = ByteBuffer.allocateDirect(mVertexCoors.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //把数据放入FloatBuffer
        mVertextBuffer = bb.asFloatBuffer();
        mVertextBuffer.put(mVertexCoors);
        mVertextBuffer.position(0);

        ByteBuffer cc = ByteBuffer.allocateDirect(mTextureCoors.length * 4);
        cc.order(ByteOrder.nativeOrder());
        mTextureBuffer = cc.asFloatBuffer();
        mTextureBuffer.put(mTextureCoors);
        mTextureBuffer.position(0);
    }

    @Override
    public void setVideoSize(int videoW, int videoH) {

    }

    @Override
    public void setWorldSize(int worldW, int worldH) {

    }

    @Override
    public void setInitListener(OnInitListener listener) {

    }

    @Override
    public void draw() {
        if (mTextureId != -1) {
            createGlPrg();

            doDraw();
        }
    }

    private void createGlPrg() {
        if (mProgram == -1) {
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader());
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader());
            //创建程序
            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            mVertextPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        }
        GLES20.glUseProgram(mProgram);
    }

    private String getFragmentShader() {
        return "precision mediump float;" +
                "void main() {" +
                "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                "}";
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        //放入代码
        GLES20.glShaderSource(shader, shaderCode);
        //编译
        GLES20.glCompileShader(shader);
        return shader;
    }

    private String getVertexShader() {
        return "attribute vec4 aPosition;" +
                "void main() {" +
                "  gl_Position = aPosition;" +
                "}";
    }

    private void doDraw() {
        GLES20.glEnableVertexAttribArray(mVertextPosHandler);
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);

        GLES20.glVertexAttribPointer(mVertextPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertextBuffer);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0 ,3);
    }

    @Override
    public void setTextureId(int id) {
        mTextureId = id;
    }

    @Override
    public void release() {
        GLES20.glDisableVertexAttribArray(mVertextPosHandler);
        GLES20.glDisableVertexAttribArray(mTexturePosHandler);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
        GLES20.glDeleteProgram(mProgram);
    }
}
