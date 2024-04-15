package com.yubi.uls.bulk.core.client.temporal;

import com.yubi.uls.bulk.core.client.Writer;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface WriterActivity<T> extends Writer<T> {

}
