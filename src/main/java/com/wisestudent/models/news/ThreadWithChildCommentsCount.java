package com.wisestudent.models.news;

public interface ThreadWithChildCommentsCount {
    public ThreadEntity getThreadEntity();
    public Long getChildCommentsCount();
}
