package assign01;


import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * Represents a grayscale (black and white) image as a 2D array of "pixel" brightnesses
 * 255 is "white" 127 is "gray" 0 is "black" with intermediate values in between
 * Author: Ben Jones and Brandon Mountan
 */
public class GrayscaleImage {
    private double[][] imageData; // the actual image data


    /**
     * Initialize an image from a 2D array of doubles
     * This constructor creates a copy of the input array
     * @param data initial pixel values
     * @throws IllegalArgumentException if the input array is empty or "jagged" meaning not all rows are the same length
     */
    public GrayscaleImage(double[][] data){
        if(data.length == 0 || data[0].length == 0){
            throw new IllegalArgumentException("Image is empty");
        }

        imageData = new double[data.length][data[0].length];
        for(var row = 0; row < imageData.length; row++){
            if(data[row].length != imageData[row].length){
                throw new IllegalArgumentException("All rows must have the same length");
            }
            for(var col = 0; col < imageData[row].length; col++){
                imageData[row][col] = data[row][col];
            }
        }
    }

    /**
     * Fetches an image from the specified URL and converts it to grayscale
     * Uses the AWT Graphics2D class to do the conversion, so it may add
     * an item to your dock/menu bar as if you're loading a GUI program
     * @param url where to download the image
     * @throws IOException if the image can't be downloaded for some reason
     */
    public GrayscaleImage(URL url) throws IOException {
        var inputImage = ImageIO.read(url);
        //convert input image to grayscale
        //based on (https://stackoverflow.com/questions/6881578/how-to-convert-between-color-models)
        var grayImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d= grayImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, null);
        g2d.dispose();
        imageData = new double[grayImage.getHeight()][grayImage.getWidth()];

