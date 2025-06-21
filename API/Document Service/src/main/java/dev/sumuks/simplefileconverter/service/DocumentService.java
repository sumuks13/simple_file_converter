package dev.sumuks.simplefileconverter.service;

import jakarta.servlet.ServletOutputStream;

import java.io.IOException;

public interface DocumentService {
    void convertHtmlToPdf(String url, String htmlContent, ServletOutputStream outputStream);

    String convertUrlToHtml(String url) throws IOException;
}
