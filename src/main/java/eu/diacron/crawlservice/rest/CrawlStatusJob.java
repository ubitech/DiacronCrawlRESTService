package eu.diacron.crawlservice.rest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import eu.diacron.crawlservice.app.Util;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author eleni
 */
public class CrawlStatusJob implements Job {



    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        try {

            // TODO 1: while checheck status of crawl by id 
            System.out.println("TODO: check status of crawl by id");

            JobDataMap data = context.getJobDetail().getJobDataMap();
            String crawlid = data.getString("CRAWL_ID");

            System.out.println("job name " + crawlid);

            String topicName = crawlid;

            String status = Util.getCrawlStatusById(crawlid);
            System.out.println("status " + status);

            data.put("status", status);

        } catch (Exception ex) {
            Logger.getLogger(CrawlStatusJob.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
