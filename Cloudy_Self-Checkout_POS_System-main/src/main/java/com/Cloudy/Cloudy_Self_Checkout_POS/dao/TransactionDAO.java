package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Purchase;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionDAO extends CrudRepository<Transaction,Long> {
    List<Transaction> findByPurchase(Purchase purchase);

    List<Transaction> findByPurchaseOrderByTransactionDateDesc(Purchase purchase);
}
