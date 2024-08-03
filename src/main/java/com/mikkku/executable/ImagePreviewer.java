package com.mikkku.executable;

import com.mikkku.search.ImgSearcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author MiskuZero
 * @date 2024/8/3 2:24
 */
public class ImagePreviewer extends JFrame {

    private final List<Object[]> imagePathSimilarityList;
    private final JPanel previewPanel;
    private int currentImageIndex;

    private final int thumbnailWidth = 200;
    private final int thumbnailHeight = 200;

    private static final int H_GAP = 1;
    private static final int V_GAP = 1;

    public ImagePreviewer(List<Object[]> imagePathSimilarityList) {
        this.imagePathSimilarityList = imagePathSimilarityList;
        setTitle("Image Previewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, H_GAP, V_GAP));
        int count = 5;//每行显示图片数
        int preferredWidth = count * (thumbnailWidth + H_GAP) + H_GAP;
        int preferredHeight = (thumbnailHeight + V_GAP) * (int) Math.ceil((double) imagePathSimilarityList.size() / count) + V_GAP;
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
        int i = 0;
        for (Object[] objects : imagePathSimilarityList) {
            File imagePath = (File) objects[1];
            try {
                BufferedImage image = ImageIO.read(imagePath);
                BufferedImage thumbnail = createThumbnail(image);
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                // 创建图片标签
                JLabel imageLabel = new JLabel(new ImageIcon(thumbnail));
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.gridwidth = 2; // 让图片占据两列
                gbc.weightx = 1;
                gbc.weighty = 1;
                gbc.fill = GridBagConstraints.BOTH;
                panel.add(imageLabel, gbc);
                // 创建文本标签
                JLabel textLabel = new JLabel(objects[0].toString(), JLabel.RIGHT);
                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.gridwidth = 1;
                gbc.weightx = 0;
                gbc.weighty = 0;
                gbc.anchor = GridBagConstraints.LINE_END;
                gbc.fill = GridBagConstraints.NONE;
                panel.add(textLabel, gbc);
                // 设置容器的背景色，以便与图片区分开
                panel.setBackground(Color.WHITE);
                panel.addMouseListener(new ThumbnailClickListener(image, i++));
                previewPanel.add(panel);
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
            if (currentImageIndex < imagePathSimilarityList.size() - 1) {
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
        File imagePath = (File) imagePathSimilarityList.get(currentImageIndex)[1];
        try {
            showOriginalImage(ImageIO.read(imagePath));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load image: " + imagePath, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        File path = new File("C:\\Users\\Administrator\\Desktop");
        File imagePath = new File("C:\\Users\\Administrator\\Desktop\\bf5dfe57gy1hr5cujv6hyj22tc480hdw.jpg");
        ImgSearcher imgSearch = new ImgSearcher();
        List<Object[]> imagePathSimilarityList = new ArrayList<>();
        long t = System.currentTimeMillis();
        imgSearch.search(path, imagePath, (similarity, image) -> {
            System.out.println(similarity + ":" + image);
            imagePathSimilarityList.add(new Object[]{similarity, image});
        });
        System.out.println("time: " + (System.currentTimeMillis() - t) + "ms");
        SwingUtilities.invokeLater(() -> new ImagePreviewer(imagePathSimilarityList));
    }
}