package com.cjs.sample3_1.utils;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * 加载顶点Shader与片元Shader的工具类
 * <p/>
 * Created by CJS on 15/5/8.
 */
public class ShaderUtils {

    private static final String TAG = "ES20_ERROR";

    /**
     * 创建一个新Shader
     *
     * @param shaderType 1.GLES20.GL_VERTEX_SHADER（顶点）2.GLES20.GL_FRAGMENT_SHADER(片元)
     * @param source     shader的字符串脚本
     * @return
     */
    public static int loadShader(int shaderType, String source) {
        //创建一个新的Shader
        int shader = GLES20.glCreateShader(shaderType);
        //创建成功加载Shader
        if (shader != 0) {
            //加载shader的源码
            GLES20.glShaderSource(shader, source);
            //编译shader
            GLES20.glCompileShader(shader);
            //存放编译成功shader数量的数组
            int[] compiled = new int[1];
            //获取shader的编译情况
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            //编译失败 输出日志并删除此shader
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * @param vertexSource   顶点源码
     * @param fragmentSource 片元源码
     * @return 链接的程序 0：失败
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        // 加载顶点着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        // 加载片元着色器
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            return 0;
        }
        // 创建程序
        int program = GLES20.glCreateProgram();
        // 程序创建成功 向程序加入顶点着色器和片元着色器
        if (program != 0) {
            // 向程序加入顶点着色器
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            // 向程序加入片元着色器
            GLES20.glAttachShader(program, fragmentShader);
            checkGlError("glAttachShader");
            // 连接程序
            GLES20.glLinkProgram(program);
            // 存放链接成功program数量的数组
            int[] linkStatus = new int[1];
            // 获取program链接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            // 链接失败则报错并删除程序
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    /**
     * @param op 操作名称
     */
    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    /**
     * 从Asset文件夹加载Shader
     *
     * @param fname     文件名
     * @param resources
     * @return shader 字符串
     */
    public static String loadFromAssetFile(String fname, Resources resources) {
        String result = null;
        try {
            InputStream inputStream = resources.getAssets().open(fname);
            int ch = 0;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((ch = inputStream.read()) != -1) {
                byteArrayOutputStream.write(ch);
            }
            byte[] buff = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            inputStream.close();
            result = new String(buff, "UTF-8");
            result = result.replaceAll("\\r\\n", "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
