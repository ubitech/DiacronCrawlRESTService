package eu.diacron.crawlservice.rest;

import eu.diacron.crawlservice.activemq.QueueManager;
import static eu.diacron.crawlservice.activemq.QueueManager.thread;
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
public class RetrieveWARCJob implements Job {

    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        try {

            // TODO 1: while checheck status of crawl by id 
            System.out.println("TODO: check status of crawl by id");
            


            JobDataMap data = context.getJobDetail().getJobDataMap();
            String crawlid = data.getString("CRAWL_ID");

            // TODO: get warc with crawlid  & save it at temp & unzip it & rdfizeit
            System.out.println("get warc with crawlid " + crawlid);

            //After finish...
            //1. deledeJob from scheduler


            context.getScheduler().deleteJob(context.getJobDetail().getKey());

        } catch (SchedulerException ex) {
            Logger.getLogger(RetrieveWARCJob.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }

}
