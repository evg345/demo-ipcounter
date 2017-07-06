/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.repos;

import my.e345.ipcounter.model.SearchProvider;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author localEvg
 */
public interface SearchProviderRepository extends JpaRepository<SearchProvider, Long>{

    public SearchProvider findOneByTitle(String title);

}
