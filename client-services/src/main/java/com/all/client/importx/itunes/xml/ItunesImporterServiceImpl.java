package com.all.client.importx.itunes.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.all.client.importx.itunes.ImportItunesLibraryException;
import com.all.client.importx.itunes.ItunesImporterService;
import com.all.client.importx.itunes.xml.sax.ItunesEntityResolver;
import com.all.client.importx.itunes.xml.sax.ItunesLibSaxParser;
import com.all.core.common.services.ApplicationConfig;
import com.all.shared.model.ModelCollection;

@Service
public class ItunesImporterServiceImpl implements ItunesImporterService {
	private static final String EX_38373_UNINITIALIZATED_VISITOR_FACTORY_IT_CANNOT_BE_NULL = "ex-38373:uninitializated -visitorFactory-. It cannot be 'null'";
	private static final String EX_8283_FILE_CANNOT_BE_NULL = "ex-8283:-file- cannot be 'null'";
	private static final String PROBLEMS_PARSING_ITUNES_LIB = "Problems parsing itunes-lib";
	private static final String CANNOT_CREATE_THE_XSL_TRANSFORMER = "cannot create the xsl transformer";
	private static final String PROBLEMS_PARSING_I_TUNES_LIBRARY = "Problems parsing iTunes Library";
	private static final String XSL_FILE = "/com/all/client/importx/itunes/preprocessItunesLibXml.xsl";
	@Autowired
	private VisitorFactory visitorFactory;
	@Autowired
	private ItunesLibSaxParserFactory parserFactory;
	@Autowired
	private ApplicationConfig appConfig;

	@Override
	public ModelCollection importItunesLibrary(File itunesLibXmlFile) {
		if (this.visitorFactory == null) {
			throw new IllegalStateException(EX_38373_UNINITIALIZATED_VISITOR_FACTORY_IT_CANNOT_BE_NULL);
		}
		if (itunesLibXmlFile == null) {
			throw new IllegalArgumentException(EX_8283_FILE_CANNOT_BE_NULL);
		}

		SAXParser saxParser;
		try {
			final InputSource input = getPreprocessedXml(itunesLibXmlFile);

			saxParser = SAXParserFactory.newInstance().newSAXParser();

			final XMLReader reader = saxParser.getXMLReader();
			Visitor visitor = this.visitorFactory.newInstance();
			final ItunesLibSaxParser handler = this.parserFactory.newInstance(visitor);
			reader.setContentHandler(handler);
			reader.parse(input);
			return visitor.createModelCollection();
		} catch (ParserConfigurationException e) {
			throw new ImportItunesLibraryException(PROBLEMS_PARSING_I_TUNES_LIBRARY, e);
		} catch (SAXException e) {
			throw new ImportItunesLibraryException(PROBLEMS_PARSING_I_TUNES_LIBRARY, e);
		} catch (FileNotFoundException e) {
			throw new ImportItunesLibraryException(PROBLEMS_PARSING_I_TUNES_LIBRARY, e);
		} catch (IOException e) {
			throw new ImportItunesLibraryException(PROBLEMS_PARSING_I_TUNES_LIBRARY, e);
		}
	}

	private InputSource getPreprocessedXml(File itunesLibXmlFile) {
		try {
			InputStream xslStream = this.getClass().getResourceAsStream(XSL_FILE);
			Source xslSource = new StreamSource(xslStream);
			TransformerFactory factory = TransformerFactory.newInstance();

			Transformer t = factory.newTransformer(xslSource);
			if (t == null) {
				throw new ImportItunesLibraryException(CANNOT_CREATE_THE_XSL_TRANSFORMER);
			}

			SAXSource xmlSource = new SAXSource(new InputSource(new FileInputStream(itunesLibXmlFile)));
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlSource.setXMLReader(xmlReader);
			xmlSource.getXMLReader().setEntityResolver(new ItunesEntityResolver());
			File xmlFile2 = getPreprocessedItunesLibTempFile();
			if (xmlFile2.exists()) {
				xmlFile2.delete();
			}
			Result outputTarget2 = new StreamResult(xmlFile2);

			t.transform(xmlSource, outputTarget2);
			return new InputSource(new FileInputStream(xmlFile2));
		} catch (IllegalArgumentException e) {
			throw new ImportItunesLibraryException(PROBLEMS_PARSING_ITUNES_LIB, e);
		} catch (TransformerConfigurationException e) {
			throw new ImportItunesLibraryException(PROBLEMS_PARSING_ITUNES_LIB, e);
		} catch (TransformerException e) {
			throw new ImportItunesLibraryException(PROBLEMS_PARSING_ITUNES_LIB, e);
		} catch (FileNotFoundException e) {
			throw new ImportItunesLibraryException(PROBLEMS_PARSING_ITUNES_LIB, e);
		} catch (SAXException e) {
			throw new ImportItunesLibraryException(PROBLEMS_PARSING_ITUNES_LIB, e);
		}
	}

	File getPreprocessedItunesLibTempFile() {
		return new File(appConfig.getAllLibraryPath() + "/preprocessedItunesLibTempFile.xml");
	}

}
