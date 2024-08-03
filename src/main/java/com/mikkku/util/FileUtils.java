package com.mikkku.util;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author MiskuZero
 * @date 2024/08/03/15:22
 */
public class FileUtils {

    public static void recursionForeach(File directory, Consumer<File> consumer) throws IOException {
        File[] files = directory.listFiles();
        if (files == null)
            throw new IOException("IO错误：" + directory);
        for (File file : files)
            if (file.isDirectory())
                recursionForeach(file, consumer);// 递归处理子目录
            else
                consumer.accept(file);
    }

    public static boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") ||
                fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") ||
                fileName.endsWith(".gif") ||
                fileName.endsWith(".bmp") ||
                fileName.endsWith(".ico");
    }
}
