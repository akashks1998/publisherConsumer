package com.modelACD;

import com.modelACD.beans.Assignment;
import com.modelACD.beans.ConsumerInfo;
import com.modelACD.dataStore.ConsumerDataStore;
import com.modelACD.publisher.services.PublisherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommandLineTestRunner implements CommandLineRunner {

    private final PublisherService publisherService;
    private final ConsumerDataStore consumerDataStore;

    public CommandLineTestRunner(PublisherService publisherService, ConsumerDataStore consumerDataStore) {
        this.publisherService = publisherService;
        this.consumerDataStore = consumerDataStore;
    }

    @Override
    public void run(String... args) throws Exception {

        for (int i=0;i<5;i++) {
            ConsumerInfo consumerInfo = new ConsumerInfo();
            consumerInfo.setId(Integer.toString(i));
            consumerInfo.setMaxCapacity(1);
            consumerInfo.setLastAssignmentTime(System.currentTimeMillis());
            consumerInfo.setConsumedCapacity(0);
            consumerDataStore.save(consumerInfo);
        }
        for (int i=0;i<15;i++) {
            Assignment assignment = new Assignment();
            assignment.setId(Integer.toString(i));
            publisherService.assign(assignment);
        }
    }
}
