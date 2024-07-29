package com.mikkku;

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
    private BufferedImage image;
    private BufferedImage buffImage;
    private double scale;
    private int dX;
    private int dY;
    private static final double MIN_SCALE = 0.01;
    private static final double MAX_SCALE = 64;
    private static final double SCALE_STEP = 1.05;

    public ImgGUI() {
        frame = new JFrame("Image Processing");
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage image = ImgGUI.this.image;
                if (image == null)
                    return;
                int w = (int) (scale * image.getWidth(null)), h = (int) (scale * image.getHeight(null));
                if (w != 0 && h != 0) {
                    super.paintComponent(g); // 调用父类方法以确保背景正确绘制
                    BufferedImage _image = ImgGUI.this.buffImage = ImgScale.biLinearInterpolation(image, w, h);
                    g.drawImage(_image, dX, dY, null);
                    g.drawImage(ImgScale.castNNI(image, w, h), dX + 2 + _image.getWidth(), dY, null);
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
                    frame.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int modifiersEx = e.getModifiersEx();
                if (modifiersEx == MouseEvent.BUTTON1_DOWN_MASK) {
                    dX = e.getX();
                    dY = e.getY();
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
        frame.setSize(1600, 1000);
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
                image = ImageIO.read(selectedFile);
                scale = 1.0;
                dX = dY = 0;
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
            File selectedFile = new File(dir, file);
            try {
                ImageIO.write(buffImage, "png", selectedFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Saving to: " + selectedFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        new ImgGUI().init();
    }
}
