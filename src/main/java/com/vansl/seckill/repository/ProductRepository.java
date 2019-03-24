package com.vansl.seckill.repository;

import com.vansl.seckill.dataobject.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    @Modifying
    @Query(value="update Product p set p.productQuantity = p.productQuantity-?2" +
            " where p.productQuantity >= ?2 and p.productId = ?1")
    Integer safeDecreaseProductQuantity(Long productId, Long decreaseNum);
}
