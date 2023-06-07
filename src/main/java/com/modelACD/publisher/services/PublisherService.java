package com.modelACD.publisher.services;


import com.modelACD.beans.Assignment;
import com.modelACD.beans.ConsumerInfo;
import com.modelACD.beans.UnassignmentEvent;
import com.modelACD.consumer.services.ConsumerService;
import com.modelACD.dataStore.AssignmentDataStore;
import com.modelACD.dataStore.ConsumerDataStore;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Service
public class PublisherService {
    private final AssignmentDataStore assignmentDataStore;
    private final ConsumerDataStore consumerDataStore;
    private final ConsumerService consumerService;
    private final Lock lock;

    public PublisherService(AssignmentDataStore assignmentDataStore, ConsumerDataStore consumerDataStore,
                            ConsumerService consumerService) {
        this.assignmentDataStore = assignmentDataStore;
        this.consumerDataStore = consumerDataStore;
        this.consumerService = consumerService;
        this.lock = new ReentrantLock();
    }

    public void tryAssign() {
        lock.lock();
        Assignment assignment = assignmentDataStore.getOldestUnAssignedAssignment();
        if (assignment == null) {
            return;
        }
        System.out.println("assigning " + assignment.getId());
        lock.unlock();
        assign(assignment);
    }

    public void assign(Assignment assignment) {
        lock.lock();
        assignment = assignmentDataStore.save(assignment);
        List<ConsumerInfo> consumerInfos = consumerDataStore.findAll();
        List<ConsumerInfo> freeConsumers =
                consumerInfos.stream().filter(consumerInfo -> consumerInfo.getMaxCapacity() > consumerInfo.getConsumedCapacity()).toList();
        if (freeConsumers.isEmpty()) {
            System.out.println("returning " + assignment.getId());
            lock.unlock();
            return;
        }
        consumerInfos.sort((a, b) -> {
            if (!Objects.equals(a.getConsumedCapacity(), b.getConsumedCapacity())) {
                return a.getConsumedCapacity() - b.getConsumedCapacity();
            }
            return a.getLastAssignmentTime() > b.getLastAssignmentTime() ? 1 : -1;
        });
        ConsumerInfo consumerInfo = consumerInfos.get(0);
        assignment.setAssigneeId(consumerInfo.getId());
        assignment.setStartTime(System.currentTimeMillis());
        consumerInfo.setConsumedCapacity(consumerInfo.getConsumedCapacity() +1);
        consumerInfo.setLastAssignmentTime(System.currentTimeMillis());
        consumerDataStore.save(consumerInfo);
        assignmentDataStore.save(assignment);
        System.out.println("Assigned: " + assignment.getId() + " from consumer: " + assignment.getAssigneeId());
        Assignment finalAssignment = assignment;
        CompletableFuture.runAsync(() -> consumerService.consumeAssignment(finalAssignment));
        lock.unlock();
    }

    @EventListener
    public void onUnassignmentEvent(UnassignmentEvent unassignmentEvent) {
        lock.lock();
        String assignmentId = unassignmentEvent.getAssignmentId();
        Assignment assignment = assignmentDataStore.findById(assignmentId);
        String consumerId = assignment.getAssigneeId();
        ConsumerInfo consumerInfo = consumerDataStore.findById(consumerId);
        consumerInfo.setConsumedCapacity(consumerInfo.getConsumedCapacity() - 1);
        consumerDataStore.save(consumerInfo);
        assignment.setEndTime(System.currentTimeMillis());
        assignmentDataStore.save(assignment);
        System.out.println("Unassigned: " + assignmentId + " from consumer: " + consumerId + " capacity " + consumerInfo.getConsumedCapacity());
        lock.unlock();
        tryAssign();
    }
}
