package com.example.opengldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private IDrawer drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glSurfaceView = findViewById(R.id.gl_surface);
//        drawer = new TriangleDrawer();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_pic);
        drawer = new BitmapDrawer(bitmap);
        initRender(drawer);
    }


    private void initRender(IDrawer drawer) {
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new SimpleRender(drawer));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawer.release();
    }
}