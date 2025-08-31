package dev.sumuks.simplefileconverter.controller;

import com.google.zxing.WriterException;
import dev.sumuks.simplefileconverter.service.ImageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class ImageServiceController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value = "/convert")
    public void convertImages(@RequestParam("targetFormat") String targetFormat,
                              @RequestParam("file") MultipartFile file,
                              HttpServletResponse response) throws IOException {

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "converted." + targetFormat);

        InputStream inputStream = file.getInputStream();
        imageService.convertImages(inputStream, response.getOutputStream(), targetFormat);

    }

    @PostMapping(value = "/url-to-qrcode")
    public void convertUrlToBufferedImage(@RequestParam("url") String url,
                                          HttpServletResponse response) throws WriterException, IOException {


        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.png");

        imageService.convertUrlToQrCode(url, response.getOutputStream());

    }
}
