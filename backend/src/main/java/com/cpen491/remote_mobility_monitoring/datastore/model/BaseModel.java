package com.cpen491.remote_mobility_monitoring.datastore.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseModel {
    String pid;
    String sid;
    String createdAt;
    String updatedAt;
}
