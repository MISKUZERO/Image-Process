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

    public static BufferedImage biLinearInterpolation(BufferedImage srcImage, int w, int h) {
        BufferedImage descBuffImg = new BufferedImage(w, h, srcImage.getType());
        int _w = srcImage.getWidth(null), _h = srcImage.getHeight(null);
        double _wScale = (double) _w / w, _hScale = (double) _h / h;
        for (int y = 0; y != h; y++) {
            for (int x = 0; x != w; x++) {
                //计算目标图片像素坐标在原图中的坐标
                double _x = x * _wScale, _y = y * _hScale;
                int x1 = (int) _x, x2 = x1 + 1, y1 = (int) _y, y2 = y1 + 1;
                double dx1 = _x - x1, dx2 = x2 - _x;
                double vm, vn = 0;
                if (x2 == _w)
                    vm = srcImage.getRGB(x1, y1);
                else
                    vm = dx1 * srcImage.getRGB(x2, y1) + dx2 * srcImage.getRGB(x1, y1);
                if (x2 != _w && y2 != _h)
                    vn = dx1 * srcImage.getRGB(x2, y2) + dx2 * srcImage.getRGB(x1, y2);
                descBuffImg.setRGB(x, y, (int) Math.round((_y - y1) * vm + (y2 - _y) * vn));
            }
        }
        return descBuffImg;
    }
}
