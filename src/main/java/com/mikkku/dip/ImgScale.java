package com.mikkku.dip;

import java.awt.image.BufferedImage;

/**
 * @author MiskuZero
 * @date 2024/07/27/12:41
 */
public class ImgScale {

    public static final int A_MASK = 0xff000000;
    public static final int R_MASK = 0x00ff0000;
    public static final int G_MASK = 0x0000ff00;
    public static final int B_MASK = 0x000000ff;

    public static BufferedImage castNNI(BufferedImage srcImage, int w, int h) {
        BufferedImage descBuffImg = new BufferedImage(w, h, srcImage.getType());
        int _w = srcImage.getWidth(null), _h = srcImage.getHeight(null);
        double _wScale = (double) _w / w, _hScale = (double) _h / h;
        for (int y = 0; y != h; y++)
            for (int x = 0; x != w; x++)
                //计算目标图片像素坐标在原图中的坐标
                descBuffImg.setRGB(x, y, srcImage.getRGB((int) (x * _wScale), (int) (y * _hScale)));
        return descBuffImg;
    }

    @Deprecated
    public static BufferedImage roundNNI(BufferedImage srcImage, int w, int h) {
        BufferedImage descBuffImg = new BufferedImage(w, h, srcImage.getType());
        int _w = srcImage.getWidth(null), _h = srcImage.getHeight(null);
        double _wScale = (double) _w / w, _hScale = (double) _h / h;
        for (int y = 0; y != h; y++) {
            for (int x = 0; x != w; x++) {
                //计算目标图片像素坐标在原图中的坐标
                int _x = (int) Math.round(x * _wScale), _y = (int) Math.round(y * _hScale);
                if (_x == _w)
                    _x--;
                if (_y == _h)
                    _y--;
                descBuffImg.setRGB(x, y, srcImage.getRGB(_x, _y));
            }
        }
        return descBuffImg;
    }

    public static BufferedImage _biLinearInterpolation(BufferedImage originalImage, int newWidth, int newHeight) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        for (int y = 0; y != newHeight; y++) {
            for (int x = 0; x != newWidth; x++) {
                double sx = (double) x / newWidth * width;
                double sy = (double) y / newHeight * height;
                resizedImage.setRGB(x, y, interpolateColor(sx, sy, originalImage, width, height));
            }
        }
        return resizedImage;
    }

    private static int interpolateColor(double sx, double sy, BufferedImage image, int width, int height) {
        int x0 = (int) sx, y0 = (int) sy;
        int x1 = Math.min(x0 + 1, width - 1), y1 = Math.min(y0 + 1, height - 1);

        double dx = sx - x0;
        double dy = sy - y0;

        int c0 = image.getRGB(x0, y0), r0 = (c0 >> 16) & 0xFF, g0 = (c0 >> 8) & 0xFF, b0 = c0 & 0xFF;
        int c1 = image.getRGB(x1, y0), r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int c2 = image.getRGB(x0, y1), r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        int c3 = image.getRGB(x1, y1), r3 = (c3 >> 16) & 0xFF, g3 = (c3 >> 8) & 0xFF, b3 = c3 & 0xFF;

        int tr = (int) ((1 - dx) * ((1 - dy) * r0 + dy * r2) + dx * ((1 - dy) * r1 + dy * r3));
        int tg = (int) ((1 - dx) * ((1 - dy) * g0 + dy * g2) + dx * ((1 - dy) * g1 + dy * g3));
        int tb = (int) ((1 - dx) * ((1 - dy) * b0 + dy * b2) + dx * ((1 - dy) * b1 + dy * b3));

        return A_MASK + (tr << 16) + (tg << 8) + tb;
    }

    public static BufferedImage biLinearInterpolation(BufferedImage srcImage, int w, int h) {
        BufferedImage descBuffImg = new BufferedImage(w, h, srcImage.getType());
        int _w = srcImage.getWidth(null), _h = srcImage.getHeight(null);
        double _wScale = (double) _w / w, _hScale = (double) _h / h;
        for (int y = 0; y != h; y++) {
            double _y = y * _hScale;
            int y1 = (int) _y, y2 = y1 + 1;
            if (y2 == _h) y2 = y1;
            for (int x = 0; x != w; x++) {
                double _x = x * _wScale;
                int x1 = (int) _x, x2 = x1 + 1;
                if (x2 == _w) x2 = x1;
                int c1 = srcImage.getRGB(x1, y1), c2 = srcImage.getRGB(x2, y1), c3 = srcImage.getRGB(x1, y2), c4 = srcImage.getRGB(x2, y2);
                int a = interpolation(_x, _y, x1, y1, x2, y2, (c1 & A_MASK) >>> 24, (c2 & A_MASK) >>> 24, (c3 & A_MASK) >>> 24, (c4 & A_MASK) >>> 24);
                int r = interpolation(_x, _y, x1, y1, x2, y2, (c1 & R_MASK) >>> 16, (c2 & R_MASK) >>> 16, (c3 & R_MASK) >>> 16, (c4 & R_MASK) >>> 16);
                int g = interpolation(_x, _y, x1, y1, x2, y2, (c1 & G_MASK) >>> 8, (c2 & G_MASK) >>> 8, (c3 & G_MASK) >>> 8, (c4 & G_MASK) >>> 8);
                int b = interpolation(_x, _y, x1, y1, x2, y2, c1 & B_MASK, c2 & B_MASK, c3 & B_MASK, c4 & B_MASK);
                descBuffImg.setRGB(x, y, (a << 24) + (r << 16) + (g << 8) + b);
            }
        }
        return descBuffImg;
    }

    private static int interpolation(double x, double y, int x1, int y1, int x2, int y2, int c1, int c2, int c3, int c4) {
        if (x1 == x2) {
            if (y1 == y2)
                return c1;
            return (int) ((y2 - y) * c1 + (y - y1) * c3);
        }
        double dx1 = x - x1, dx2 = x2 - x;
        if (y1 == y2)
            return (int) (dx1 * c2 + dx2 * c1);
        return (int) ((y2 - y) * (dx1 * c2 + dx2 * c1) + (y - y1) * (dx1 * c4 + dx2 * c3));
    }

}
