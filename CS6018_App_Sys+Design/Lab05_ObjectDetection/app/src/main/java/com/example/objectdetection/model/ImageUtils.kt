package com.example.objectdetection.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale

object ImageUtils { // Utility functions for image processing

    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? { // Convert camera frame to bitmap
        return try {
            when (imageProxy.format) { // Check image format
                ImageFormat.YUV_420_888 -> { // Most common camera format
                    yuv420ToBitmap(imageProxy) // Convert YUV to RGB bitmap
                }
                ImageFormat.JPEG -> { // If already JPEG format
                    val buffer = imageProxy.planes[0].buffer // Get image data
                    val bytes = ByteArray(buffer.remaining()) // Create byte array
                    buffer.get(bytes) // Copy data to array
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size) // Decode JPEG to bitmap
                }
                else -> { // For other formats
                    createGrayscaleBitmap(imageProxy) // Create simple grayscale bitmap
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log error
            null // Return null on error
        }
    }

    private fun yuv420ToBitmap(imageProxy: ImageProxy): Bitmap? { // Convert YUV420 to RGB bitmap
        val yBuffer = imageProxy.planes[0].buffer // Y (brightness) data
        val uBuffer = imageProxy.planes[1].buffer // U (color) data
        val vBuffer = imageProxy.planes[2].buffer // V (color) data

        val ySize = yBuffer.remaining() // Y data size
        val uSize = uBuffer.remaining() // U data size
        val vSize = vBuffer.remaining() // V data size

        val nv21 = ByteArray(ySize + uSize + vSize) // Combined array for all data

        yBuffer.get(nv21, 0, ySize) // Copy Y data
        vBuffer.get(nv21, ySize, vSize) // Copy V data
        uBuffer.get(nv21, ySize + vSize, uSize) // Copy U data

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null) // Create YUV image
        val out = ByteArrayOutputStream() // Stream to write JPEG data
        yuvImage.compressToJpeg( // Convert YUV to JPEG
            Rect(0, 0, imageProxy.width, imageProxy.height), // Full image area
            100, // Maximum quality
            out // Output stream
        )

        val imageBytes = out.toByteArray() // Get JPEG bytes
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) // Decode to bitmap
    }

    private fun createGrayscaleBitmap(imageProxy: ImageProxy): Bitmap { // Create simple grayscale bitmap
        val buffer = imageProxy.planes[0].buffer // Get Y plane (brightness data)
        val bytes = ByteArray(buffer.remaining()) // Create byte array
        buffer.get(bytes) // Copy brightness data

        val bitmap = createBitmap(imageProxy.width, imageProxy.height)

        bitmap.eraseColor(android.graphics.Color.GRAY) // Fill with gray color as placeholder
        return bitmap // Return placeholder bitmap
    }

    fun resizeBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap { // Resize bitmap to target size
        return bitmap.scale(targetWidth, targetHeight) // Create resized bitmap
    }

    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap { // Rotate bitmap by degrees
        val matrix = android.graphics.Matrix() // Create transformation matrix
        matrix.postRotate(degrees) // Set rotation
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true) // Apply rotation
    }
}