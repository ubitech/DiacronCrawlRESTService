/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.diacron.crawlservice.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class QueueManager {

    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }

    public static class CompletedCrawlsProducer implements Runnable {

        private static String crawlid;

        public static void setCrawlid(String crawlid) {
            CompletedCrawlsProducer.crawlid = crawlid;
        }

        public void run() {
            try {

                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();

                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue(crawlid);

                // Create a MessageProducer from the Session to the Topic or Queue
                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                // Create a messages
                String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
                TextMessage message = session.createTextMessage(text);

                // Tell the producer to send the message
                System.out.println("Sent message: " + message.hashCode() + " : " + Thread.currentThread().getName());
                producer.send(message);

                // Clean up
                session.close();
                connection.close();
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }
    }

    public static class CompletedCrawlsConsumer implements Runnable, ExceptionListener {

        private static String crawlid;

        public static void setCrawlid(String crawlid) {
            CompletedCrawlsConsumer.crawlid = crawlid;
        }

        public void run() {
            try {

                // Create a ConnectionFactory
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");

                // Create a Connection
                Connection connection = connectionFactory.createConnection();
                connection.start();

                connection.setExceptionListener(this);

                // Create a Session
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                // Create the destination (Topic or Queue)
                Destination destination = session.createQueue(crawlid);

                // Create a MessageConsumer from the Session to the Topic or Queue
                MessageConsumer consumer = session.createConsumer(destination);

                // Wait for a message
                Message message = consumer.receive(1000);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Received: " + text);
                } else {
                    System.out.println("Received: " + message);
                }

                consumer.close();
                session.close();
                connection.close();
            } catch (Exception e) {
                System.out.println("Caught: " + e);
                e.printStackTrace();
            }
        }

        public synchronized void onException(JMSException ex) {
            System.out.println("JMS Exception occured.  Shutting down client.");
        }
    }

    public static void main(String[] args) throws Exception {
//        thread(new CompletedCrawlsProducer(), false);
//        thread(new CompletedCrawlsProducer(), false);
//        thread(new CompletedCrawlsConsumer(), false);
//        Thread.sleep(1000);
//        thread(new HelloWorldConsumer(), false);
//        thread(new HelloWorldProducer(), false);
        
            QueueManager.CompletedCrawlsProducer completedCrawlsProducer = new QueueManager.CompletedCrawlsProducer();
            completedCrawlsProducer.setCrawlid("555555555555555");
            thread(new QueueManager.CompletedCrawlsProducer(), false);
        
        
                 QueueManager.CompletedCrawlsConsumer completedCrawlsConsumer = new QueueManager.CompletedCrawlsConsumer();
            completedCrawlsConsumer.setCrawlid("555555555555555");
            thread(completedCrawlsConsumer, false);
        

    }
}
