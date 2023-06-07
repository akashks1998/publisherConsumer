package com.modelACD.dataStore;

import com.modelACD.beans.Assignment;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssignmentDataStore {
    private Map<String, Assignment> idVsAssignment;

    public AssignmentDataStore() {
        this.idVsAssignment = new HashMap<>();
    }

    public Assignment save(Assignment assignment) {
        if (assignment.getId() == null || assignment.getId().isBlank()) {
            assignment.setId(new ObjectId().toHexString());
        }

        if (assignment.getCreationTime() == null) {
            Assignment oldAssignment = findById(assignment.getId());
            if (oldAssignment != null && oldAssignment.getCreationTime() != null) {
                assignment.setCreationTime(assignment.getCreationTime());
            } else {
                assignment.setCreationTime(System.currentTimeMillis());
            }
        }
        idVsAssignment.put(assignment.getId(), assignment);
        return assignment;
    }

    public Assignment findById(String id) {
        return idVsAssignment.get(id);
    }

    public Assignment getOldestUnAssignedAssignment() {
        return idVsAssignment.values().stream().filter(assignment -> assignment.getAssigneeId() == null).sorted((a, b) -> (int) (a.getCreationTime() - b.getCreationTime())).findFirst().orElse(null);
    }
}
