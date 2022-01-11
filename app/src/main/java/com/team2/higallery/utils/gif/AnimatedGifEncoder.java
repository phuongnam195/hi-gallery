package com.team2.higallery.utils.gif;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.team2.higallery.utils.BitmapUtils;

/**
 * Class AnimatedGifEncoder - Encodes a GIF file consisting of one or more
 * frames.
 *
 * <pre>
 *  Example:
 *     AnimatedGifEncoder e = new AnimatedGifEncoder();
 *     e.start(outputFileName);
 *     e.setDelay(1000);   // 1 frame per sec
 *     e.addFrame(image1);
 *     e.addFrame(image2);
 *     e.finish();
 * </pre>
 *
 * No copyright asserted on the source code of this class. May be used for any
 * purpose, however, refer to the Unisys LZW patent for restrictions on use of
 * the associated LZWEncoder class. Please forward any corrections to
 * kweiner@fmsware.com.
 *
 * @author Kevin Weiner, FM Software
 * @version 1.03 November 2003
 *
 */

public class AnimatedGifEncoder {
    private final static int MAX_WIDTH = 512;       // kích thước tối đa của ảnh GIF
    private final static int MAX_HEIGHT = 512;
    private final static int DELAY = 1500;          // thời lượng của một tấm (ms)

    int fixedWidth;     // kích thước cố định của ảnh GIF
    int fixedHeight;
    byte[] indexedPixels;   // converted frame indexed to palette
    byte[] colorTab;        // RGB palette
    boolean[] usedEntry = new boolean[256]; // active palette entries

    private ArrayList<Bitmap> bitmaps;      // các hình ảnh input
    ByteArrayOutputStream out;              // luồng byte tạo file GIF
    boolean isEncoded;                      // true, nếu đã tạo GIF thành công (chưa xuất)

    public void set(ArrayList<Bitmap> inputs) {
        this.bitmaps = inputs;
        isEncoded = false;
    }

    public void encode() {
        out = new ByteArrayOutputStream();
        try {
            handleSizeBitmaps();    // xử lý kích thước cho các hình ảnh: resize, nền đen

            writeString("GIF89a"); // header

            for (int i = 0; i < bitmaps.size(); i++) {
                Bitmap image = bitmaps.get(i);
                // phân giải hình ảnh ra mảng pixels
                byte[] pixels = getImagePixels(image);

                // xây dựng bảng màu và bản đồ pixels
                analyzePixels(pixels);

                if (i == 0) {
                    writeLSD();     // logical screen descriptior
                    writePalette(); // global color table
                    writeNetscapeExt();
                }

                writeGraphicCtrlExt(); // write graphic control extension
                writeImageDesc(i == 0); // image descriptor
                if (i > 0) {
                    writePalette(); // local color table
                }
                writePixels(); // encode and write pixel data
            }

            out.write(0x3b); // gif trailer
            out.flush();
            out.close();

            isEncoded = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("AnimatedGifEncoder-encode", e.getMessage());
        }

        indexedPixels = null;
        colorTab = null;
    }

    public boolean export(String fileName) {
        if (isEncoded) {
            FileOutputStream outStream;
            try{
                outStream = new FileOutputStream(fileName);
                outStream.write(out.toByteArray());
                outStream.close();
            } catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }
        out = null;
        return true;
    }

    private void calcFixedSize() {
        fixedWidth = 0;
        fixedHeight = 0;
        for (int i = 0; i < bitmaps.size(); i++) {
            bitmaps.set(i, BitmapUtils.resize(bitmaps.get(i), MAX_WIDTH, MAX_HEIGHT));
            int w = bitmaps.get(i).getWidth();
            int h = bitmaps.get(i).getHeight();

            if (w > fixedWidth) {
                fixedWidth = w;
            }
            if (h > fixedHeight) {
                fixedHeight = h;
            }
        }
    }

    private void handleSizeBitmaps() {
        calcFixedSize();
        for (int i = 0; i < bitmaps.size(); i++) {
            int w = bitmaps.get(i).getWidth();
            int h = bitmaps.get(i).getHeight();

            if ((w != fixedWidth) || (h != fixedHeight)) {
                Bitmap tmp = Bitmap.createBitmap(fixedWidth, fixedHeight, Config.RGB_565);
                int left = (fixedWidth - w) / 2;
                int top = (fixedHeight - h) / 2;

                Canvas g = new Canvas(tmp);
                g.drawBitmap(bitmaps.get(i), left, top, new Paint());
                bitmaps.set(i, tmp);
            }
        }
    }

