package dev.sumuks.simplefileconverter.controller;

import dev.sumuks.simplefileconverter.service.DocumentService;
import jakarta.servlet.http.HttpServletResponse;
import org.docx4j.openpackaging.exceptions.Docx4JException;
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
public class DocumentController {

    @Autowired
    DocumentService documentService;


    @PostMapping("/url-to-pdf")
    public void convertUrlToPdf(@RequestParam("url") String url,
                                 HttpServletResponse response) throws IOException {
        
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf");

        String htmlContent = documentService.convertUrlToHtml(url);
        documentService.convertHtmlToPdf(url, htmlContent, response.getOutputStream());
    }

    @PostMapping("/pdf-to-docx")
    public void convertPdfToDocx(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException {

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"converted.docx\"");

        InputStream inputStream = file.getInputStream();
        documentService.convertPdfToDocx(inputStream, response.getOutputStream());

    }

    @PostMapping("/docx-to-pdf")
    public void convertDocxToPdf(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException, Docx4JException {

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf");

        InputStream inputStream = file.getInputStream();
        documentService.convertDocxToPdf(inputStream, response.getOutputStream());
    }

}
