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

    public ImagePreviewer(List<String> imagePathList) {
        this.imagePathList = imagePathList;
        setTitle("Image Previewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        previewPanel = new JPanel(new GridLayout(0, 4, 5, 5)); // 4 columns, 5 pixels gap
        previewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(previewPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        populatePreviewPanel();

        add(scrollPane);
        pack(); // Adjust size based on content
        setVisible(true);
    }

    private void populatePreviewPanel() {
        for (int i = 0; i < imagePathList.size(); i++) {
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
        int thumbnailWidth = 150;
        int thumbnailHeight = 150;
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
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);

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
        String path = "C:\\Users\\Administrator\\Desktop\\recent";
        String img = "C:\\Users\\Administrator\\Desktop\\20220828120956_05aec.jpg";
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