package dev.sumuks.simplefileconverter.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService{

    @Override
    public void convertImages(InputStream inputStream, OutputStream outputStream, String targetFormat) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(inputStream);
        ImageIO.write(bufferedImage, targetFormat, outputStream);

    }

    @Override
    public void convertUrlToQrCode(String url, OutputStream outputStream) throws WriterException, IOException {

        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200, hints);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ImageIO.write(bufferedImage, "PNG", outputStream);

    }
}
