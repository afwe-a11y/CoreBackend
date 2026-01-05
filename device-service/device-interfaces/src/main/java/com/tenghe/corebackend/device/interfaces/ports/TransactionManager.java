package com.tenghe.corebackend.device.interfaces.ports;

public interface TransactionManager {
  void doInTransaction(Runnable action);
}
