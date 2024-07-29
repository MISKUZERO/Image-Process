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

    public static BufferedImage scale(BufferedImage srcImage, int w, int h) {
        BufferedImage descBuffImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int _w = srcImage.getWidth(null), _h = srcImage.getHeight(null);
        double _wScale = (double) _w / w, _hScale = (double) _h / h;
        for (int y = 0; y != h; y++)
            for (int x = 0; x != w; x++)
                //计算目标图片像素坐标在原图中的坐标
                descBuffImg.setRGB(x, y, srcImage.getRGB((int) (x * _wScale), (int) (y * _hScale)));
        return descBuffImg;
    }

    public static BufferedImage scaleRound(BufferedImage srcImage, int w, int h) {
        BufferedImage descBuffImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
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
}
