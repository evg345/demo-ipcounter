/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.downloaders;

import java.io.IOException;
import java.util.Random;

/**
 * Вместо реального запроса возвращает случайное число.
 *
 * @author localEvg
 */
public class FakeRandomDownloader extends AbstractDownloader {

    Random rnd = new Random();

    @Override
    public String getProviderName() {
        return "fake.random";
    }

    @Override
    public String getProviderUrl() {
        return "http://fakerandom";
    }

    @Override
    public long downloadCount(String strToSearch) throws IOException, InterruptedException {
        // типа что то скачали
        Thread.sleep(10);

        return rnd.nextInt(9999);
    }

}
