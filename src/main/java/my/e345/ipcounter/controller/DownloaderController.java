/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import my.e345.ipcounter.services.WorkerThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author localEvg
 */
@Controller
public class DownloaderController {

    @Autowired
    WorkerThreadService svcThreadMnr;

    //TODO: startedAt, progress, estimates
    @ModelAttribute("isRunning")
    public Boolean getIsRunning() {
        return svcThreadMnr.getIsRunning();
    }

    @ModelAttribute("downloadCount")
    public Long getDownloadCount() {
        return svcThreadMnr.getDownloadedCount();
    }

    @ModelAttribute("lastDownloadedIp")
    public String getLastDownloadedIp() {
        return svcThreadMnr.getLastDownloadedIp();

    }

    @ModelAttribute("lastErrorMsg")
    public String getLastErrorMsg() {
        Exception ex = svcThreadMnr.getLastError();
        if (ex == null) {
            return null;
        }
        return ex.getMessage();
    }

    @RequestMapping("/status")
    public String doStatus() {
        //return "classpath:resources/templates/status";
        return "status";
    }

    @RequestMapping("/start")
    public String doStart(@RequestParam(name = "force", required = false) Boolean force) {
        svcThreadMnr.start(Objects.equals(force, Boolean.TRUE));
        return "redirect:/status";
    }

    @RequestMapping("/stop")
    public String doStop() {
        svcThreadMnr.stop();
        return "redirect:/status";
    }

}
