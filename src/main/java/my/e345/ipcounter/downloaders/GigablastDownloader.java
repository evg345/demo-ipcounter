/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.downloaders;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Скачивание информации с поискового сайта http://www.gigablast.com/
 *
 * Deprecated потому что gigablast.com не позволяет более 25 запросов для роботов с одного IP.
 *
 * @author localEvg
 */
@Deprecated
public class GigablastDownloader  {

    private static final Logger LOG = LoggerFactory.getLogger(GigablastDownloader.class);

    public static final String PROVIDER_NAME = "gigablast";
    public static final String PROVIDER_URL = "http://www.gigablast.com/";

    static final long PAUSE_BEFOR_DOWNLOAD = 1000;
    static long LAST_HTTP_QUERY_TIME;

    RestTemplate httpClient;
    HttpEntity httpHeaderEntity;
    String rxieu; // session marker
    ObjectMapper jsonParser;

    /**
     * сервис просит не загружать его запросами чаще 1 в секунду <br>
     * если со времени последнего запроса прошло менее 1 секунды, то засыпаем
     *
     * @throws java.lang.InterruptedException
     */
    void sleepBeforDownload() throws InterruptedException {
        if (PAUSE_BEFOR_DOWNLOAD > 0) {
            long timeDelta = System.currentTimeMillis() - LAST_HTTP_QUERY_TIME;
            if (timeDelta < PAUSE_BEFOR_DOWNLOAD) {
                Thread.sleep(timeDelta);
            }
        }
    }

    /**
     * Запрашивает у поискового сайта строку, возвращает кол-во
     *
     * @param strToSearch
     * @return кол-во, сколько раз строка встречается в интеренте
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public long downloadCount(String strToSearch) throws IOException, InterruptedException {

        Random rnd = new Random();
        // Spring HTTP Client
        if (httpClient == null) {
            httpClient = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            headers.add("Accept", "*/*");

            httpHeaderEntity = new HttpEntity<String>("", headers);

            // session marker
            rxieu = Integer.toString(rnd.nextInt() + rnd.nextInt());

            // json parser
            jsonParser = new ObjectMapper();
            jsonParser.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            jsonParser.configure(Feature.ALLOW_MISSING_VALUES, true);
            jsonParser.configure(Feature.ALLOW_SINGLE_QUOTES, true);
            jsonParser.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        }

        String respS;

        synchronized (GigablastDownloader.class) {
            // засыпаем на 1 секунду со времени последнего запроса
            sleepBeforDownload();

            // ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.GET, requestEntity, String.class);
            ResponseEntity<String> responseEntity = httpClient.exchange("http://www.gigablast.com/search?format=json&q={str}&n=1&rxieu={rxieu}&rand={rand}",
                    HttpMethod.GET, httpHeaderEntity, String.class, strToSearch, rxieu, Integer.toString(rnd.nextInt()));

            respS = responseEntity.getBody();

            LAST_HTTP_QUERY_TIME = System.currentTimeMillis();
        }

        JsonNode respJ = jsonParser.readTree(respS);

        long count = respJ.path("hits").asLong();

        return count;
    }

}
