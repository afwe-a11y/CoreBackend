package com.tenghe.corebackend.iam.infrastructure.persistence.repository;

import com.tenghe.corebackend.iam.interfaces.TransactionManagerPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class SpringTransactionManager implements TransactionManagerPort {
  private final TransactionTemplate transactionTemplate;

  public SpringTransactionManager(PlatformTransactionManager transactionManager) {
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Override
  public void doInTransaction(Runnable action) {
    transactionTemplate.executeWithoutResult(status -> action.run());
  }
}
