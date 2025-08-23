package dev.sumuks.simplefileconverter.controller;

import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import dev.sumuks.simplefileconverter.service.DocumentService;
import jakarta.servlet.http.HttpServletResponse;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
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

        PdfDocument doc = new PdfDocument();
        InputStream inputStream = file.getInputStream();
        doc.loadFromStream(inputStream);

        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"converted.docx\"");
        doc.saveToStream(response.getOutputStream(), FileFormat.DOCX);

        doc.close();
        response.getOutputStream().close();
        inputStream.close();
    }

    @PostMapping("/docx-to-pdf")
    public void convertDocxToPdf(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException, Docx4JException {

        InputStream inputStream = file.getInputStream();

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);
        FOSettings foSettings = Docx4J.createFOSettings();
        foSettings.setOpcPackage(wordMLPackage);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.pdf");
        Docx4J.toFO(foSettings, response.getOutputStream(), Docx4J.FLAG_EXPORT_PREFER_XSL);
    }

}
