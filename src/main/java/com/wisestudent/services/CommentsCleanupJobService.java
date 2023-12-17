package com.wisestudent.services;

import com.wisestudent.dto.admin.RequestUpdateJob;
import com.wisestudent.models.DefaultCommentEntity;
import com.wisestudent.properties.QuartzProperties;
import com.wisestudent.repositories.DefaultCommentRepository;
import com.wisestudent.repositories.DefaultFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentsCleanupJobService implements JobService {

    private final QuartzProperties quartzProperties;
    private final Scheduler scheduler;
    private final DefaultCommentRepository defaultCommentRepository;
    private final CommentService commentService;
    private final FileService fileService;
    private final DefaultFileRepository commentFileRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("Old comments deleting started");

        var timeInPast = LocalDateTime.now().minusMinutes(quartzProperties.getCleanOldCommentsTime());

        var pageNumber = 0;
        while (true) {
            var pageable = PageRequest.of(pageNumber, 100, Sort.by(
                    Sort.Order.asc("id")
            ));
            var commentsWithFiles = defaultCommentRepository.getAllByCreatedAtBefore(timeInPast, pageable);
            if (!commentsWithFiles.hasContent()) {
                break;
            }
            commentService.deleteBatch(commentsWithFiles.getContent());
            pageNumber++;
        }

        log.info("Deleted old comments");
    }

    @Override
    public void createJob() {
        try {
            // Создание объекта JobDetail
            JobDetail jobDetail = JobBuilder.newJob(CommentsCleanupJobService.class)
                    .withIdentity("postCleanupJob", "cleanupGroup")
                    .storeDurably(true)
                    .build();

            // Создание объекта Trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("postCleanupTrigger", "cleanupGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule(quartzProperties.getCleanOldComments()))
                    .build();

            // Регистрация JobDetail и Trigger в планировщике
            scheduler.scheduleJob(jobDetail, trigger);

            log.info("Job created successfully");
        } catch (SchedulerException e) {
            log.error("Failed to create job", e);
        }
    }

    @Override
    public void updateJobSchedule(RequestUpdateJob request) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey("postCleanupTrigger", "cleanupGroup");
            CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            if (oldTrigger != null) {
                TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(request.getCron()));

                CronTrigger newTrigger = triggerBuilder.build();

                scheduler.rescheduleJob(triggerKey, newTrigger);
                log.info("Job schedule updated to: {}", request.getCron());
                log.info("Age of deleted comments updated to: {}", request.getMinusMinutes());
            } else {
                log.warn("Trigger not found");
            }
        } catch (SchedulerException e) {
            log.error("Failed to reschedule comments cleanup job", e);
        }
        quartzProperties.setCleanOldCommentsTime(request.getMinusMinutes());
    }
}
