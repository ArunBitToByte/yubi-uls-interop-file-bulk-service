package com.yubi.uls.bulk.core.client.temporal;

import com.yubi.uls.bulk.core.client.Partitioner;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface PartitionerActivity extends Partitioner {

}
