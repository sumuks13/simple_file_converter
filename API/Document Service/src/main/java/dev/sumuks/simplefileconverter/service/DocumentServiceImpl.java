package dev.sumuks.simplefileconverter.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import dev.sumuks.simplefileconverter.client.TextServiceClient;
import feign.Response;
import jakarta.servlet.ServletOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class DocumentServiceImpl implements DocumentService{

    @Autowired
    TextServiceClient textServiceClient;

    @Override
    public void convertHtmlToPdf(String url, String htmlContent, ServletOutputStream outputStream) {

        ConverterProperties props = new ConverterProperties().setBaseUri(url);
        HtmlConverter.convertToPdf(htmlContent, outputStream, props);
    }

    @Override
    public String convertUrlToHtml(String url) throws IOException {

        Response textServiceResponse = textServiceClient.convertUrlToHtml(url);
        InputStream inputStream = textServiceResponse.body().asInputStream();

        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
