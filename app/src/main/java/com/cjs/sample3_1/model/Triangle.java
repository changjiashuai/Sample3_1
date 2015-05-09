package com.cjs.sample3_1.model;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.cjs.sample3_1.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by CJS on 15/5/9.
 */
public class Triangle {
    public static float[] mProjMatrix = new float[16]; //4x4投影矩阵
    public static float[] mVMatrix = new float[16]; //摄像机位置
    public static float[] mMVPMatrix; //总变换矩阵

    int mProgram; //自定义渲染管线程序id
    int muMVPMatrixHandle; //总变换矩阵引用id
    int maPositionHandle; //顶点位置属性引用id
    int maColorHandle; //顶点颜色属性引用id
    String mVertexShader; //顶点着色器
    String mFragmentShader; //片元着色器
    static float[] mMMatrix = new float[16]; //具体物体的变换矩阵

    FloatBuffer mVertexBuffer; //顶点坐标数据缓冲
    FloatBuffer mColorBuffer; //顶点颜色数据缓冲
    int vCount = 0;
    float xAngle = 0; //绕x轴旋转的角度

    public Triangle(GLSurfaceView glSurfaceView){
        initVertexData();
        initShader(glSurfaceView);
    }

    /**
     * 初始化顶点坐标数据
     */
    public void initVertexData(){
        vCount = 3;
        final float UNIT_SIZE = 0.2f;
        float vertices[] = new float[]{
                -4 * UNIT_SIZE, 0, 0,
                0, -4 * UNIT_SIZE, 0,
                4 * UNIT_SIZE, 0, 0
        };
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        float colors[] = new float[]{
                1, 1, 1,
                0, 0, 0,
                1, 0, 0,
                1, 0, 0
        };
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        mColorBuffer = cbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);
    }

    /**
     * 初始化shader
     * @param view
     */
    public void initShader(GLSurfaceView view){
        // 加载顶点着色器脚本内容
        mVertexShader = ShaderUtils.loadFromAssetFile("vertex.sh", view.getResources());
        // 加载片元着色器脚本内容
        mFragmentShader = ShaderUtils.loadFromAssetFile("frag.sh", view.getResources());
        // 基于顶点着色器和片元着色器创建程序
        mProgram = ShaderUtils.createProgram(mVertexShader, mFragmentShader);
        // 获取程序中顶点位置属性引用id
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        // 获取程序中顶点颜色属性引用id
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        // 获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    public void drawSelf(){
        // 使用程序
        GLES20.glUseProgram(mProgram);
        // 初始化变换矩阵
        Matrix.setRotateM(mMMatrix, 0, 0, 0, 1, 0);
        // 设置沿Z轴正向位移1
        Matrix.translateM(mMMatrix, 0, 0, 0, 1);
        // 设置绕X轴旋转
        Matrix.rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
        //
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, getFinalMatrix(mMMatrix), 0);
        // 为画笔指定顶点位置数据
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, mColorBuffer);
        // 允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);
        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
    }

    public static float[] getFinalMatrix(float[] spec){
        mMVPMatrix = new float[16];
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        return mMVPMatrix;
    }
}
