package com.mikkku.executable;

import com.mikkku.dip.ImgScale;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author MiskuZero
 * @date 2024/07/27/13:18
 */
public class ImgGUI {
    private final JFrame frame;
    private final JPanel panel;
    private final FileDialog fileDialog;
    private final MouseAdapter mouseAdapter;
    private BufferedImage orgImg;
    private BufferedImage buffImg;
    private BufferedImage buffImg1;
    private double scale;
    private double preScale;
    private int dx;
    private int dy;
    private int x;
    private int y;
    private static final int RENDER_WIDTH = 600;
    private static final int RENDER_HEIGHT = 600;
    private static final double MIN_SCALE = 0.01;
    private static final double MAX_SCALE = 64;
    private static final double SCALE_STEP = 1.05;

    public ImgGUI() {
        frame = new JFrame("Image Processing");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage orgImg = ImgGUI.this.orgImg;
                if (orgImg == null)
                    return;
                super.paintComponent(g); // 调用父类方法以确保背景正确绘制
                double scale = ImgGUI.this.scale;
                if (scale == preScale) {
                    BufferedImage bImg = buffImg;
                    g.drawImage(bImg, x, y, null);
                    g.drawImage(buffImg1, x + 1 + bImg.getWidth(), y, null);
                    return;
                }
                preScale = scale;
                int w = (int) (scale * orgImg.getWidth(null)), h = (int) (scale * orgImg.getHeight(null));
                if (w != 0 && h != 0) {
                    BufferedImage bImg = ImgGUI.this.buffImg = ImgScale.biLinearInterpolation(orgImg, w, h, RENDER_WIDTH, RENDER_HEIGHT);
                    BufferedImage cImg = ImgGUI.this.buffImg1 = ImgScale.castNNI(orgImg, w, h, RENDER_WIDTH, RENDER_HEIGHT);
                    g.drawImage(bImg, x, y, null);
                    g.drawImage(cImg, x + 1 + bImg.getWidth(), y, null);
                }
            }
        };
        fileDialog = new FileDialog(frame, "Select File...");
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int wheelRotation = e.getWheelRotation();
                if (wheelRotation == MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
                    if ((scale /= SCALE_STEP) < MIN_SCALE)
                        scale = MIN_SCALE;
                } else {
                    if ((scale *= SCALE_STEP) > MAX_SCALE)
                        scale = MAX_SCALE;
                }
                System.out.println(scale);
                frame.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                int modifiersEx = e.getModifiersEx();
                if (modifiersEx == MouseEvent.BUTTON1_DOWN_MASK) {
                    dx = e.getX() - x;
                    dy = e.getY() - y;
                    frame.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int modifiersEx = e.getModifiersEx();
                if (modifiersEx == MouseEvent.BUTTON1_DOWN_MASK) {
                    x = e.getX() - dx;
                    y = e.getY() - dy;
                    frame.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int modifiersEx = e.getModifiers();
                if (modifiersEx == MouseEvent.BUTTON1_MASK) {
                    frame.repaint();
                }
            }
        };
        scale = 1.0;
    }

    public void init() {
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        // 添加画布
        frame.add(panel);
        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem chooseFolderItem = new JMenuItem("Open Image...");
        JMenuItem saveItem = new JMenuItem("Save Image...");
        chooseFolderItem.addActionListener(e -> openFileDialog());
        saveItem.addActionListener(e -> saveFile());
        fileMenu.add(chooseFolderItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
        // 绑定事件监听器
        frame.addMouseListener(mouseAdapter);
        frame.addMouseWheelListener(mouseAdapter);
        frame.addMouseMotionListener(mouseAdapter);
        // 设置文件对话框
        fileDialog.setFilenameFilter((dir, name) -> {
            String ext = getExtension(name);
            return ext != null && (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("gif") || ext.equals("bmp") || ext.equals("ico"));
        });
        frame.setVisible(true);
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0)
            return filename.substring(lastDot + 1).toLowerCase();
        return null;
    }

    private void openFileDialog() {
        fileDialog.setMode(FileDialog.LOAD);
        fileDialog.setVisible(true);
        String directory = fileDialog.getDirectory();
        String file = fileDialog.getFile();
        if (directory != null && file != null) {
            File selectedFile = new File(directory, file);
            try {
                orgImg = ImageIO.read(selectedFile);
                buffImg = buffImg1 = orgImg;
                scale = 1.0;
                x = y = 0;
                frame.setTitle("Image Processing: " + selectedFile);
                frame.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile() {
        fileDialog.setMode(FileDialog.SAVE);
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            String dir = fileDialog.getDirectory();
            String file = fileDialog.getFile();
            try {
                File selectedFile = new File(dir, file);
                File selectedFile1 = new File(dir, "_" + file);
                ImageIO.write(buffImg, "png", selectedFile);
                System.out.println("Saving to: " + selectedFile.getAbsolutePath());
                ImageIO.write(buffImg1, "png", selectedFile1);
                System.out.println("Saving to: " + selectedFile1.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ImgGUI().init();
    }
}
