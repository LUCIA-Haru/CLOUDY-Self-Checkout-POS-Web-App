package com.Cloudy.Cloudy_Self_Checkout_POS.dao;

import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.Purchase;
import com.Cloudy.Cloudy_Self_Checkout_POS.POJO.PurchaseItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchaseItemDAO extends CrudRepository<PurchaseItem,Long> {
    List<PurchaseItem> findByPurchase(Purchase purchase);
    void deleteByPurchase(Purchase purchase);


}
