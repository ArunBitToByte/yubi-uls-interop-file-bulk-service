package com.yubi.uls.bulk.core.client.temporal;

import com.yubi.uls.bulk.core.client.Processor;
import io.temporal.activity.ActivityInterface;


@ActivityInterface
public interface ProcessorActivity<T>  extends Processor<T> {

}
