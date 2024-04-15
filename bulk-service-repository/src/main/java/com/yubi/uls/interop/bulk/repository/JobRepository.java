package com.yubi.uls.interop.bulk.repository;

import com.yubi.uls.interop.bulk.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JobRepository extends JpaRepository<JobEntity, String> {


}
