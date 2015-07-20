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
import eu.diacron.crawlservice.activemq.CrawlTopicConsumer;
import static eu.diacron.crawlservice.activemq.SimpleJmsApp.thread;

// class to test crawl scheduler service
public class JerseyClientPost {

    private static final String BROKER_URL = "tcp://127.0.0.1:61616?jms.prefetchPolicy.all=1000";
    private static final int CONSUME_LIFE_TIME_IN_MS = 5000 * 1000;

    public static void main(String[] args) {

        try {

            Client client = Client.create();

//            WebResource webResource = client.resource("http://localhost:8181/Diacrawl/rest/crawl/getid");
//
//            String input = "http://example.com/";
//
//            ClientResponse response = webResource.post(ClientResponse.class, input);
//
//            if (response.getStatus() != 201) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + response.getStatus());
//            }
//
//            System.out.println("Output from Server .... \n");
//            String output = response.getEntity(String.class);
//            System.out.println(output);
//
//            String topicName = output.trim();
//
//            System.out.println("topicName from Producer " + topicName);
            
            String topicName = "a374b6db-a50f-4295-9e09-6234b246246f";
            //05f4012c-1698-4a2f-b674-2876cfce048d

            CrawlTopicConsumer consumer = new CrawlTopicConsumer(BROKER_URL, topicName, CONSUME_LIFE_TIME_IN_MS);
            thread(consumer, false);

            WebResource initcrawlRequest = client.resource("http://localhost:8080/Diacrawl/rest/crawl/initcrawl");

            ClientResponse initcrawlResponse = initcrawlRequest.post(ClientResponse.class, topicName);
            if (initcrawlResponse.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : " + initcrawlResponse.getStatus());
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
