/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.diacron.crawlservice.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eleni
 */
public final class ConfigController {

    private final Properties prop = new Properties();

    private static ConfigController instance = null;

    protected ConfigController() {
        readProperties();

    }

    public static ConfigController getInstance() {
        if (instance == null) {
            instance = new ConfigController();
        }
        return instance;
    }

    public void readProperties() {
        try {
            InputStream input;

            input = this.getClass().getClassLoader().getResourceAsStream("diacrawl.properties");

            prop.load(input);

            // load a properties file
            Configuration.BROKER_URL = prop.getProperty("BROKER_URL").trim();


            input.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}//EoC

