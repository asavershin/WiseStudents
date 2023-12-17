package com.wisestudent.services;

import com.wisestudent.dto.admin.RequestUpdateJob;
import org.quartz.Job;

public interface JobService extends Job {
    void createJob();
    void updateJobSchedule(RequestUpdateJob request);
}
