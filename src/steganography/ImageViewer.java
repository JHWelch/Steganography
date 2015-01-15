package steganography;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A step between StegGUI and Steganography
 * Holds the image that is being manipulated and displays it in the GUI.
 * <br>
 * <img src="ImageViewer.png" alt="UML class diagram"/>
 * @author Jordan Welch
 * @version Feb 14, 2011
 */
public class ImageViewer extends JPanel {
    private BufferedImage image;


    public ImageViewer(){
        this.setBackground(Color.DARK_GRAY);

    }
    @Override
    public void paintComponent(Graphics g) {
        if (image != null) {
            // {Begin} Just create Image
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            double panelWidth = this.getWidth();
            double imageWidth = image.getWidth(this);

            double panelHeight = this.getHeight();
            double imageHeight = image.getHeight(this);

            AffineTransform scale = new AffineTransform();
            scale.setToScale(panelWidth / imageWidth,
                    panelHeight / imageHeight);
            //{End} Just create Image

            g2d.drawImage(image, scale, this);
        }// if
    }// paintComponent(Graphics)
    /**
     * Encodes the image held in this frame with a message
     * @param message Message to Encode
     */
    public void manipImage(String message) {
        Steganography.encodeImage(message, image);
    }//manipImage(String)

    public void setImage(BufferedImage image){
        this.image = image;
    }// setImage ( BufferedImage )

    /**
     * Gets the message encoded in the image
     * @return The encoded message
     */
    public String getMessage(){
        return Steganography.decodeImage(image);
    }
    /**
     * Getter
     * @return The private variable image
     */
    public BufferedImage getImage() {
        return image;
    }

}
