package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.TransactionManagerPort;
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
