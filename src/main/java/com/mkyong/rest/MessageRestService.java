package com.mkyong.rest;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

//http://localhost:8080/RESTfulExample/rest/message/hello%20world
@Path("/message")
public class MessageRestService {

    @GET
    @Path("/{param}")
    public Response printMessage(@PathParam("param") String msg) {

        String result = "Restful example : " + msg;

        try {

            //initiate process of crawling
            JobDetail job = JobBuilder.newJob(CrawlStatusJob.class).withIdentity("checkCrawlStatusJob", "group1").build();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("checkCrawlStatusTrigger", "group1").withSchedule(
                    SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(5).repeatForever())
                    .build();

            // schedule it
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);
            
           

        } catch (SchedulerException ex) {
            Logger.getLogger(MessageRestService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(result).build();

    }

    @GET
    @Path("/getjobs")
    public Response getcrawljobs() {
        
        String result ="";

        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            
            for (String groupName : scheduler.getJobGroupNames()) {
                
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    
                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();
                    
                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Date nextFireTime = triggers.get(0).getNextFireTime();
                    
                    System.out.println("[jobName] : " + jobName + " [groupName] : "
                            + jobGroup + " - " + nextFireTime);
                    
                    result +="[jobName] : " + jobName + " [groupName] : "+ jobGroup + " - " + nextFireTime ;
                    
                }
                
            }

            
        } catch (SchedulerException ex) {
            Logger.getLogger(MessageRestService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.status(200).entity(result).build();

    }

}
