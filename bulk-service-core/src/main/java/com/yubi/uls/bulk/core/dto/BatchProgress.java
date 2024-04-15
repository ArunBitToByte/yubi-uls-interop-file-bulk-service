package com.yubi.uls.bulk.core.dto;


import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class BatchProgress {
    private ConcurrentMap<JobConfiguration, State> partitionStates;

    public BatchProgress() {
        partitionStates = new ConcurrentHashMap<>();
    }

    public void initialize(List<JobConfiguration> partitions) {
        partitionStates = partitions.stream()
                .collect(Collectors.toConcurrentMap(partition -> partition, partition -> State.PENDING));
    }

    public void updateCompletion(JobConfiguration partition) {
        partitionStates.put(partition, State.COMPLETED);
    }

    public void updateInProgress(JobConfiguration partition) {
        partitionStates.put(partition, State.IN_PROGRESS);
    }
}