    /**
     * Analyzes image colors and creates color map.
     */
    protected void analyzePixels(byte[] pixels) {
        int len = pixels.length;
        int nPix = len / 3;
        indexedPixels = new byte[nPix];
        NeuQuant nq = new NeuQuant(pixels, len, 10);
        // initialize quantizer
        colorTab = nq.process(); // create reduced palette
        // convert map from BGR to RGB
        for (int i = 0; i < colorTab.length; i += 3) {
            byte temp = colorTab[i];
            colorTab[i] = colorTab[i + 2];
            colorTab[i + 2] = temp;
            usedEntry[i / 3] = false;
        }
        // map image pixels to new palette
        int k = 0;
        for (int i = 0; i < nPix; i++) {
            int index = nq.map(pixels[k++] & 0xff, pixels[k++] & 0xff, pixels[k++] & 0xff);
            usedEntry[index] = true;
            indexedPixels[i] = (byte) index;
        }
    }

    /**
     * Extracts image pixels into byte array "pixels"
     */
    protected byte[] getImagePixels(Bitmap image) {
        int[] data = getImageData(image);
        byte[] pixels = new byte[data.length * 3];
        for (int i = 0; i < data.length; i++) {
            int td = data[i];
            int tind = i * 3;
            pixels[tind++] = (byte) ((td >> 0) & 0xFF);
            pixels[tind++] = (byte) ((td >> 8) & 0xFF);
            pixels[tind] = (byte) ((td >> 16) & 0xFF);
        }
        return pixels;
    }

    protected int[] getImageData(Bitmap img) {
        int w = img.getWidth();
        int h = img.getHeight();

        int[] data = new int[w * h];
        img.getPixels(data, 0, w, 0, 0, w, h);
        return data;
    }

    /**
     * Writes Graphic Control Extension
     */
    protected void writeGraphicCtrlExt() throws IOException {
        out.write(0x21); // extension introducer
        out.write(0xf9); // GCE label
        out.write(4); // data block size
        int transp, disp;
        transp = 0;
        disp = 0; // dispose = no action

        // packed fields
        out.write(0 | // 1:3 reserved
                disp | // 4:6 disposal
                0 | // 7 user input - 0 = none
                transp); // 8 transparency flag

        writeShort(DELAY / 10);
        out.write(0); // transparent color index
        out.write(0); // block terminator
    }

    /**
     * Writes Image Descriptor
     */
    protected void writeImageDesc(boolean firstFrame) throws IOException {
        out.write(0x2c); // image separator
        writeShort(0); // image position x,y = 0,0
        writeShort(0);
        writeShort(fixedWidth); // image size
        writeShort(fixedHeight);
        // packed fields
        if (firstFrame) {
            // no LCT - GCT is used for first (or only) frame
            out.write(0);
        } else {
            // specify normal LCT
            out.write(0x80 | 0x07);
        }
    }

    /**
     * Writes Logical Screen Descriptor
     */
    protected void writeLSD() throws IOException {
        writeShort(fixedWidth);
        writeShort(fixedHeight);
        out.write((0x80 | 0x70 | 0x07));
        out.write(0);
        out.write(0);
    }

    /**
     * Writes Netscape application extension to define repeat count.
     */
    protected void writeNetscapeExt() throws IOException {
        out.write(0x21); // extension introducer
        out.write(0xff); // app extension label
        out.write(11); // block size
        writeString("NETSCAPE" + "2.0"); // app id + auth code
        out.write(3); // sub-block size
        out.write(1); // loop sub-block id
        writeShort(0); // loop count (extra iterations, 0=repeat forever)
        out.write(0); // block terminator
    }

    /**
     * Writes color table
     */
    protected void writePalette() throws IOException {
        out.write(colorTab, 0, colorTab.length);
        int n = (3 * 256) - colorTab.length;
        for (int i = 0; i < n; i++) {
            out.write(0);
        }
    }

    /**
     * Encodes and writes pixel data
     */
    protected void writePixels() throws IOException {
        LZWEncoder encoder = new LZWEncoder(fixedWidth, fixedHeight, indexedPixels, 8);
        encoder.encode(out);
    }

    /**
     * Write 16-bit value to output stream, LSB first
     */
    protected void writeShort(int value) throws IOException {
        out.write(value & 0xff);
        out.write((value >> 8) & 0xff);
    }

    /**
     * Writes string to output stream
     */
    protected void writeString(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            out.write((byte) s.charAt(i));
        }
    }
}