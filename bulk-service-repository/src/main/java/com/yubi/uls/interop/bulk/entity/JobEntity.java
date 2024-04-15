package com.yubi.uls.interop.bulk.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "Job")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JobEntity extends AbstractEntity {

    @Id
    @Column(name="job_id", nullable = false)
    private String jobId;

    @Column(name="job_name", nullable = false)
    private String jobName;

    @Type(type = "jsonb")
    @Column(name = "job_config", columnDefinition = "jsonb")
    private JsonNode config;

}
