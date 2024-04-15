package com.yubi.uls.interop.bulk.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

  protected static final long serialVersionUID = 1L;

  @Column(columnDefinition = "TIMESTAMP", name = "created_at", updatable = false)
  protected LocalDateTime createdAt;

  @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", name = "updated_at")
  protected LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

}