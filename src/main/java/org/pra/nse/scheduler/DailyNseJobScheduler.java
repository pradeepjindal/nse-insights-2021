package org.pra.nse.scheduler;

import org.pra.nse.calculation.CalculationManager;
import org.pra.nse.csv.download.DownloadManager;
import org.pra.nse.csv.transformation.TransformationManager;
import org.pra.nse.db.upload.CalcUploadManager;
import org.pra.nse.db.upload.NseUploadManager;
import org.pra.nse.processor.ProcessManager;
import org.pra.nse.report.ReportManager;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

//@Component
//@EnableScheduling
//@ConditionalOnProperty(name = "app.enable.scheduling")
public class DailyNseJobScheduler implements SchedulingConfigurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyNseJobScheduler.class);

    private final int POOL_SIZE = 4;

    private TaskScheduler taskScheduler;
    private ScheduledFuture<?> dailyJob;


    private final PraFileUtils praFileUtils;
    private final DownloadManager downloadManager;
    private final TransformationManager transformationManager;
    private final NseUploadManager nseUploadManager;
    private final CalculationManager calculationManager;
    private final CalcUploadManager calcUploadManager;
    private final ProcessManager processManager;
    private final ReportManager reportManager;

    public DailyNseJobScheduler(PraFileUtils praFileUtils,
                                DownloadManager downloadManager,
                                TransformationManager transformationManager,
                                NseUploadManager nseUploadManager,
                                CalculationManager calculationManager,
                                CalcUploadManager calcUploadManager, ProcessManager processManager,
                                ReportManager reportManager) {
        this.praFileUtils = praFileUtils;
        this.downloadManager = downloadManager;
        this.transformationManager = transformationManager;
        this.nseUploadManager = nseUploadManager;
        this.calculationManager = calculationManager;
        this.calcUploadManager = calcUploadManager;
        this.processManager = processManager;
        this.reportManager = reportManager;
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("pra-scheduler-");
        threadPoolTaskScheduler.initialize();

        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);

        assignJobToScheduler(threadPoolTaskScheduler); // Assign the dailyJob to the scheduler

        // this will be used in later part of the article during refreshing the cron expression dynamically
        this.taskScheduler = threadPoolTaskScheduler;
    }


    private void assignJobToScheduler(TaskScheduler scheduler) {
        dailyJob = scheduler.schedule(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.info("cron executed at "+ new Date());
                        try {
                            downloadManager.execute();
                            transformationManager.execute();
                            nseUploadManager.execute();
                            if(praFileUtils.validateDownloadCD() != null) {
                                calculationManager.execute();
                                calcUploadManager.execute();
                                reportManager.execute();
                            }
                            if(praFileUtils.validateDownloadCDF() != null) {
                                processManager.execute();
                                //reportManager.execute();
                            }
                        } catch(Exception e) {
                            LOGGER.error("ERROR: {}", e);
                        }
                    }
                }, new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                        //https://stackoverflow.com/questions/30887822/spring-cron-vs-normal-cron
                        //"0 0 18 * * MON-FRI" means every weekday at 6:00 PM.
                        //String cronExp="0 0/1 * * * *"; //Can be pulled from a db . This will run every minute
                        //String cronExp="0 32 10 * * *";
                        //String cronExp="0 0 19 * * *"; // daily at 6pm
                        String cronExp="0 0/30 * * * *"; // daily at 6pm
                    return new CronTrigger(cronExp).nextExecutionTime(triggerContext);
                    }
                }
        );
        LOGGER.info("job assigned - nse downloader, transformer, uploader, processor and emailer");
    }

}
