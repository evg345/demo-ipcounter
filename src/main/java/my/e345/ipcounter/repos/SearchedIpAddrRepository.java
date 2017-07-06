/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.repos;

import my.e345.ipcounter.model.SearchProvider;
import my.e345.ipcounter.model.SearchedIpAddr;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author localEvg
 */
public interface SearchedIpAddrRepository extends JpaRepository<SearchedIpAddr, Long> {

    public SearchedIpAddr findOneByProviderAndIpAddress(SearchProvider provider, String ipAddress);

    public SearchedIpAddr findAllByProviderAndSubnetOrderByIpAddress(SearchProvider provider, Character subnet);

}
