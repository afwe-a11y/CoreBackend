package com.tenghe.corebackend.device.interfaces;

public interface TransactionManagerPort {
    void doInTransaction(Runnable action);
}
