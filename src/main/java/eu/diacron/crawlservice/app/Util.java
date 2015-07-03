/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.diacron.crawlservice.app;

import java.net.URL;
import java.util.UUID;

/**
 *
 * @author eleni
 */
public class Util {

    public static String crawlpage(URL url) {
        String crawlid = UUID.randomUUID().toString();

        //TODO: Request a Capture from Hanzo Service API
        return crawlid;
    }   

}
