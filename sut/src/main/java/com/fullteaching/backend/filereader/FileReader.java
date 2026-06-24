package com.fullteaching.backend.filereader;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileReader {

    private static final Logger log = LoggerFactory.getLogger(FileReader.class);

    public String parseToPlainText(File file) throws IOException, SAXException, TikaException {
        try (InputStream fileStream = new FileInputStream(file)) {
            ContentHandler handler = new BodyContentHandler();
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();
            Metadata metadata = new Metadata();
            log.info("Starting parsing...");
            parser.parse(fileStream, handler, metadata, context);
            log.info("Parsing finished...");
            return handler.toString();
        } catch (IOException e) {
            throw new IOException("Failed to parse file '" + file.getName() + "'", e);
        } catch (SAXException e) {
            throw new SAXException("Failed to parse file '" + file.getName() + "'", e);
        } catch (TikaException e) {
            throw new TikaException("Failed to parse file '" + file.getName() + "'", e);
        }
    }
}
