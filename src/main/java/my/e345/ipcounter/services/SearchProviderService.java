/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.services;

import my.e345.ipcounter.model.SearchProvider;
import my.e345.ipcounter.repos.SearchProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author localEvg
 */
@Service
public class SearchProviderService {

    @Autowired
    private SearchProviderRepository repoSearchProvider;

    public SearchProvider findCreate(String title, String url) {
        SearchProvider rez = repoSearchProvider.findOneByTitle(title);
        if (rez == null) {
            rez = new SearchProvider();
            rez.setTitle(title);
            rez.setUrl(url);
            rez = repoSearchProvider.save(rez);
        }
        return rez;
    }

    public SearchProvider getById(Long id) {
        return repoSearchProvider.getOne(id);
    }
}
