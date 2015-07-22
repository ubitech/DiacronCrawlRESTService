/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.diacron.crawlservice.activemq;

/**
 *
 * @author eleni
 */
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;

public class CrawlTopicProducer implements Runnable
{
    private final String brokerUrl;
    public final String topicName;

    public CrawlTopicProducer(String brokerUrl, String topicName)
    {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
    }

    public void run()
    {
        try
        {
            
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createTopic(topicName);

            // Create a MessageProducer from the Session to the Topic or
            // Queue
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create a messages
            String text = "CrawlTopicProducer - From: " + Thread.currentThread().getName() + " : " + this.hashCode();
            TextMessage message = session.createTextMessage(text);

            // Tell the producer to send the message
            System.out.println("Sent message to Topic: " + message.hashCode() + " : " + Thread.currentThread().getName());
            producer.send(message);

            // Clean up
            session.close();
            connection.close();
        }
        catch (Exception e)
        {
            System.out.println("Caught: " + e + " CrawlTopicProducer failed to sent message to topic");
            e.printStackTrace();
        }
    }
}