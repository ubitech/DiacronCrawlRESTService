/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.diacron.crawlservice.activemq;



public class SimpleJmsApp
{
    private static final String BROKER_URL = "tcp://localhost:61616?jms.prefetchPolicy.all=1000";
    private static final int CONSUME_LIFE_TIME_IN_MS = 5 * 1000;
    private static final String TOPIC = "test_topic";

    public static void main(String[] args) throws Exception
    {


        System.out.println("Now starting consumers...");
    
            CrawlTopicConsumer consumer = new CrawlTopicConsumer(BROKER_URL, TOPIC, CONSUME_LIFE_TIME_IN_MS);
            thread(consumer, false);
        

  
            Thread.sleep(1000);
            System.out.println("starting producers...");

                CrawlTopicProducer producer = new CrawlTopicProducer(BROKER_URL, TOPIC);
                thread(producer, false);
            
        
    }

    public static void thread(Runnable runnable, boolean daemon)
    {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
}