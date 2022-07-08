package com.inv.data.unique;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author XDSSWAR
 */
@SuppressWarnings("all")
class Notify {

    /**
     * It reads a JUnique formatted message from an InputStream.
     *
     * @param inputStream
     *            The source stream.
     * @return The message decoded from the stream.
     * @throws IOException
     *             In an I/O error occurs.
     */
    public static String read(InputStream inputStream) throws IOException {
        // Message length (4 bytes)
        byte[] b = new byte[4];
        if (inputStream.read(b) != 4) {
            throw new IOException("Unexpected end of stream");
        }
        int length = byteArrayToInt(b);
        // Length validation.
        if (length < 0) {
            throw new IOException("Invalid length block");
        } else if (length == 0) {
            return "";
        } else {
            // The message in bytes.
            byte[] message = new byte[length];
            if (inputStream.read(message) != length) {
                throw new IOException("Unexpected end of stream");
            }
            // From bytes to string (utf-8).
            return new String(message, "UTF-8");
        }
    }

    /**
     * It writes a JUnique formatted message in an OutputStream.
     *
     * @param message
     *            The message.
     * @param outputStream
     *            The OutputStream.
     * @throws IOException
     *             In an I/O error occurs.
     */
    public static void write(String message, OutputStream outputStream)
            throws IOException {
        // Is this message null?
        if (message == null) {
            // Writes a 0 length block.
            outputStream.write(0);
            outputStream.write(0);
            outputStream.write(0);
            outputStream.write(0);
            outputStream.flush();
        } else {
            // Message length.
            int length = message.length();
            // The length block.
            byte[] lenght = intToByteArray(length);
            outputStream.write(lenght);
            outputStream.flush();
            // Message block.
            byte[] b = message.getBytes("UTF-8");
            outputStream.write(b);
            outputStream.flush();
        }
    }

    /**
     * Encode an integer value in a four elements byte array.
     *
     * @param value
     *            The value to be encoded.
     * @return The encoded value, as a four bytes array.
     */
    private static byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (3 - i) * 8;
            b[i] = (byte) ((value >> offset) & 0xFF);
        }
        return b;
    }

    /**
     * Decoded a four bytes lengh array into an integer value.
     *
     * @param b
     *            The source array (four elements, nothing less, nothing more)
     * @return The decoded integer value.
     */
    private static int byteArrayToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }
}
