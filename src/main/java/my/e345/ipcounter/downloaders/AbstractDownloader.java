/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.e345.ipcounter.downloaders;

import java.io.IOException;

/**
 *
 * @author localEvg
 */
public abstract class AbstractDownloader {

    public abstract String getProviderName();

    public abstract String getProviderUrl();

    public abstract long downloadCount(String strToSearch) throws IOException, InterruptedException;
    
}
