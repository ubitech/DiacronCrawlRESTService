package eu.diacron.crawlservice.rest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import eu.diacron.crawlservice.activemq.CrawlTopicProducer;
import static eu.diacron.crawlservice.activemq.SimpleJmsApp.thread;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

/**
 *
 * @author eleni
 */
public class CrawlStatusJob implements Job {

    private static final String BROKER_URL = "tcp://192.168.7.139:61616?jms.prefetchPolicy.all=1000";

    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        try {

            // TODO 1: while checheck status of crawl by id 
            System.out.println("TODO: check status of crawl by id");

            JobDataMap data = context.getJobDetail().getJobDataMap();
            String crawlid = data.getString("CRAWL_ID");

            System.out.println("job name " + crawlid);

            String topicName = crawlid;

            System.out.println("topicName from Producer " + topicName);
            CrawlTopicProducer producer = new CrawlTopicProducer(BROKER_URL, topicName);
            thread(producer, false);

            //After finish...
            //1. deledeJob from scheduler
            context.getScheduler().deleteJob(context.getJobDetail().getKey());

        } catch (SchedulerException ex) {
            Logger.getLogger(CrawlStatusJob.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrawlStatusJob.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
