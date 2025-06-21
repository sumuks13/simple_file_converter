package dev.sumuks.simplefileconverter.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

@RestController
public class ImageServiceController {

    @PostMapping(value = "/images/convert")
    public void convertImages(@RequestParam("targetFormat") String targetFormat,
                              @RequestParam("file") MultipartFile file,
                              HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        String filename = "converted." + targetFormat;

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        ImageIO.write(bufferedImage, targetFormat, response.getOutputStream());
    }

    @PostMapping(value = "/url-to-qrcode")
    public void convertUrlToBufferedImage(@RequestParam("url") String url,
                                          HttpServletResponse response) throws WriterException, IOException {

        BufferedImage bufferedImage = getBufferedImage(url);

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.png");
        ImageIO.write(bufferedImage, "PNG", response.getOutputStream());
    }

    private BufferedImage getBufferedImage(String url) throws WriterException {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200, hints);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
