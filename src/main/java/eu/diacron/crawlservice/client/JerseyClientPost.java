/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.diacron.crawlservice.client;

/**
 *
 * @author eleni
 */
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import eu.diacron.crawlservice.activemq.QueueManager;
import static eu.diacron.crawlservice.activemq.QueueManager.thread;

// class to test crawl scheduler service
public class JerseyClientPost {

    public static void main(String[] args) {

        try {

            Client client = Client.create();

            WebResource webResource = client.resource("http://localhost:8181/Diacrawl/rest/message/post");

            String input = "http://www.ubitech.eu/";

            ClientResponse response = webResource.post(ClientResponse.class, input);

            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            System.out.println("Output from Server .... \n");
            String output = response.getEntity(String.class);
            System.out.println(output);

//            Thread.sleep(10000);
//            
//            
//            QueueManager.CompletedCrawlsConsumer completedCrawlsConsumer = new QueueManager.CompletedCrawlsConsumer();
//            completedCrawlsConsumer.setCrawlid(output.trim());
//            thread(completedCrawlsConsumer, false);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
