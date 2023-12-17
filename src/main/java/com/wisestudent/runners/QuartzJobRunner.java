package com.wisestudent.runners;

import com.wisestudent.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("production")
@RequiredArgsConstructor
public class QuartzJobRunner implements ApplicationRunner {
    private final JobService jobService;

    @Override
    public void run(ApplicationArguments args) {
        jobService.createJob();
    }
}
