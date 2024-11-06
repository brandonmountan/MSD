package assign01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GrayscaleImageTest {

    private GrayscaleImage smallSquare;
    private GrayscaleImage smallWide;

    @BeforeEach
    void setUp() {
        smallSquare = new GrayscaleImage(new double[][]{{1,2},{3,4}});
        smallWide = new GrayscaleImage(new double[][]{{1,2,3},{4,5,6}});
    }

    @Test
    void testGetPixel(){
        assertEquals(smallSquare.getPixel(0,0), 1);
        assertEquals(smallSquare.getPixel(1,0), 2);
        assertEquals(smallSquare.getPixel(0,1), 3);
        assertEquals(smallSquare.getPixel(1,1), 4);

    }

    @Test
    void testEquals() {
        assertEquals(smallSquare, smallSquare);
        var equivalent = new GrayscaleImage(new double[][]{{1,2},{3,4}});
        assertEquals(smallSquare, equivalent);
    }

    @Test
    void averageBrightness() {
        assertEquals(smallSquare.averageBrightness(), 2.5, 2.5*.001);
        var bigZero = new GrayscaleImage(new double[1000][1000]);
        assertEquals(bigZero.averageBrightness(), 0);
    }

    @Test
    void normalized() {
        var smallNorm = smallSquare.normalized();
        assertEquals(smallNorm.averageBrightness(), 127, 127*.001);
        var scale = 127/2.5;
        var expectedNorm = new GrayscaleImage(new double[][]{{scale, 2*scale},{3*scale, 4*scale}});
        for(var row = 0; row < 2; row++){
            for(var col = 0; col < 2; col++){
                assertEquals(smallNorm.getPixel(col, row), expectedNorm.getPixel(col, row),
                        expectedNorm.getPixel(col, row)*.001,
                        "pixel at row: " + row + " col: " + col + " incorrect");
            }
        }
    }

    @Test
    void mirrored() {
        var expected = new GrayscaleImage(new double[][]{{2,1},{4,3}});
        assertEquals(smallSquare.mirrored(), expected);
    }

    @Test
    void cropped() {
        var cropped = smallSquare.cropped(1,1,1,1);
        assertEquals(cropped, new GrayscaleImage(new double[][]{{4}}));
    }

    @Test
    void squarified() {
        var squared = smallWide.squarified();
        var expected = new GrayscaleImage(new double[][]{{1,2},{4,5}});
        assertEquals(squared, expected);
    }

    @Test
    void testGetPixelThrowsOnNegativeX(){
        assertThrows(IllegalArgumentException.class, () -> { smallSquare.getPixel(-1,0);});
    }

    @Test
    void testEqualsWithDifferentDimensions() {
        var differentSize = new GrayscaleImage(new double[][]{{1, 2, 3}, {4, 5, 6}});
        assertNotEquals(smallSquare, differentSize);
    }

    @Test
    void testEqualsWithDifferentPixelValues() {
        var differentValues = new GrayscaleImage(new double[][]{{1, 2}, {3, 5}});
        assertNotEquals(smallSquare, differentValues);
    }

    @Test
    void testAverageBrightnessWithNonUniformValues() {
        var variedImage = new GrayscaleImage(new double[][]{{10, 20}, {30, 40}});
        assertEquals(25, variedImage.averageBrightness(), 0.001);
    }

    @Test
    void testMirroredWithOddWidth() {
        var oddWidthImage = new GrayscaleImage(new double[][]{{1, 2, 3}, {4, 5, 6}});
        var expectedMirrored = new GrayscaleImage(new double[][]{{3, 2, 1}, {6, 5, 4}});
        assertEquals(expectedMirrored, oddWidthImage.mirrored());
    }

    @Test
    void testCroppedAtBoundary() {
        var croppedEdge = smallSquare.cropped(0, 0, 2, 2);
        assertEquals(smallSquare, croppedEdge);
    }

    @Test
    void testSquarifiedOnSquareImage() {
        var squared = smallSquare.squarified();
        assertEquals(smallSquare, squared);
    }

    @Test
    void testGetPixelOutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> smallSquare.getPixel(2, 2));
        assertThrows(IllegalArgumentException.class, () -> smallSquare.getPixel(0, -1));
        assertThrows(IllegalArgumentException.class, () -> smallSquare.getPixel(-1, 0));
    }
}
