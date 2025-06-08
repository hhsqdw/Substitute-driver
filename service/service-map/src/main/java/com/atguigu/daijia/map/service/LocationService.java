package com.atguigu.daijia.map.service;

import com.atguigu.daijia.model.form.map.SearchNearByDriverForm;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import com.atguigu.daijia.model.vo.map.NearByDriverVo;

import java.util.List;

public interface LocationService {

    // 更新司机位置信息
    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    // 删除司机位置信息
    Boolean removeDriverLocation(Long driverId);

    // 搜索附近满足条件的司机
    List<NearByDriverVo> searchNearByDriver(SearchNearByDriverForm searchNearByDriverForm);

    // 司机赶往代驾起始点：更新订单地址到缓存
    Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);
}
