package com.tenghe.corebackend.iam.interfaces;

public interface TransactionManagerPort {
  void doInTransaction(Runnable action);
}
