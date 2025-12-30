package com.tenghe.corebackend.device.infrastructure.inmemory;

import com.tenghe.corebackend.device.interfaces.TransactionManagerPort;
import org.springframework.stereotype.Component;

@Component
public class LocalTransactionManager implements TransactionManagerPort {
    private final Object lock = new Object();

    @Override
    public void doInTransaction(Runnable action) {
        synchronized (lock) {
            action.run();
        }
    }
}
