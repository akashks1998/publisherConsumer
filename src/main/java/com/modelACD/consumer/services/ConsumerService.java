package com.modelACD.consumer.services;

import com.modelACD.beans.Assignment;
import com.modelACD.beans.UnassignmentEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    private final ApplicationEventPublisher applicationEventPublisher;

    public ConsumerService(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void consumeAssignment(Assignment assignment) {
        System.out.println("Consuming: " + assignment.getId() + " from consumer: " + assignment.getAssigneeId());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        applicationEventPublisher.publishEvent(new UnassignmentEvent(assignment.getId()));
    }
}
