package com.modelACD.beans;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Assignment {
    private String id;
    private Long creationTime;
    private String assigneeId;
    private Long startTime;
    private Long endTime;
}
