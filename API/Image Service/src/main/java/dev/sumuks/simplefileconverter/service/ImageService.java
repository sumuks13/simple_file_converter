package dev.sumuks.simplefileconverter.service;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ImageService {

    void convertImages(InputStream inputStream, OutputStream outputStream, String targetFormat) throws IOException;

    void convertUrlToQrCode(String url, OutputStream outputStream) throws WriterException, IOException;

}
