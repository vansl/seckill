package com.vansl.seckill.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer productId;

    /** 商品名称 **/
    private String productName;

    /** 商品价格 **/
    private BigDecimal productPrice;

    /** 商品数量 **/
    private Long productQuantity;

    @CreatedDate
    private Date createTime;

    @LastModifiedDate
    private Date updateTime;
}
