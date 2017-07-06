/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.services;

import java.io.IOException;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;
import my.e345.ipcounter.downloaders.AbstractDownloader;
import my.e345.ipcounter.downloaders.FakeRandomDownloader;
import my.e345.ipcounter.model.Ip4SubnetEnum;
import my.e345.ipcounter.model.SearchProvider;
import my.e345.ipcounter.model.SearchedIpAddr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис управления фоновым потоком по скачиванию информации об IP адресах.
 *
 * @author localEvg
 */
@Service
public class WorkerThreadService implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(WorkerThreadService.class);

    @Autowired
    private SearchedIpAddrService svcIpAddr;

    @Autowired
    private SearchProviderService svcSearchProvider;

    private Long searchProviderId;
    public boolean redownload = false;

    private volatile Long downloadedCount = 0L;
    private volatile String lastDownloadedIp;
    private volatile Exception lastError;;

    private Thread workingThread;
    private AbstractDownloader downloader;
    private boolean isRunning = false;

    public Long getDownloadedCount() {
        return downloadedCount;
    }

    public String getLastDownloadedIp() {
        return lastDownloadedIp;
    }

    public Exception getLastError() {
        return lastError;
    }

    public boolean getIsRunning() {
        return isRunning && (workingThread != null) && workingThread.isAlive();
    }

    /**
     * Запсусть фоновый поток по сказиванию информации из интерента
     *
     * @param redownload false - уже скаченные не будут повторно перекачиваться, true - качаем всё заново
     */
    public synchronized void start(boolean redownload) {
        if (getIsRunning()) {
            return;
        }

        this.redownload = redownload;

        if (downloader == null) {
            downloader = new FakeRandomDownloader();
        }

        workingThread = new Thread(this);
        isRunning = true;
        workingThread.start();
    }

    public synchronized void stop() {
        if (getIsRunning() == false) {
            return;
        }
        // выставляем флаг к завершению
        isRunning = false;

        try {
            // ожидаем завершение фонового потока "по хорошему"
            workingThread.join(1000 * 2);
            // или рубим "по плохому"
            if (workingThread.isAlive()) {
                workingThread.interrupt();
            }
        } catch (InterruptedException ex) {
            LOG.warn("exception while stopping: {}", ex.getMessage());
        }

        workingThread = null;

    }

    /**
     * to google - это глагол :) https://en.wikipedia.org/wiki/Google_(verb)
     *
     * @param ipAddress IPv4 адрес в виде int
     */
    @Transactional
    public void googleIt(long ipAddress) throws IOException, InterruptedException {
        String ipAddr = Ip4SubnetEnum.longToIp(ipAddress);
        lastDownloadedIp = ipAddr;
        LOG.info("query ip:{} from {}", ipAddr, downloader.getProviderUrl());


        SearchProvider provider;
        if (searchProviderId == null) {
            provider = svcSearchProvider.findCreate(downloader.getProviderName(), downloader.getProviderUrl());
            searchProviderId = provider.getId();
        } else {
            // очень быстро, из кэша, получаем провайдера по ID
            provider = svcSearchProvider.getById(searchProviderId);
        }

        // смотрим, а есть ли уже в базе искомый IP ?
        SearchedIpAddr ipAddrEntity = svcIpAddr.findOne(provider, ipAddr);
        if (redownload || ipAddrEntity == null) {
            if (ipAddrEntity == null) {
                ipAddrEntity = new SearchedIpAddr();
                ipAddrEntity.setProvider(provider);
                ipAddrEntity.setIpAddress(ipAddr);
            }
            // обращаемся к поисковому сервису
            long count = downloader.downloadCount(ipAddr);
            ipAddrEntity.setCount(count);
            downloadedCount += 1;

            // сохраняем в базу
            svcIpAddr.save(provider, ipAddr, count);
        }

    }

    @Override
    public void run() {
        searchProviderId = null;
        lastError = null;

        try {
            // в каждом подклассе подсети IPv4
            for (Ip4SubnetEnum subnet : Ip4SubnetEnum.values()) {

                // ищим первый (x.x.x.1) адрес каждой /24 сети
                PrimitiveIterator.OfLong ip4InfiniteIter = LongStream
                        .iterate(subnet.minIpAddress | 0x01, ipAddress -> (ipAddress + 256))
                        .iterator();

                // переходим от java8 stream к класическому while loop
                while (ip4InfiniteIter.hasNext()) {
                    long ip4addr = ip4InfiniteIter.next();

                    if (ip4addr > subnet.maxIpAddress) {
                        break;
                    }

                    if (isRunning == false) {
                        LOG.info("FORCE STOP background worker thread");
                        return;
                    }

                    googleIt(ip4addr);
                }
            }

        } catch (Exception ex) {
            lastError = ex;
            LOG.error("background worker thread error:", ex);
        } finally {
            searchProviderId = null;
        }
    }
}
