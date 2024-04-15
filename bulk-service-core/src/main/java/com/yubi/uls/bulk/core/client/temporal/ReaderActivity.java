package com.yubi.uls.bulk.core.client.temporal;

import com.yubi.uls.bulk.core.client.Reader;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface ReaderActivity<T> extends Reader<T> {

}
