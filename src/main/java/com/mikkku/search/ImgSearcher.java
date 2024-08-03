package com.mikkku.search;

import com.mikkku.util.FileUtils;
import com.mikkku.util.digest.LongDigest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * @author MiskuZero
 * @date 2024/8/3 2:24
 */
public class ImgSearcher implements LongDigest<BufferedImage>, FileSearcher<BiConsumer<Double, File>> {

    private double similarity;//相似度（重合度）
    private int scale;//精确度
    private int threshold;
    private long descHash;
    private byte[] greys;

    public ImgSearcher() {
        this(8, 0.75);
    }

    public ImgSearcher(int scale, double similarity) {
        updateThresholdAndGreys(this.scale = scale, this.similarity = similarity);
    }

    public void setSimilarity(double similarity) {
        updateThresholdAndGreys(scale, this.similarity = similarity);
    }

    public void setScale(int scale) {
        updateThresholdAndGreys(this.scale = scale, similarity);
    }

    private void updateThresholdAndGreys(int scale, double similarity) {
        this.threshold = (int) (scale * scale * similarity);
        this.greys = new byte[scale * scale];
    }

    @Override
    public void search(File directory, File img, BiConsumer<Double, File> biConsumer) {
        try {
            descHash = hash(ImageIO.read(img));
        } catch (IOException ioException) {
            System.err.println("无效的文件：" + img);
            System.exit(1);
        }
        try {
            FileUtils.recursionForeach(directory, (file -> {
                if (FileUtils.isImageFile(file)) {
                    double similarity = -1;
                    try {
                        int len = greys.length;
                        similarity = LongDigest.match(hash(ImageIO.read(file)), descHash, len, len - threshold);
                    } catch (Exception e) {
                        System.err.println("无效的文件：" + file);
                    }
                    if (similarity != -1)
                        biConsumer.accept(similarity, file);
                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    @Override
    public Long hash(BufferedImage srcImage) {
        double _wScale = (double) srcImage.getWidth(null) / scale,
                _hScale = (double) srcImage.getHeight(null) / scale;
        int size = 0, sum = 0;
        for (int y = 0; y != scale; y++)
            for (int x = 0, _y = (int) (y * _hScale); x != scale; x++) {
                int rgb = srcImage.getRGB((int) (x * _wScale), _y);
                // (0 ~ 255) / 4 -> 0 ~ 63
                // 256 -> 64级灰度
                int grey = (((rgb & 0xff) + ((rgb >>> 8) & 0xff) + ((rgb >>> 16) & 0xff)) / 3) >> 2;
                greys[size++] = (byte) grey;
                sum += grey;
            }
        int avg = sum / size;
        long res = 0;
        for (int i = 0; i != size; i++)
            if (greys[i] > avg)
                res += (1L << i);
        return res;
    }

}
