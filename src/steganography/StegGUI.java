package steganography;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A Basic GUI to implements the {@link Steganography} class and shows
 * basically how it works.  Currently is not as beautiful as I would like it
 * to be, but is in a more basic version.
 * <br>
 * <img src="StegGUI.png" alt="UML class diagram"/>
 * <a href="../../launch.html">Run program.</a>
 * <a href="../../../slides.pdf">Presentation Slides</a>
 * @author Jordan Welch
 * @version Feb 14, 2011
 */
public class StegGUI extends JFrame implements ActionListener, KeyListener {

    private static final Logger logger = Logger.getLogger("StegGUI");
    private static final int GUI_WIDTH = 400;
    private static final int GUI_HEIGHT = 1000;
    /**
     * Menu Items
     */
    private static final String GUI_TITLE = "Steganography";

    /**
     * GUI Components
     */
    private JButton btnEncodeMessage,
            btnSelectImage,
            btnDecodeMessage;
    private JLabel lblCharactersLeft,
            lblImagePreview;
    private JTextArea txtInput;
    private static final String ENCODE_MESSAGE = "Encode Message";
    private static final String SELECT_IMAGE = "Select Image";
    private static final String CHARS_LEFT = "Characters Left: ";
    private static final String IMAGE_PREVIEW = "Image Preview:";
    private static final String INPUT = "Input";
    private static final String DECODE_MESSAGE = "Decode Message";
    private static final String SAVE_ENCODED = "Save Encoded Image";
    private static final String SAVE = "Save";
    /**
     * A preview of the image with the encoded message.
     */
    private ImageViewer previewImage;

    /**
     * Creates a new GUI and creates the Menus associated with it.
     */
    public StegGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(GUI_WIDTH, GUI_HEIGHT);
        this.setTitle(GUI_TITLE);


        Container pane = this.getContentPane();

        GridLayout grid = new GridLayout(7, 2);
        pane.setLayout(grid);

        previewImage = new ImageViewer();

        txtInput = new JTextArea();
        txtInput.addKeyListener(this);
        txtInput.setLineWrap(true);


        lblCharactersLeft = new JLabel(getCharsLeft());

        btnEncodeMessage = new JButton(ENCODE_MESSAGE);
        btnEncodeMessage.addActionListener(this);
        btnEncodeMessage.setActionCommand(ENCODE_MESSAGE);


        btnSelectImage = new JButton(SELECT_IMAGE);
        btnSelectImage.addActionListener(this);
        btnSelectImage.setActionCommand(SELECT_IMAGE);

        btnDecodeMessage = new JButton(DECODE_MESSAGE);
        btnDecodeMessage.addActionListener(this);
        btnDecodeMessage.setActionCommand(DECODE_MESSAGE);

        lblImagePreview = new JLabel(IMAGE_PREVIEW);

        //Add in nice order
        pane.add(lblCharactersLeft);
        pane.add(txtInput);
        pane.add(btnSelectImage);
        pane.add(btnEncodeMessage);
        pane.add(lblImagePreview);
        pane.add(previewImage);
        pane.add(btnDecodeMessage);

        this.setVisible(true);

        generalTesting();
    }// PosterGUI()

    private String getCharsLeft() {
        logger.log(Level.INFO, "" + txtInput.getText().toCharArray().length);
        return CHARS_LEFT + (1000 - txtInput.getText().length());
    }

    /**
     * Method for testing purposes only
     */
    private static void generalTesting() {
        logger.log(Level.INFO, "The value of A is " + Character.getNumericValue('A'));
        logger.log(Level.INFO, "The value of B is " + Character.getNumericValue('B'));
        logger.log(Level.INFO, "The value of Z is " + Character.getNumericValue('Z'));
        logger.log(Level.INFO, "The value of a space is " + Character.getNumericValue('0'));

        System.out.println("");
    }//generalTesting() 

    /**
     * Begins running the program
     * @param args - Command line arguments not used currently.
     */
    public static void main(String[] args) {
        StegGUI theGUI = new StegGUI();

        //Logger!
        Steganography.setLoggerLevel(Level.WARNING);
        logger.setLevel(Level.WARNING);
    }// main( String[] )

    /**
     * Code Stolen From ImageReadWrite
     */
    private BufferedImage localFile() {
        // Create a pop-up window that displays files
        // and directories from which we can select an
        // image file.
        JFileChooser chooser = new JFileChooser();

        // Stipulate that we are only interested in looking
        // at files with the extensions ",jpg", ".jpeg", and ".png".
        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("PNG and JPG files", "jpeg", "jpg", "png");
        chooser.setFileFilter(filter);

        // Pop up the file chooser window and wait for
        // user to select a file.
        int status = chooser.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                // Read image file and create a BufferedImage from
                // the data.
                BufferedImage image = ImageIO.read(file);

                //Returns the Image
                return image;
            } // try
            catch (IOException e) {
                System.out.println(e.getMessage());
            } // catch( IOException )
        } // if
        return null;
    }//localFile()

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals(ENCODE_MESSAGE)) {
            previewImage.manipImage(txtInput.getText());
            SaveLocal();
        } else if (command.equals(SELECT_IMAGE)) {
            previewImage.setImage(localFile());
        } else if (command.equals(DECODE_MESSAGE)){
            JOptionPane.showMessageDialog(this, previewImage.getMessage());
        }//if, else if
        this.repaint();
    }//actionPerformed( ActionEvent )

    /**
     * Opens a save Dialogue for the user to save the file
     */
    private void SaveLocal(){
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle(SAVE_ENCODED);
        chooser.setApproveButtonText(SAVE);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);


        FileNameExtensionFilter filter =
                new FileNameExtensionFilter("PNG Files", "png");
        chooser.setFileFilter(filter);

        // Pop up the file chooser window and wait for
        // user to select a file.
        int status = chooser.showOpenDialog(this);
        if (status == JFileChooser.APPROVE_OPTION) {

            
            String file_name =  chooser.getSelectedFile().getPath();
            if (!file_name.endsWith(".png"))
                file_name += ".png";
            
            File toWrite = new File(file_name);
            try {

                ImageIO.write(previewImage.getImage(), "PNG", toWrite);

            } // try
            catch (IOException e) {
                System.out.println(e.getMessage());
            } // catch( IOException )
        } // if
    }// SaveLocal()

    /*
     * Unused
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }
    /*
     * Unused
     */

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        lblCharactersLeft.setText(getCharsLeft());
    }
}// PosterGUI

