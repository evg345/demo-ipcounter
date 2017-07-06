/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.controller;

import my.e345.ipcounter.model.SearchedIpAddr;
import my.e345.ipcounter.repos.SearchedIpAddrRepository;
import my.e345.ipcounter.services.SearchedIpAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author localEvg
 */
@Controller
public class ListIpController {

    @Autowired
    private SearchedIpAddrService svcIpAddr;

    @Autowired
    private SearchedIpAddrRepository repoIpAddr;

    @RequestMapping("/list")
    public String doList(@RequestParam(name = "pageSize", defaultValue = "50") Integer pageSize,
			 @RequestParam(name = "pageIdx", defaultValue = "0") Integer pageIdx,
                         ModelMap modelMap) {

        //
        Pageable pgParams = new PageRequest(pageIdx, pageSize);
        Page<SearchedIpAddr> dataPage = repoIpAddr.findAll(pgParams);
        modelMap.addAttribute("dataPage", dataPage);
        modelMap.addAttribute("pageSize", pageSize);
        modelMap.addAttribute("pageIdx", pageIdx);
        
        return "list";
    }

}
