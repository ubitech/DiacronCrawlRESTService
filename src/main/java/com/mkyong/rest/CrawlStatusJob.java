package com.mkyong.rest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.logging.Level;
import java.util.logging.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

/**
 *
 * @author eleni
 */
public  class CrawlStatusJob implements Job
{
	public void execute(JobExecutionContext context)
	throws JobExecutionException {
 
            try {
                //TODO: check status of crawl by id
                System.out.println("TODO: check status of crawl by id");
                
                for (int i = 0; i < 100; i++) {
                   System.out.println("TODO: check status of crawl by id:" + i); 
                }
                
                //After finish...
                //1. deledeJob from scheduler
                context.getScheduler().deleteJob(context.getJobDetail().getKey());
                
            } catch (SchedulerException ex) {
                Logger.getLogger(CrawlStatusJob.class.getName()).log(Level.SEVERE, null, ex);
            }
 
	}
 
}
