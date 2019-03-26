package com.vansl.seckill.repository;

import com.vansl.seckill.dataobject.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    @Modifying
    @Query(value="update Product p set p.productQuantity = p.productQuantity-?2 where p.productId = ?1")
    Integer decreaseProductQuantity(Long productId, Long decreaseNum);

    @Modifying
    @Query(value="update Product p set p.productQuantity = p.productQuantity-?2" +
            " where p.productQuantity >= ?2 and p.productId = ?1")
    Integer safeDecreaseProductQuantity(Long productId, Long decreaseNum);


    @Modifying
    @Query(value="update Product p set p.productQuantity = p.productQuantity-?2" +
            " where p.productQuantity =?3 and p.productId = ?1")
    Integer casDecreaseProductQuantity(Long productId, Long decreaseNum,Long expectNum);


    @Modifying
    @Query(nativeQuery = true,value="select product_quantity from product " +
            "product_id = :productId for update")
    Integer lockGetProductQuantity(@Param("productId") Long productId);
}
