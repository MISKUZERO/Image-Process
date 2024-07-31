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

    public static BufferedImage castNNI(BufferedImage srcImage, int w, int h, int limW, int limH) {
        int wb = Math.min(limW, w), hb = Math.min(limH, h);
        BufferedImage descBuffImg = new BufferedImage(wb, hb, srcImage.getType());
        final double _wScale = (double) srcImage.getWidth(null) / w, _hScale = (double) srcImage.getHeight(null) / h;
        for (int y = 0; y != hb; y++) {
            int _y = (int) (y * _hScale);
            for (int x = 0; x != wb; x++)
                //计算目标图片像素坐标在原图中的坐标
                descBuffImg.setRGB(x, y, srcImage.getRGB((int) (x * _wScale), _y));
        }
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

    public static BufferedImage fastBLI(BufferedImage srcImage, int w, int h, int limW, int limH) {
        int wb = Math.min(limW, w), hb = Math.min(limH, h);
        BufferedImage descBuffImg = new BufferedImage(wb, hb, srcImage.getType());
        int _w = srcImage.getWidth(null), _h = srcImage.getHeight(null);
        double _wScale = (double) _w / w, _hScale = (double) _h / h;
        for (int y = 0; y != hb; y++) {
            double _y = y * _hScale;
            int y1 = (int) _y, y2;
            if ((y2 = y1 + 1) == _h) break;
            for (int x = 0; x != wb; x++) {
                double _x = x * _wScale;
                int x1 = (int) _x, x2;
                if ((x2 = x1 + 1) == _w) break;
                int c1 = srcImage.getRGB(x1, y1), a1 = c1 >>> 24, r1 = (c1 >>> 16) & 0xFF, g1 = (c1 >>> 8) & 0xFF, b1 = c1 & 0xFF;
                int c2 = srcImage.getRGB(x2, y1), a2 = c2 >>> 24, r2 = (c2 >>> 16) & 0xFF, g2 = (c2 >>> 8) & 0xFF, b2 = c2 & 0xFF;
                int c3 = srcImage.getRGB(x1, y2), a3 = c3 >>> 24, r3 = (c3 >>> 16) & 0xFF, g3 = (c3 >>> 8) & 0xFF, b3 = c3 & 0xFF;
                int c4 = srcImage.getRGB(x2, y2), a4 = c4 >>> 24, r4 = (c4 >>> 16) & 0xFF, g4 = (c4 >>> 8) & 0xFF, b4 = c4 & 0xFF;
                double dx1 = _x - x1, dx2 = x2 - _x, dy1 = _y - y1, dy2 = y2 - _y;
                descBuffImg.setRGB(x, y,
                        ((int) (dy2 * (dx1 * a2 + dx2 * a1) + dy1 * (dx1 * a4 + dx2 * a3)) << 24) |
                                ((int) (dy2 * (dx1 * r2 + dx2 * r1) + dy1 * (dx1 * r4 + dx2 * r3)) << 16) |
                                ((int) (dy2 * (dx1 * g2 + dx2 * g1) + dy1 * (dx1 * g4 + dx2 * g3)) << 8) |
                                (int) (dy2 * (dx1 * b2 + dx2 * b1) + dy1 * (dx1 * b4 + dx2 * b3)));
            }
        }
        return descBuffImg;
    }

    public static BufferedImage biLinearInterpolation(BufferedImage srcImage, int w, int h, int limW, int limH) {
        int wb = Math.min(limW, w), hb = Math.min(limH, h);
        BufferedImage descBuffImg = new BufferedImage(wb, hb, srcImage.getType());
        int _w = srcImage.getWidth(null), _h = srcImage.getHeight(null);
        double _wScale = (double) _w / w, _hScale = (double) _h / h;
        for (int y = 0; y != hb; y++) {
            double _y = y * _hScale;
            int y1 = (int) _y, y2 = y1 + 1;
            if (y2 == _h) y2 = y1;
            for (int x = 0; x != wb; x++) {
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
