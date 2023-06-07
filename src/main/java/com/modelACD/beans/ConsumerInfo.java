package com.modelACD.beans;

import lombok.Data;

@Data
public class ConsumerInfo {
    private String id;
    private Integer consumedCapacity;
    private Integer maxCapacity;
    private Long lastAssignmentTime;
}
