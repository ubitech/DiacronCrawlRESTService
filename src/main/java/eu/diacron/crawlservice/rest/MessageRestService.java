package eu.diacron.crawlservice.rest;

import eu.diacron.crawlservice.app.Util;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
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

//http://localhost:8080/Diacrawl/rest/post
@Path("/message")
public class MessageRestService {

    @POST
    @Path("/post")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response crawlpage(String pageToCrawl) {

        String crawlid = null;

        try {

            // STEP 1: create new crawl process for a specific url 
             crawlid = Util.crawlpage(new URL(pageToCrawl));

            System.out.println("Crawl page: " + pageToCrawl + " with ID: " + crawlid);

            JobDetail job = JobBuilder.newJob(CrawlStatusJob.class).withIdentity(crawlid, "checkCrawlStatus").build();
            job.getJobDataMap().put("CRAWL_ID", crawlid);

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(crawlid, "checkCrawlStatus").withSchedule(
                    SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(5).repeatForever())
                    .build();

            // schedule it
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            scheduler.scheduleJob(job, trigger);

        } catch (SchedulerException ex) {
            Logger.getLogger(MessageRestService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MessageRestService.class.getName()).log(Level.SEVERE, null, ex);

            return Response.status(400).entity("Page to Crawl has malformed url").build();
        }

        return Response.status(201).entity(crawlid).build();

    }

    @GET
    @Path("/getjobs")
    public Response getcrawljobs() {

        String result = "";

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

                    result += "[jobName] : " + jobName + " [groupName] : " + jobGroup + " - " + nextFireTime;

                }

            }

        } catch (SchedulerException ex) {
            Logger.getLogger(MessageRestService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.status(200).entity(result).build();

    }

}
