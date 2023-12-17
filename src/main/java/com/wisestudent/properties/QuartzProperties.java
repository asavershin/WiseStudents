package com.wisestudent.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * cleanOldComments is cron expression
 * cleanOldCommentsTime is the time in minutes. All comments of this age will be deleted.
 */
@Component
@ConfigurationProperties(prefix = "cron")
@Data
public class QuartzProperties {
    private String cleanOldComments;
    private Long cleanOldCommentsTime;
}
