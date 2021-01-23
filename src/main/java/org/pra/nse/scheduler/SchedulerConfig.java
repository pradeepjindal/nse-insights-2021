package org.pra.nse.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

//@Component
//@EnableScheduling
//@ConditionalOnProperty(name = "app.enable.scheduling")
public class SchedulerConfig implements SchedulingConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfig.class);

    private final int POOL_SIZE = 4;

    TaskScheduler taskScheduler;

    private ScheduledFuture<?> job1;
    private ScheduledFuture<?> job2;


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("pra-scheduler-");
        threadPoolTaskScheduler.initialize();

        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);

        //job1(threadPoolTaskScheduler);// Assign the job1 to the scheduler

//        downloadJob(threadPoolTaskScheduler);// Assign the job1 to the scheduler

        this.taskScheduler=threadPoolTaskScheduler;// this will be used in later part of the article during refreshing the cron expression dynamically
    }

//    private void downloadJob(TaskScheduler scheduler) {
//        job2=scheduler.schedule(new Runnable(){
//            @Override
//            public void run() {
//                LOGGER.info("cron executed at "+ new Date());
//
//            }
//        }, new Trigger(){
//            @Override
//            public Date nextExecutionTime(TriggerContext triggerContext) {
//                //https://stackoverflow.com/questions/30887822/spring-cron-vs-normal-cron
//                //"0 0 18 * * MON-FRI" means every weekday at 6:00 PM.
//                //String cronExp="0 0/1 * * * *";//Can be pulled from a db . This will run every minute
//                //String cronExp="0 0 18 * * *";
//                String cronExp="0 32 10 * * *";
//                return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
//            }
//        });
//    }

}
