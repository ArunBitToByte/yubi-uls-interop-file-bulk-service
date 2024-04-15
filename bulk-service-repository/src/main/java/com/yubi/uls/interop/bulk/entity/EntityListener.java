package com.yubi.uls.interop.bulk.entity;


import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import java.lang.reflect.Field;
import java.util.UUID;

@Component
public class EntityListener {

    @PrePersist
    public void entityPrePersistEvent(Object entity) throws IllegalAccessException, NoSuchFieldException {
            Field id = entity.getClass().getDeclaredField("id");
            id.setAccessible(true);
            if(id.get(entity) != null) return;
            String s = UUID.randomUUID().toString();
            id.set(entity, s);
    }
}
