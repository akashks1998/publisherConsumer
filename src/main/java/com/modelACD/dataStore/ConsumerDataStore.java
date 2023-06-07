package com.modelACD.dataStore;

import com.modelACD.beans.ConsumerInfo;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConsumerDataStore {
    private final Map<String, ConsumerInfo> idVsConsumerInfo;

    public ConsumerDataStore() {
        this.idVsConsumerInfo = new HashMap<>();
    }

    public ConsumerInfo save(ConsumerInfo consumerInfo) {
        if (consumerInfo.getId() == null || consumerInfo.getId().isBlank()) {
            consumerInfo.setId(new ObjectId().toHexString());
        }
        idVsConsumerInfo.put(consumerInfo.getId(), consumerInfo);
        return consumerInfo;
    }

    public ConsumerInfo findById(String id) {
        return idVsConsumerInfo.get(id);
    }

    public List<ConsumerInfo> findAll() {
        return idVsConsumerInfo.values().stream().collect(Collectors.toList());
    }
}
