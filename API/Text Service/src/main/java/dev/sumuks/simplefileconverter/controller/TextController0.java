package dev.sumuks.simplefileconverter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.servlet.http.HttpServletResponse;
//import jakarta.xml.bind.JAXBContext;
//import jakarta.xml.bind.JAXBException;
//import jakarta.xml.bind.Marshaller;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
//import org.docx4j.openpackaging.exceptions.Docx4JException;
//import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
//import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
//import org.docx4j.wml.Styles;
//import org.fit.pdfdom.PDFDomTree;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Entities;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@RestController
public class TextController0 {

    @PostMapping(value = "/json-to-xml")
    public void convertJsonToXml(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String jsonContent = new String(inputStream.readAllBytes());

        ObjectMapper jsonMapper = new ObjectMapper();
        Object jsonObject = jsonMapper.readValue(jsonContent, Object.class);

        XmlMapper xmlMapper = new XmlMapper();
        String xmlContent = xmlMapper.writer().withRootName("root").writeValueAsString(jsonObject);

        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.xml");
        response.getWriter().write(xmlContent);

    }

    @PostMapping(value = "/xml-to-json")
    public void convertXmlToJson(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String xmlContent = new String(inputStream.readAllBytes());

        XmlMapper xmlMapper = new XmlMapper();
        JsonNode jsonTree = xmlMapper.readTree(xmlContent.getBytes());

        ObjectMapper jsonMapper = new ObjectMapper();
        String jsonContent = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonTree);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.json");
        response.getWriter().write(jsonContent);

    }

    @PostMapping(value = "/json-to-csv")
    public void convertJsonToCsv(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String jsonContent = new String(inputStream.readAllBytes());

        ObjectMapper jsonMapper = new ObjectMapper();
        JsonNode jsonTree = jsonMapper.readTree(jsonContent.getBytes());

        if(!jsonTree.isArray())
            jsonTree = jsonTree.get(jsonTree.fieldNames().next());

        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode csvHeaders = jsonTree.elements().next();
        csvHeaders.fieldNames().forEachRemaining(csvSchemaBuilder::addColumn);
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.csv");

        CsvMapper csvMapper = new CsvMapper();
        csvMapper.writerFor(JsonNode.class)
                .with(csvSchema)
                .writeValue(response.getWriter(), jsonTree);
    }

    @PostMapping(value = "/xml-to-csv")
    public void convertXmlToCsv(@RequestParam("file") MultipartFile file,
                                HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String xmlContent = new String(inputStream.readAllBytes());

        XmlMapper xmlMapper = new XmlMapper();
        JsonNode rootNode = xmlMapper.readTree(xmlContent);

        CsvSchema csvSchema = CsvSchema.builder()
                .setUseHeader(true)
                .build();

        CsvMapper csvMapper = new CsvMapper();

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.csv");

        csvMapper.writerFor(JsonNode.class)
                .with(csvSchema)
                .writeValue(response.getWriter(), rootNode);

    }

    @PostMapping(value = "/csv-to-json")
    public void convertCsvToJson(@RequestParam("file") MultipartFile file,
                                 HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String csvContent = new String(inputStream.readAllBytes());

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
        MappingIterator<Map<String, String>> iterator = csvMapper.readerFor(Map.class)
                .with(csvSchema)
                .readValues(new StringReader(csvContent));

        List<Map<String, String>> records = new ArrayList<>();

        while (iterator.hasNext()) {
            records.add(iterator.next());
        }

        ObjectMapper objectMapper = new ObjectMapper();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.json");
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(response.getWriter(), records);
    }

    @PostMapping(value = "/csv-to-xml")
    public void convertCsvToXml(@RequestParam("file") MultipartFile file,
                                HttpServletResponse response) throws IOException {

        InputStream inputStream = file.getInputStream();
        String csvContent = new String(inputStream.readAllBytes());

        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();

        MappingIterator<Map<String, String>> iterator = csvMapper.readerFor(Map.class)
                .with(csvSchema)
                .readValues(new StringReader(csvContent));

        List<Map<String, String>> records = iterator.readAll();


        XmlMapper xmlMapper = new XmlMapper();
        JsonNode jsonNode = xmlMapper.valueToTree(records);
        String xmlContent = xmlMapper.writer().withRootName("root").writeValueAsString(jsonNode);

        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.xml");
        response.getWriter().write(xmlContent);

    }

