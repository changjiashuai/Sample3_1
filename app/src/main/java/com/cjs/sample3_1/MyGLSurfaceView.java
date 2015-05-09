package com.cjs.sample3_1;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.cjs.sample3_1.model.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by CJS on 15/5/9.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    final float ANGLE_SPAN = 0.375f;
    RotateThread rotateThread;
    SceneRenderer sceneRenderer;
    Triangle triangle;

    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        sceneRenderer = new SceneRenderer();
        setRenderer(sceneRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private class SceneRenderer implements GLSurfaceView.Renderer{
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0, 0, 0, 1.0f);
            //创建三角形对象
            triangle = new Triangle(MyGLSurfaceView.this);
            //打开深度检测
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            rotateThread = new RotateThread();
            rotateThread.start();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES20.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            float ratio = (float)width / height;
            //计算透视投影矩阵
            Matrix.frustumM(Triangle.mProjMatrix, 0, -ratio, ratio, -1, 1, 1, 10);
            //设置相机矩阵
            Matrix.setLookAtM(Triangle.mVMatrix, 0, 0, 0, 3, 0, 0, 0, 0, 1, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //清除深度缓冲与颜色缓冲
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            triangle.drawSelf();
        }
    }

    public class RotateThread extends Thread{
        @Override
        public void run() {
            while (true) {
                triangle.xAngle = triangle.xAngle + ANGLE_SPAN;
                try {
                    Thread.sleep(20);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
