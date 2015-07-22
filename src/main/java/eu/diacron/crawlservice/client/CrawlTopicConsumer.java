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
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class CrawlTopicConsumer implements Runnable, ExceptionListener
{
    private final String brokerUrl;
    private final String topicName;
    private final int lifetime;
    

    public CrawlTopicConsumer(String brokerUrl, String topicName, int lifetime)
    {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.lifetime = lifetime;
    }

    public void run()
    {
        try
        {
            System.out.println("SimpleTopicConsumer, started on " + topicName);

            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createTopic(topicName);

            // Create a MessageConsumer from the Session to the Topic or
            // Queue
            MessageConsumer consumer = session.createConsumer(destination);

            long startTime = System.currentTimeMillis();

            while (true)
            {
                System.out.println("wait");
                long now = System.currentTimeMillis();
                if (now - startTime > lifetime)
                {
                    System.out.println("Time's up, exiting...");
                    break;
                }

                // Wait for a message
                Message message = consumer.receive(1000);

                if (message == null)
                    continue;

                if (message instanceof TextMessage)
                {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("SimpleTopicConsumer - Received (text): " + text);
                    break;
                }
                else
                {
                    System.out.println("SimpleTopicConsumer - Received: " + message);
                     break;
                }
            }

            consumer.close();
            session.close();
            connection.close();
        }
        catch (Exception e)
        {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex)
    {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }
}