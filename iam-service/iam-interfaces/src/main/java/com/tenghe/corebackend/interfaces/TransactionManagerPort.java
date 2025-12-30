package com.tenghe.corebackend.interfaces;

public interface TransactionManagerPort {
    void doInTransaction(Runnable action);
}
