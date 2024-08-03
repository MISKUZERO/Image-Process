package com.mikkku;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ImagePreviewer extends JFrame {

    private final List<String> imagePathList;
    private final JPanel previewPanel;
    private int currentImageIndex;

    private final int thumbnailWidth = 100;
    private final int thumbnailHeight = 100;

    private static final int H_GAP = 1;
    private static final int V_GAP = 1;

    public ImagePreviewer(List<String> imagePathList) {
        this.imagePathList = imagePathList;
        setTitle("Image Previewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, H_GAP, V_GAP));
        int count = 5;//每行显示图片数
        int preferredWidth = count * (thumbnailWidth + H_GAP) + H_GAP;
        int preferredHeight = (thumbnailHeight + V_GAP) * (int) Math.ceil((double) imagePathList.size() / count) + V_GAP;
        previewPanel.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        JScrollPane scrollPane = new JScrollPane(previewPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // 获取垂直滚动条
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        // 设置垂直滚动条的单位增量和块增量
        verticalScrollBar.setUnitIncrement(16); // 每次点击滚动箭头时滚动 16 个像素
        verticalScrollBar.setBlockIncrement(64); // 滚动鼠标滚轮一次滚动 64 个像素
        populatePreviewPanel();

        add(scrollPane);
        pack(); // Adjust size based on content
        setSize(preferredWidth + 34, count * (thumbnailHeight + V_GAP) + 40);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void populatePreviewPanel() {
        int size = imagePathList.size();
        for (int i = 0; i != size; i++) {
            String imagePath = imagePathList.get(i);
            try {
                BufferedImage image = ImageIO.read(new File(imagePath));
                BufferedImage thumbnail = createThumbnail(image);
                JLabel label = new JLabel(new ImageIcon(thumbnail));
                label.addMouseListener(new ThumbnailClickListener(image, i));
                previewPanel.add(label);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to load image: " + imagePath, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private BufferedImage createThumbnail(BufferedImage originalImage) {
        BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumbnail.createGraphics();
        g2d.drawImage(originalImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
        g2d.dispose();
        return thumbnail;
    }

    private class ThumbnailClickListener implements MouseListener {
        private final BufferedImage originalImage;
        private final int index;

        public ThumbnailClickListener(BufferedImage originalImage, int index) {
            this.originalImage = originalImage;
            this.index = index;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) { // Double-click detected
                currentImageIndex = index;
                showOriginalImage(originalImage);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    private void showOriginalImage(BufferedImage image) {
        JFrame frame = new JFrame("View Original Image");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        JLabel label = new JLabel(new ImageIcon(image));
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");

        panel.add(label);
        panel.add(prevButton);
        panel.add(nextButton);

        prevButton.addActionListener(e -> {
            if (currentImageIndex > 0) {
                currentImageIndex--;
                updateImage();
            }
        });

        nextButton.addActionListener(e -> {
            if (currentImageIndex < imagePathList.size() - 1) {
                currentImageIndex++;
                updateImage();
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void updateImage() {
        try {
            BufferedImage image = ImageIO.read(new File(imagePathList.get(currentImageIndex)));
            showOriginalImage(image);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load image: " + imagePathList.get(currentImageIndex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        String path = "C:\\Users\\bx001\\Desktop";
        String img = "C:\\Users\\bx001\\Desktop\\.trashed-1672458607-IMG_20221201_114947.jpg";
        long t = System.currentTimeMillis();
        Collection<String> search = ImgSearch.search(new File(path), new File(img));
        System.out.println("time: " + (System.currentTimeMillis() - t) + "ms");
        List<String> collect = search.stream().map(s -> {
            System.out.println(s);
            return s.substring(s.indexOf(':') + 2);
        }).collect(Collectors.toList());
        SwingUtilities.invokeLater(() -> new ImagePreviewer(collect));
    }
}