        //raster is basically a width x height x 1 3-dimensional array
        var grayRaster = grayImage.getRaster();
        for(var row = 0; row < imageData.length; row++){
            for(var col = 0; col < imageData[0].length; col++){
                //getSample parameters are x (our column) and y (our row), so they're "backwards"
                imageData[row][col] = grayRaster.getSampleDouble(col, row, 0);
            }
        }
    }

    public void savePNG(File filename) throws IOException {
        var outputImage = new BufferedImage(imageData[0].length, imageData.length, BufferedImage.TYPE_BYTE_GRAY);
        var raster = outputImage.getRaster();
        for(var row = 0; row < imageData.length; row++){
            for(var col = 0; col < imageData[0].length; col++){
                raster.setSample(col, row, 0, imageData[row][col]);
            }
        }
        ImageIO.write(outputImage, "png", filename);
    }

    ///Methods to be filled in by students below

    /**
     * Get the pixel brightness value at the specified coordinates
     * (0,0) is the top left corner of the image, (width -1, height -1) is the bottom right corner
     * @param x horizontal position, increases left to right
     * @param y vertical position, **increases top to bottom**
     * @return the brightness value at the specified coordinates
     * @throws IllegalArgumentException if x, y are not within the image width/height
     */
    public double getPixel(int x, int y){
        if (x < 0 || x >= imageData[0].length || y < 0 || y >= imageData.length) {
            throw new IllegalArgumentException("Coordinates are out of bounds.");
        }
        return imageData[y][x];
    }

    /**
     * Two images are equal if they have the same size and each corresponding pixel
     * in the two images is exactly equal
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other){
        // first check if other object is instance of GreyscaleImage
        if(!(other instanceof GrayscaleImage)){
            return false;
        }
        // cast other to GreyscaleImage so we can access its imageData
        GrayscaleImage otherImage = (GrayscaleImage)other;
        // checking if the two images have different dimensions
        if (imageData.length != otherImage.imageData.length || imageData[0].length != otherImage.imageData[0].length) {
            return false;
        }
        // iterate over each pixel
        for (int row = 0; row < imageData.length; row++) {
            for (int col = 0; col < imageData[0].length; col++) {
                if (imageData[row][col] != otherImage.imageData[row][col]) {
                    return false;
                }
            }
        }
        return true;

    }


    /**
     * Computes the average of all values in image data
     * @return the average of the imageData array
     */
    public double averageBrightness(){
        double sum = 0.0;
        int pixelCount = 0;
        // iterate over each row
        for (double[] row : imageData) {
            // add each pixel value to sum and increase pixel count
            for (double value : row) {
                sum += value;
                pixelCount++;
            }
        }
        return sum / pixelCount;
    }

    /**
     * Return a new GrayScale image where the average new average brightness is 127
     * To do this, uniformly scale each pixel (ie, multiply each imageData entry by the same value)
     * Due to rounding, the new average brightness will not be 127 exactly, but should be very close
     * The original image should not be modified
     * @return a GrayScale image with pixel data uniformly rescaled so that its averageBrightness() is 127
     */
    public GrayscaleImage normalized(){
        double targetBrightness = 127.0;
        double currentAverage = this.averageBrightness();
        double scaleFactor = targetBrightness / currentAverage;

        double[][] newImageData = new double[imageData.length][imageData[0].length];
        for (int row = 0; row < imageData.length; row++) {
            for (int col = 0; col < imageData[0].length; col++) {
                // apply scaling factor
                newImageData[row][col] = imageData[row][col] * scaleFactor;
                // clamping
                newImageData[row][col] = Math.max(0, Math.min(255, newImageData[row][col]));
            }
        }
        return new GrayscaleImage(newImageData);
    }


    /**
     * Returns a new grayscale image that has been "mirrored" across the y-axis
     * In other words, each row of the image should be reversed
     * The original image should be unchanged
     * @return a new GrayscaleImage that is a mirrored version of the this
     */
    public GrayscaleImage mirrored(){
        double[][] mirroredData = new double[imageData.length][imageData[0].length];
        // set pixels in reverse order
        for (int row = 0; row < imageData.length; row++) {
            for (int col = 0; col < imageData[0].length; col++) {
                mirroredData[row][col] = imageData[row][imageData[0].length - 1 - col];
            }
        }
        return new GrayscaleImage(mirroredData);
    }

    /**
     * Returns a new GrayscaleImage of size width x height, containing the part of `this`
     * from startRow -> startRow + height, startCol -> startCol + width
     * The original image should be unmodified
     * @param startRow
     * @param startCol
     * @param width
     * @param height
     * @return A new GrayscaleImage containing the sub-image in the specified rectangle
     * @throws IllegalArgumentException if the specified rectangle goes outside the bounds of the original image
     */
    public GrayscaleImage cropped(int startRow, int startCol, int width, int height){
        // check bounds
        if (startRow < 0 || startCol < 0 || startRow + height > imageData.length || startCol + width > imageData[0].length) {
            throw new IllegalArgumentException("Cropping area is out of bounds.");
        }
        // initialize croppedData to desired height and width
        double[][] croppedData = new double[height][width];
        // fill croppedData with pixels from imageData starting at (startRow, startCol)
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                croppedData[row][col] = imageData[startRow + row][startCol + col];
            }
        }
        return new GrayscaleImage(croppedData);
    }

    /**
     * Returns a new "centered" square image (new width == new height)
     * For example, if the width is 20 pixels greater than the height,
     * this should return a height x height image, with 10 pixels removed from the left and right
     * edges of the image
     * If the number of pixels to be removed is odd, remove 1 fewer pixel from the left or top part
     * (note this convention should be SIMPLER/EASIER to implement than the alternative)
     * The original image should not be changed
     * @return a new, square, GrayscaleImage
     */
    public GrayscaleImage squarified(){
        // dimensions of the image
        int width = imageData[0].length;
        int height = imageData.length;
        // return if already square
        if (width == height) {
            return new GrayscaleImage(imageData);
        }
        // squareSize is set to smaller dimension
        // startRow and startCol calculate the center crop position
        int squareSize = Math.min(width, height);
        int startRow = (height - squareSize) / 2;
        int startCol = (width - squareSize) / 2;
        // uses cropped method to return a square image centered on the original
        return cropped(startRow, startCol, squareSize, squareSize);
    }
}
