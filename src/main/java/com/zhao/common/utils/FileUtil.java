package com.zhao.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    private FileUtil(){}

    /**
     * 保存文件，完成之后会删除源文件
     * @param source 源文件
     * @param target 目标文件，如果目标文件已存在会覆盖
     */
    public static void transferTo(File source, File target){
        transferTo(source, target, false, true);
    }

    /**
     * 保存文件，完成之后会删除源文件
     * @param source 源文件
     * @param target 目标文件
     * @param isAppend 是否是以追加的方式保存到目标文件
     */
    public static void transferTo(File source, File target, boolean isAppend){
        transferTo(source, target, isAppend, true);
    }

    /**
     * 保存文件
     * @param source 源文件
     * @param target 目标文件
     * @param isAppend 是否是以追加的方式保存到目标文件
     * @param delSource 完成之后是否删除源文件
     */
    public static void transferTo(File source, File target, boolean isAppend, boolean delSource){
        FileInputStream fileInputStream = null;
        FileOutputStream outputStream = null;
        if (target.getParent() != null){
            File targetDir = new File(target.getParent());
            if (!targetDir.exists()){
                targetDir.mkdirs();
            }
        }
        byte[] byt = new byte[10 * 1024 * 1024];
        int len;
        try {
            outputStream = new FileOutputStream(target, isAppend);
            fileInputStream = new FileInputStream(source);
            while ((len = fileInputStream.read(byt)) != -1) {
                outputStream.write(byt, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null){
                    fileInputStream.close();
                    fileInputStream = null;
                }
                if (outputStream != null){
                    outputStream.close();
                    outputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (delSource)
                source.delete();
        }

    }

}
