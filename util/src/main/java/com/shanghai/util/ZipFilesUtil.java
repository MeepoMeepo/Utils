package com.shanghai.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFilesUtil {
	/** 
     * 功能:压缩多个文件成一个zip文件 
     * @param srcfile：源文件列表 
     * @param zipfile：压缩后的文件 
     */  
    public static void zipFiles(File[] srcfile, File zipfile) {  
        byte[] buf = new byte[1024];  
        try {  
            //ZipOutputStream类：完成文件或文件夹的压缩  
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));  
            for (int i = 0; i < srcfile.length; i++) {  
                FileInputStream in = new FileInputStream(srcfile[i]);  
                out.putNextEntry(new ZipEntry(srcfile[i].getName()));  
                int len;  
                while ((len = in.read(buf)) > 0) {  
                    out.write(buf, 0, len);  
                }  
                out.closeEntry();  
                in.close();  
            }  
            out.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}
