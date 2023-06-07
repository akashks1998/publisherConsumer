package com.modelACD.beans;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

public class UnassignmentEvent extends ApplicationEvent {

    public UnassignmentEvent(String assignmentId) {
        super(assignmentId);
    }

    public String getAssignmentId() {
        return (String) source;
    }
}
