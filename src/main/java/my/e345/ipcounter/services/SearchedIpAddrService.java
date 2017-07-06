/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.services;

import java.time.LocalDateTime;
import my.e345.ipcounter.model.SearchProvider;
import my.e345.ipcounter.model.SearchedIpAddr;
import my.e345.ipcounter.repos.SearchedIpAddrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author localEvg
 */
@Service
public class SearchedIpAddrService {

    @Autowired
    private SearchedIpAddrRepository repoSearchedIpAddr;

    public SearchedIpAddr findOne(SearchProvider provider, String ip4addr) {
        return repoSearchedIpAddr.findOneByProviderAndIpAddress(provider, ip4addr);
    }

    public SearchedIpAddr save(SearchedIpAddr sia) {
        sia.setQueryDateTime(LocalDateTime.now());
        return repoSearchedIpAddr.save(sia);
    }

    public SearchedIpAddr save(SearchProvider provider, String ip4addr, Long count) {
        SearchedIpAddr rez = findOne(provider, ip4addr);
        if (rez == null) {
            rez = new SearchedIpAddr();
            rez.setProvider(provider);
            rez.setIpAddress(ip4addr);
        }

        rez.setCount(count);
        return save(rez);
    }

}
