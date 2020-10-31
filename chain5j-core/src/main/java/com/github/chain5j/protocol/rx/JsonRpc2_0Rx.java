package com.github.chain5j.protocol.rx;

import java.util.concurrent.ScheduledExecutorService;

import com.github.chain5j.protocol.Chain5j;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * reactive API implementation.
 */
public class JsonRpc2_0Rx {

    private final Chain5j chain5j;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Scheduler scheduler;

    public JsonRpc2_0Rx(Chain5j chain5j, ScheduledExecutorService scheduledExecutorService) {
        this.chain5j = chain5j;
        this.scheduledExecutorService = scheduledExecutorService;
        this.scheduler = Schedulers.from(scheduledExecutorService);
    }

}
