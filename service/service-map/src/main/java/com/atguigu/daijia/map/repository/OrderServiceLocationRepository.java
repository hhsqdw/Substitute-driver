package com.atguigu.daijia.map.repository;

import com.atguigu.daijia.model.entity.map.OrderServiceLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderServiceLocationRepository extends MongoRepository<OrderServiceLocation, String> {

    // 根据订单id获取位置信息，按照创建时间排序
    List<OrderServiceLocation> findByOrderIdOrderByCreateTimeAsc(Long orderId);
}
