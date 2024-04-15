package com.yubi.uls.bulk.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


@Data
@AllArgsConstructor
@Builder
public class JobConfiguration {
    private final Map<String, Object> parameters;

    public JobConfiguration() {
        this.parameters = new HashMap<>();
    }

}
