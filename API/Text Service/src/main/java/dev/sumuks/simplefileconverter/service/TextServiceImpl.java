package dev.sumuks.simplefileconverter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

@Service
public class TextServiceImpl {

    // XML to List of Map
    public List<Map<String, Object>> xmlToListOfMap(String xmlContent) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xmlContent, xmlMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    // CSV to List of Map
    public List<Map<String, Object>> csvToListOfMap(String csvContent) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();

        MappingIterator<Map<String, Object>> iterator = csvMapper.readerFor(Map.class)
                .with(csvSchema)
                .readValues(new StringReader(csvContent));

        return iterator.readAll();

    }

    // JSON to List of Map
    public List<Map<String, Object>> jsonToListOfMap(String jsonContent) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonTree = objectMapper.readTree(jsonContent.getBytes());
        if(!jsonTree.isArray()) {
            jsonTree = jsonTree.get(jsonTree.fieldNames().next());
        }

        return objectMapper.readValue(jsonTree.toString(), objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
    }

    // List of Map to XML
    public String listOfMapToXml(List<Map<String, Object>> records) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.writer().withRootName("root").writeValueAsString(records);
    }

    // List of Map to CSV
    public String listOfMapToCsv(List<Map<String, Object>> records) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema.Builder schemaBuilder = CsvSchema.builder().setUseHeader(true);

        records.get(0)
                .keySet()
                .forEach(schemaBuilder::addColumn);

        CsvSchema schema = schemaBuilder.build();

        return csvMapper.writerFor(List.class)
                .with(schema)
                .writeValueAsString(records);
    }

    // List of Map to JSON
    public String listOfMapToJson(List<Map<String, Object>> records) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
    }
}