//    @PostMapping(value = "/pdf-to-html")
//    public void convertPdfToHtml(@RequestParam("file") MultipartFile file,
//                                 HttpServletResponse response) throws IOException {
//
//        InputStream inputStream = file.getInputStream();
//        PDDocument pdfContent = PDDocument.load(inputStream);
//
//        PDFDomTree parser = new PDFDomTree();
//
//        response.setContentType("text/html;charset=UTF-8");
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.html");
//        parser.writeText(pdfContent, response.getWriter());
//    }
//
//    @PostMapping(value = "/html-to-docx")
//    public void convertHtmlToDocx(@RequestParam("file") MultipartFile file,
//                                  HttpServletResponse response) throws IOException, Docx4JException, JAXBException {
//
//        InputStream inputStream = file.getInputStream();
//        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
//
//        // Get the MainDocumentPart
//        MainDocumentPart mainDocPart = wordMLPackage.getMainDocumentPart();
//
//        // Ensure StyleDefinitionsPart exists
//        if (mainDocPart.getStyleDefinitionsPart() == null) {
//            StyleDefinitionsPart styleDefinitionsPart = new StyleDefinitionsPart();
//            Styles styles = new Styles();
//            JAXBContext jc = JAXBContext.newInstance(Styles.class);  // Initialize JAXBContext for Styles
//            Marshaller marshaller = jc.createMarshaller();  // Create the Marshaller
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//            styleDefinitionsPart.setJaxbElement(styles);
//            mainDocPart.addTargetPart(styleDefinitionsPart);
//        }
//
//        // Load and convert the HTML to XHTML using Jsoup
//        Document htmlDoc = Jsoup.parse(inputStream, "UTF-8", "");
//        htmlDoc.outputSettings()
//                .syntax(Document.OutputSettings.Syntax.xml)
//                .escapeMode(Entities.EscapeMode.xhtml)
//                .prettyPrint(true);
//
//        String xhtmlContent = htmlDoc.html(); // Converts HTML to XHTML
//        System.out.println(xhtmlContent);
//
//        // Create an XHTMLImporter to convert XHTML to DOCX
//        XHTMLImporterImpl xHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
//
//        // Convert the XHTML content and add it to the MainDocumentPart
//        mainDocPart.getContent().addAll(xHTMLImporter.convert(xhtmlContent, null));
//        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.docx");
//        wordMLPackage.save(response.getOutputStream());
//    }

    @PostMapping(value = "/url-to-html")
    public void convertUrlToHtml(@RequestParam("url") String url,
                                 //@RequestParam(value = "withXhtml", required = false, defaultValue = "true") boolean withXhtml,
                                 HttpServletResponse response) throws IOException, SAXException {
        Document document = Jsoup.connect(url).get();

        Elements links = document.select("link[rel=stylesheet]");
        for (Element link : links) {
            String cssUrl = link.absUrl("href");
            String css = Jsoup.connect(cssUrl).ignoreContentType(true).execute().body();
            Element styleTag = document.createElement("style");

            styleTag.appendText(css);
            link.replaceWith(styleTag);
        }


        if (true){
            document.outputSettings()
                    .syntax(Document.OutputSettings.Syntax.xml)
                    .escapeMode(Entities.EscapeMode.xhtml)
                    .charset(StandardCharsets.UTF_8);
        }
        String htmlContent = document.html();
        //Open the network connection
//        DocumentSource docSource = new DefaultDocumentSource(url);
//
//        //Parse the input document
//        DOMSource parser = new DefaultDOMSource(docSource);
//        Document doc = parser.parse();
//
//        //Create the CSS analyzer
//        DOMAnalyzer da = new DOMAnalyzer(doc, docSource.getURL());
//        da.attributesToStyles(); //convert the HTML presentation attributes to inline styles
//        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the standard style sheet
//        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT); //use the additional style sheet
//        da.getStyleSheets(); //load the author style sheets
//
//        da.stylesToDomInherited();
//
//        Output out = new NormalOutput(doc);



        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.html");
        response.getWriter().write(htmlContent);

//        response.getWriter();
//        out.dumpTo(response.getWriter());
//        docSource.close();
    }

}
