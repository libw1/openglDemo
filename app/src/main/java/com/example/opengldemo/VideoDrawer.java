package com.example.opengldemo;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * created by libowen
 * on 2023/3/8
 */
public class VideoDrawer implements IDrawer {
    //顶点坐标 世界坐标系
    private float[] mVertexCoors = new float[]{
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };

    //纹理坐标 Android坐标系
    private float[] mTextureCoors = new float[]{
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private int mTextureId = -1;//纹理id
    private int mProgram = -1;//opengl程序id
    private int mVertextPosHandler = -1;//顶点坐标接受者
    private int mTexturePosHandler = -1;//纹理坐标接受者
    // 纹理接收者
    private int mTextureHandler = -1;

    //矩阵变换接收者
    private int mVertexMatrixHandler = -1;

    private FloatBuffer mVertextBuffer;//顶点坐标
    private FloatBuffer mTextureBuffer;//纹理坐标

    private int mWorldWidth = -1;
    private int mWorldHeight = -1;
    private int mVideoWidth = -1;
    private int mVideoHeight = -1;

    private float[] mMatrix;//变化矩阵

    private SurfaceTexture mSurfaceTexture = null;//
    private OnInitListener listener;


    public VideoDrawer() {
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
        this.listener = listener;
    }

    @Override
    public void draw() {
        if (mTextureId != -1) {
            //【步骤2: 创建、编译并启动OpenGL着色器】
            createGlPrg();
            //-------【注4：新增两个步骤】-------
            //【步骤3: 激活并绑定纹理单元】
            activateTexture();
            //【步骤4: 绑定图片到纹理单元】
            updateTexture();

            ////【步骤5: 开始渲染绘制】
            doDraw();
        }
    }

    private void updateTexture() {
        mSurfaceTexture.updateTexImage();
    }

    private void activateTexture() {
        //激活指定单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定纹理ID到纹理单元
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);
        //将激活的纹理单元传递到着色器
        GLES20.glUniform1f(mTextureHandler, 0);

        //配置边缘过渡参数
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
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
            mTextureHandler = GLES20.glGetUniformLocation(mProgram, "uTexture");
        }
        GLES20.glUseProgram(mProgram);
    }

    private String getFragmentShader() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;" +
                "uniform samplerExternalOES uTexture;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "  vec4 color = texture2D(uTexture, vCoordinate);" +
//                "  gl_FragColor = color;" +
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
                "attribute vec2 aCoordinate;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "  gl_Position = aPosition;" +
                "  vCoordinate = aCoordinate;" +
                "}";
    }

    private void doDraw() {
        GLES20.glEnableVertexAttribArray(mVertextPosHandler);
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);

        //设置着色器参数， 第二个参数表示一个顶点包含的数据数量，这里为xy，所以为2
        GLES20.glVertexAttribPointer(mVertextPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertextBuffer);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0 ,4);
    }

    @Override
    public void setTextureId(int id) {
        mTextureId = id;
        mSurfaceTexture = new SurfaceTexture(id);
        if (listener != null) {
            listener.onInitSurfaceTexture(mSurfaceTexture);
        }
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
