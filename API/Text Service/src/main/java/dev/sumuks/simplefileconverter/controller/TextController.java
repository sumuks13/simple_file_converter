package dev.sumuks.simplefileconverter.controller;

import dev.sumuks.simplefileconverter.service.TextServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@RestController
public class TextController {

    @Autowired
    TextServiceImpl textService;

    @PostMapping(value = "/json-to-xml")
    public void convertJsonToXml(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String jsonContent = new String(inputStream.readAllBytes());

        List<Map<String, Object>> map = textService.jsonToListOfMap(jsonContent);
        String xmlContent = textService.listOfMapToXml(map);

        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.xml");
        response.getWriter().write(xmlContent);

    }

    @PostMapping(value = "/xml-to-json")
    public void convertXmlToJson(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String xmlContent = new String(inputStream.readAllBytes());

        List<Map<String, Object>> map = textService.xmlToListOfMap(xmlContent);
        String jsonContent = textService.listOfMapToJson(map);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.json");
        response.getWriter().write(jsonContent);

    }

    @PostMapping(value = "/json-to-csv")
    public void convertJsonToCsv(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String jsonContent = new String(inputStream.readAllBytes());

        List<Map<String, Object>> map = textService.jsonToListOfMap(jsonContent);
        String csvContent = textService.listOfMapToCsv(map);

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.csv");
        response.getWriter().write(csvContent);

    }

    @PostMapping(value = "/xml-to-csv")
    public void convertXmlToCsv(@RequestParam("file") MultipartFile file,
                                HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String xmlContent = new String(inputStream.readAllBytes());

        List<Map<String, Object>> map = textService.xmlToListOfMap(xmlContent);
        String csvContent = textService.listOfMapToCsv(map);

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.csv");
        response.getWriter().write(csvContent);

    }

    @PostMapping(value = "/csv-to-json")
    public void convertCsvToJson(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String csvContent = new String(inputStream.readAllBytes());

        List<Map<String, Object>> map = textService.csvToListOfMap(csvContent);
        String jsonContent = textService.listOfMapToJson(map);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.json");
        response.getWriter().write(jsonContent);
    }

    @PostMapping(value = "/csv-to-xml")
    public void convertCsvToXml(@RequestParam("file") MultipartFile file,
                                HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String csvContent = new String(inputStream.readAllBytes());

        List<Map<String, Object>> map = textService.csvToListOfMap(csvContent);
        String xmlContent = textService.listOfMapToXml(map);

        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.xml");
        response.getWriter().write(xmlContent);
    }

    @PostMapping("/url-to-html")
    public void convertUrltoPdf(@RequestParam("url") String url,
                                HttpServletResponse response) throws IOException {

        Document doc = Jsoup.connect(url).get();
        doc.outputSettings().syntax(org.jsoup.nodes.Document.OutputSettings.Syntax.xml)
                .escapeMode(Entities.EscapeMode.xhtml)
                .charset("UTF-8");
        String cleanedHtml = doc.html();

        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.html");
        response.getWriter().write(cleanedHtml);
    }
}
