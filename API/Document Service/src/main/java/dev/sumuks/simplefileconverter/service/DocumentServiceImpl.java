package dev.sumuks.simplefileconverter.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import dev.sumuks.simplefileconverter.client.TextServiceClient;
import feign.Response;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class DocumentServiceImpl implements DocumentService{

    @Autowired
    TextServiceClient textServiceClient;

    @Override
    public void convertHtmlToPdf(String url, String htmlContent, OutputStream outputStream) {

        ConverterProperties props = new ConverterProperties().setBaseUri(url);
        HtmlConverter.convertToPdf(htmlContent, outputStream, props);
    }

    @Override
    public String convertUrlToHtml(String url) throws IOException {

        Response textServiceResponse = textServiceClient.convertUrlToHtml(url);
        InputStream inputStream = textServiceResponse.body().asInputStream();

        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    @Override
    public void convertPdfToDocx(InputStream inputStream, OutputStream outputStream) {
        PdfDocument doc = new PdfDocument();
        doc.loadFromStream(inputStream);
        doc.saveToStream(outputStream, FileFormat.DOCX);
        doc.close();
    }

    @Override
    public void convertDocxToPdf(InputStream inputStream, OutputStream outputStream) throws Docx4JException {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);
        FOSettings foSettings = Docx4J.createFOSettings();
        foSettings.setOpcPackage(wordMLPackage);
        Docx4J.toFO(foSettings, outputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);

    }
}
