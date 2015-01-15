package steganography;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hides a small section of text inside of an image without visibly
 * altering the image.  This class contains methods to hide the text as well
 * as to read the text.
 * <br>
 * <img src="Steganography.png" alt="UML class diagram"/>
 * @author  Jordan Welch
 * @version Feb 14, 2011
 */
public class Steganography {

    /**
     * The Maximum message length. This is the highest number that can be stored
     * inside the top leftmost pixel of an image designating the message length.
     */
    public static final int MESSAGE_MAX = 1000;
    /**
     * Logger for testing purposes
     */
    private static final Logger logger = Logger.getLogger("Steganography");

    /**
     * Set Logger level remotely
     * @param newLevel Level to set to.
     */
    public static void setLoggerLevel(Level newLevel) {
        logger.setLevel(newLevel);
    }
    /**
     * The Value of uppercase 'A' in Unicode (Minus 1).  By adding or subtracting this
     * number we can still use Unicode methods while keeping the final value
     * between 1 and 26
     *
     * Space will be defined as 0
     */
    private static final int UNICODE_CHAR_OFFSET = 9;
    /**
     * A variable to represent space that is easier to read than ' '
     */
    private static final char SPACE = ' ';

    /**
     * Embeds a number from 0 to 999 inside of a pixel
     * Rounds down the RGB Value to their closest 0 and adds:
     *  The Ones place to the R Value
     *  The Tens place to the G Value
     *  The Hundreds place to the B Value
     * @param num The number to be hidden
     * @param color The color for the number to be hidden in
     * @return The color with the embedded number
     * @see #retrieveNumber(int[])  For extraction
     */
    private static int[] embedNumber(int num, int[] color) {
        color[0] = roundDown(color[0]) + num % 10;
        logger.log(Level.INFO, "NumIn: {0}", (num % 10));
        logger.log(Level.INFO, "R Val = {0}", color[0]);
        color[1] = roundDown(color[1]) + ((num % 100) - num % 10) / 10;
        logger.log(Level.INFO, "NumIn: {0}", ((num % 100) - num % 10));
        logger.log(Level.INFO, "G Val = {0}", color[1]);
        color[2] = roundDown(color[2]) + (num - (num % 100)) / 100;
        logger.log(Level.INFO, "NumIn: {0}", (num - (num % 100)));
        logger.log(Level.INFO, "B Val = {0}", color[2]);

        return color;
    }

    /**
     * Retrieves a number that was earlier hidden in a color
     * Number from 0-999
     * @param color The color with the hidden number
     * @return The Number that is extracted
     * @see #embedNumber(int, int[]) For hiding
     */
    private static int retrieveNumber(int[] color) {
        int num = color[0] % 10;
        logger.log(Level.INFO, "NumOut: {0}", (color[0] % 10));
        logger.log(Level.INFO, "R Val = {0}", color[0]);
        num += (color[1] % 10) * 10;
        logger.log(Level.INFO, "NumOut: {0}", ((color[1] % 10) * 10));
        logger.log(Level.INFO, "G Val = {0}", color[1]);
        num += (color[2] % 10) * 100;
        logger.log(Level.INFO, "NumOut: {0}", ((color[2] % 10) * 100));
        logger.log(Level.INFO, "B Val = {0}", color[2]);

        return num;
    }// retrieveNumber(int[])

    /**
     * Retrieves the message hidden in the image.
     * @param toDecode The Image to retrieve the message from
     * @return The hidden message or null if no message found
     * @see #encodeImage(java.lang.String, java.awt.image.BufferedImage) 
     */
    public static String decodeImage(BufferedImage toDecode) {
        ArrayList<Character> toReturn = new ArrayList<Character>();

        Raster raster = toDecode.getRaster();
        int[] color = new int[3];

        raster.getPixel(0, 0, color);
        int messageLength = retrieveNumber(color) + 1;

        int imageWidth = raster.getWidth();
        int imageHeight = raster.getHeight();

        int totalPixels = imageWidth * imageHeight;

        int toGrab = (totalPixels - 1) / messageLength;

        int counter = toGrab - 1;

        logger.log(Level.INFO, "Number coming out: {0}", messageLength);

        for (int j = 0; j < raster.getHeight(); j++) {
            for (int i = 0; i < raster.getWidth(); i++) {
                if ((counter % toGrab) == 0 && toReturn.size() < messageLength) {
                    raster.getPixel(i, j, color);
                    toReturn.add(retrieveChar(color));
                }//if
                counter++;
            }//for
        }//for
        return arrayToString(toReturn);
    }// decodeImage ( BufferedImage )

