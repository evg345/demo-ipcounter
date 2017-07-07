/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.downloaders;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Yandex Search API - https://tech.yandex.ru/xml/doc/dg/concepts/about-docpage/
 *
 * @author localEvg
 */
public class YandexDownloader extends AbstractDownloader {

    RestTemplate httpClient;
    HttpEntity httpHeaderEntity;

    @Override
    public String getProviderName() {
        return "yandex";
    }

    @Override
    public String getProviderUrl() {
        return "http://ya.ru";
    }

    @Override
    public long downloadCount(String strToSearch) throws IOException, InterruptedException {

        String yaUser = System.getProperty("yandex.user");
        String yaKey = System.getProperty("yandex.key");
        String yaUrl = "https://yandex.com/search/xml?l10n=en&query=%s&user=%s&key=%s";

        String rez;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(String.format(yaUrl, strToSearch, yaUser, yaKey));

            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            /*
            <?xml version="1.0" encoding="utf-8"?>
            <yandexsearch version="1.0">
                    .....
            <response date="20120928T103130">
               <error code="15">Искомая комбинация слов нигде не встречается</error>
               <found priority="phrase">206775197</found>
               <found priority="strict">206775197</found>
               <found priority="all">206775197</found>
                    .....
            */
            XPathExpression expr = xpath.compile("/yandexsearch/response/found[@priority='all']/text()");
            rez = (String) expr.evaluate(doc, XPathConstants.STRING);

            if (rez == null || "".equals(rez)) {
                return 0L;
            }
        } catch (ParserConfigurationException | XPathExpressionException | SAXException ex) {
            Logger.getLogger(YandexDownloader.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }

        return Long.parseLong(rez);
    }

}
