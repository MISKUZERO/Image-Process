package com.mikkku;

import com.mikkku.util.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class ImgSearch {

    private static final int PIXELS = 8;
    private static final double SIMILARITY = 0.75;
    private static final int THRESHOLD = (int) (PIXELS * PIXELS * SIMILARITY);

    private static final byte[] GREYS = new byte[PIXELS * PIXELS];
    private static final TreeSet<String> RESULTS = new TreeSet<>(Comparator.reverseOrder());
    private static long descHash;

    public static Collection<String> search(File directory, File img) {
        try {
            descHash = hash(ImageIO.read(img));
        } catch (IOException ioException) {
            System.err.println("无效的文件：" + img);
            System.exit(1);
        }
        try {
            FileUtils.recursionForeach(directory, (file -> {
                if (isImageFile(file)) {
                    double match = -1;
                    try {
                        match = match(hash(ImageIO.read(file)), descHash);
                    } catch (Exception e) {
                        System.err.println("无效的文件：" + file);
                    }
                    if (match != -1)
                        RESULTS.add(match + ": " + file);
                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
        return RESULTS;
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

    public static long hash(BufferedImage srcImage) {
        int _w = srcImage.getWidth(null), _h = srcImage.getHeight(null);
        final double _wScale = (double) _w / PIXELS, _hScale = (double) _h / PIXELS;
        int sum = 0, size = 0;
        for (int y = 0; y != PIXELS; y++) {
            int _y = (int) (y * _hScale);
            for (int x = 0; x != PIXELS; x++) {
                int rgb = srcImage.getRGB((int) (x * _wScale), _y);
                // (0 ~ 255) / 4 -> 0 ~ 63
                // 256 -> 64级灰度
                int grey = (((rgb & 0xff) + ((rgb >>> 8) & 0xff) + ((rgb >>> 16) & 0xff)) / 3) >> 2;
                GREYS[size++] = (byte) grey;
                sum += grey;
            }
        }
        int avg = sum / (PIXELS * PIXELS);
        long res = 0;
        for (int i = 0; i != PIXELS * PIXELS; i++)
            if (GREYS[i] > avg)
                res += (1L << i);
        return res;
    }

    public static double match(long hash1, long hash2) {
        long res = hash1 ^ hash2;
        int missCnt = 0, quitThreshold = PIXELS * PIXELS - THRESHOLD;
        for (int i = 0; i != PIXELS * PIXELS; i++) {
            if (((res >>> i) & 1) == 1)
                missCnt++;
            if (missCnt == quitThreshold) return -1;
        }
        return (double) (PIXELS * PIXELS - missCnt) / (PIXELS * PIXELS);
    }

}
