package dev.sumuks.simplefileconverter.service;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DocumentService {
    void convertHtmlToPdf(String url, String htmlContent, OutputStream outputStream);

    String convertUrlToHtml(String url) throws IOException;

    void convertPdfToDocx(InputStream inputStream, OutputStream outputStream);

    void convertDocxToPdf(InputStream inputStream, OutputStream outputStream) throws Docx4JException;
}