    /**
     * Encodes a message inside of an image.
     * Process:  Convert Message to array of characters
     * - All characters removed except letters
     * - All letters converted to uppercase;
     * - Array of characters converted to array of integers (0 - 25)
     * - Separated into 3 values 0 - 9, 0 - 9, 0 - 4
     * - Storage Pixels rounded down to 0;
     * - first value added to R and so on.
     * @param message The message to be encoded
     * @param original The Original image
     * @return The image with the message encoded.
     * @see #decodeImage(java.awt.image.BufferedImage)
     */
    public static void encodeImage(String message, BufferedImage original) {
        ArrayList<Character> messageArray = getUpperCaseArray(message);

        //Raster to change pixels in picture
        WritableRaster raster = original.getRaster();

        //Length of message being written
        int messageLength = messageArray.size();

        //Image dimmensions
        int imageWidth = raster.getWidth();
        int imageHeight = raster.getHeight();

        int totalPixels = imageWidth * imageHeight;

        int toPlace = (totalPixels - 1) / messageLength;

        int[] color = new int[3];


        logger.log(Level.INFO, "Number going in: {0}", messageLength);

        //First Hide Message size
        //The -1 Allows message to be 1000 characters long.  Cannot be 0
        raster.getPixel(0, 0, color);
        color = embedNumber(messageLength - 1, color);
        raster.setPixel(0, 0, color);

        int counter = toPlace - 1;

        for (int j = 0; j < raster.getHeight(); j++) {
            for (int i = 0; i < raster.getWidth(); i++) {
                raster.getPixel(i, j, color);

                if ((counter % toPlace) == 0 && messageArray.size() > 0) {
                    hideChar(color, messageArray.get(0));
                    messageArray.remove(0);
                }

                raster.setPixel(i, j, color);
                counter++;
            }//for
        }//for
    }// encodeImage ( String , BufferedImage )

    /**
     * Converts an arrayList of Characters to a more meaningful String
     * @param array - An Array of Characters
     * @return A string of all the characters in the array
     */
    private static String arrayToString(ArrayList<Character> array) {
        String toReturn = "";

        for (Character c : array) {
            toReturn += c;
        }

        return toReturn;
    }// arrayToString(ArrayList<Character>)

    /**
     * Rounds down the number so that the last digit is a zero
     * @param num The number to round down
     * @return The number modified so the last digit is a zero
     */
    private static int roundDown(int num) {
        if (num > 250) {
            num -= 10;
        }// if
        return num - (num % 10);
    } // roundDown ( int )

    private static ArrayList<Character> getUpperCaseArray(String input) {
        char[] temp = input.toCharArray();
        ArrayList<Character> toReturn = new ArrayList<Character>();
        for (char c : temp) {
            if (Character.isLetter(c) || c == SPACE) {
                c = Character.toLowerCase(c);
                toReturn.add(c);
            }// if
        }// for

        return toReturn;
    }// getUpperCaseArray ( String )

    /**
     * Hides a character in a pixel by placing portions of its value in
     * each of the RGB values
     *
     * Characters hidden by rounding down the value to 0 and adding the portion
     * of the value.
     * @param color - Color to hide the character in
     * @param c - Character to hide
     * @return - The color modified with the hidden character
     */
    private static int[] hideChar(int[] color, Character c) {
        int charVal;

        if (Character.getNumericValue(c) == -1) {
            charVal = 0;
        } else {
            charVal = Character.getNumericValue(c) - UNICODE_CHAR_OFFSET;
        }

        int[] temp = separateCharVal(charVal);

        color[0] = roundDown(color[0]) + temp[0];
        color[1] = roundDown(color[1]) + temp[1];
        color[2] = roundDown(color[2]) + temp[2];

        return color;
    }// hideChar (int[] , Character )

    /**
     * Retrieves the character hidden in a pixel
     * @param color The color with hidden character
     * @return The hidden character
     */
    private static char retrieveChar(int[] color) {
        int charVal = 0;
        charVal += color[0] % 10;
        charVal += color[1] % 10;
        charVal += color[2] % 10;

        return getChar(charVal);
    }// retrieveChar

    /**
     * Creates an array of three integer values that allow a character
     * to be hidden in a pixel's RBG Value
     * @param charVal The Value to be split
     * @return An array of three integers that is charvalue separated
     * @see #hideChar
     */
    private static int[] separateCharVal(int charVal) {
        int[] toReturn = {0, 0, 0};

        if (charVal > 9) {
            toReturn[0] = 9;
            charVal -= 9;
        }// if
        if (charVal > 9) {
            toReturn[1] = 9;
            charVal -= 9;
        }// if

        toReturn[2] = charVal;
        return toReturn;
    }// separateCharVal( int )

    /**
     * Gets the character based on an integer value (0-25) (A-Z)
     * //TODO There has to be a better way of doing this
     * @param value The value representing an uppercase number
     * @return The Character
     */
    private static char getChar(int value) {
        switch (value) {
            case 0:
                return SPACE;
            case 1:
                return 'A';
            case 2:
                return 'B';
            case 3:
                return 'C';
            case 4:
                return 'D';
            case 5:
                return 'E';
            case 6:
                return 'F';
            case 7:
                return 'G';
            case 8:
                return 'H';
            case 9:
                return 'I';
            case 10:
                return 'J';
            case 11:
                return 'K';
            case 12:
                return 'L';
            case 13:
                return 'M';
            case 14:
                return 'N';
            case 15:
                return 'O';
            case 16:
                return 'P';
            case 17:
                return 'Q';
            case 18:
                return 'R';
            case 19:
                return 'S';
            case 20:
                return 'T';
            case 21:
                return 'U';
            case 22:
                return 'V';
            case 23:
                return 'W';
            case 24:
                return 'X';
            case 25:
                return 'Y';
            case 26:
                return 'Z';
            default:
                return ' ';
        }// switch
    }// getChar ( int )
}// Steganography

