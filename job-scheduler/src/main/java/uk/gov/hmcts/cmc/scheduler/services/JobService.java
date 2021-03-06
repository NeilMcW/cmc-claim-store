package uk.gov.hmcts.cmc.scheduler.services;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.cmc.scheduler.exceptions.JobException;
import uk.gov.hmcts.cmc.scheduler.model.JobData;

import java.time.ZonedDateTime;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

@Service
public class JobService {

    private final Scheduler scheduler;

    @Autowired
    public JobService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public JobKey scheduleJob(JobData jobData, ZonedDateTime startDateTime) {
        try {
            scheduler.scheduleJob(
                newJob(jobData.getJobClass())
                    .withIdentity(jobData.getId(), jobData.getGroup())
                    .withDescription(jobData.getDescription())
                    .usingJobData(new JobDataMap(jobData.getData()))
                    .requestRecovery()
                    .build(),
                newTrigger()
                    .startAt(Date.from(startDateTime.toInstant()))
                    .withIdentity(jobData.getId(), jobData.getGroup())
                    .withDescription(jobData.getDescription())
                    .withSchedule(
                        simpleSchedule()
                            .withMisfireHandlingInstructionNowWithExistingCount()
                    )
                    .build()
            );

            return JobKey.jobKey(jobData.getId(), jobData.getGroup());

        } catch (SchedulerException exc) {
            throw new JobException("Error while scheduling a job", exc);
        }
    }

    public JobKey rescheduleJob(JobData jobData, ZonedDateTime startDateTime) {
        try {

            TriggerKey triggerKey = triggerKey(jobData.getId(), jobData.getGroup());
            SimpleTrigger newTrigger = newTrigger()
                .startAt(Date.from(startDateTime.toInstant()))
                .withIdentity(jobData.getId(), jobData.getGroup())
                .withDescription(jobData.getDescription())
                .withSchedule(
                    simpleSchedule()
                        .withMisfireHandlingInstructionNowWithExistingCount()
                )
                .build();

            Date rescheduleJob = scheduler.rescheduleJob(triggerKey, newTrigger);

            if (rescheduleJob == null) {
                scheduleJob(jobData, startDateTime);
            }

            return JobKey.jobKey(jobData.getId(), jobData.getGroup());

        } catch (SchedulerException exc) {
            throw new JobException("Error while rescheduling a job", exc);
        }
    }
}
