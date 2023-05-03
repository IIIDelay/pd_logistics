package org.iiidev.pinda.controller;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import org.iiidev.pinda.DTO.*;
import org.iiidev.pinda.DTO.angency.AgencyScopeDto;
import org.iiidev.pinda.DTO.user.CourierScopeDto;
import org.iiidev.pinda.authority.api.AreaApi;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.authority.entity.common.Area;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.common.context.RequestContext;
import org.iiidev.pinda.common.utils.DateUtils;
import org.iiidev.pinda.common.utils.EntCoordSyncJob;
import org.iiidev.pinda.common.utils.PageResponse;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.entity.AddressBook;
import org.iiidev.pinda.entity.Member;
import org.iiidev.pinda.enums.OrderPaymentStatus;
import org.iiidev.pinda.enums.OrderStatus;
import org.iiidev.pinda.enums.OrderType;
import org.iiidev.pinda.enums.pickuptask.PickupDispatchTaskAssignedStatus;
import org.iiidev.pinda.enums.pickuptask.PickupDispatchTaskStatus;
import org.iiidev.pinda.enums.pickuptask.PickupDispatchTaskType;
import org.iiidev.pinda.feign.*;
import org.iiidev.pinda.feign.agency.AgencyScopeFeign;
import org.iiidev.pinda.feign.user.CourierScopeFeign;
import org.iiidev.pinda.future.PdCompletableFuture;
import org.iiidev.pinda.service.IMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>
 * 寄件  前端控制器
 * </p>
 *
 * @author diesel
 * @since 2020-3-30
 */
@Log4j2
@Api(tags = "业务接口")
@RestController
@RequestMapping("mailing")
public class MailingController {
    private final AgencyScopeFeign agencyScopeFeign;
    private final OrgApi orgApi;
    private final TransportTaskFeign transportTaskFeign;
    private final TransportOrderFeign transportOrderFeign;
    private final UserApi userApi;
    private final AreaApi areaApi;
    private final PickupDispatchTaskFeign pickupDispatchTaskFeign;
    private final OrderFeign orderFeign;
    private final CargoFeign cargoFeign;
    private final IMemberService memberService;
    private final AddressBookFeign addressBookFeign;
    private final CourierScopeFeign courierScopeFeign;

    public MailingController(AgencyScopeFeign agencyScopeFeign, OrgApi orgApi, TransportTaskFeign transportTaskFeign, TransportOrderFeign transportOrderFeign, UserApi userApi, AreaApi areaApi, PickupDispatchTaskFeign pickupDispatchTaskFeign, OrderFeign orderFeign, CargoFeign cargoFeign, IMemberService memberService, AddressBookFeign addressBookFeign, CourierScopeFeign courierScopeFeign) {
        this.agencyScopeFeign = agencyScopeFeign;
        this.orgApi = orgApi;
        this.transportTaskFeign = transportTaskFeign;
        this.transportOrderFeign = transportOrderFeign;
        this.userApi = userApi;
        this.areaApi = areaApi;
        this.pickupDispatchTaskFeign = pickupDispatchTaskFeign;
        this.orderFeign = orderFeign;
        this.cargoFeign = cargoFeign;
        this.memberService = memberService;
        this.addressBookFeign = addressBookFeign;
        this.courierScopeFeign = courierScopeFeign;
    }

    private OrderDTO buildOrderAndPrice(MailingSaveDTO entity) {
        // 获取地址详细信息
        AddressBook sendAddress = addressBookFeign.detail(entity.getSendAddress());
        AddressBook receiptAddress = addressBookFeign.detail(entity.getReceiptAddress());
        log.info("sendAddress:{},{} receiptAddress:{},{}", entity.getSendAddress(), sendAddress, entity.getReceiptAddress(), receiptAddress);
        OrderDTO orderAddDto = new OrderDTO();
        orderAddDto.setSenderName(sendAddress.getName());
        orderAddDto.setSenderPhone(sendAddress.getPhoneNumber());
        orderAddDto.setSenderProvinceId(sendAddress.getProvinceId().toString());
        orderAddDto.setSenderCityId(sendAddress.getCityId().toString());
        orderAddDto.setSenderCountyId(sendAddress.getCountyId().toString());
        orderAddDto.setSenderAddress(sendAddress.getAddress());
        orderAddDto.setSenderAddressId(entity.getSendAddress());

        orderAddDto.setReceiverName(receiptAddress.getName());
        orderAddDto.setReceiverPhone(receiptAddress.getPhoneNumber());
        orderAddDto.setReceiverProvinceId(receiptAddress.getProvinceId().toString());
        orderAddDto.setReceiverCityId(receiptAddress.getCityId().toString());
        orderAddDto.setReceiverCountyId(receiptAddress.getCountyId().toString());
        orderAddDto.setReceiverAddress(receiptAddress.getAddress());
        orderAddDto.setReceiverAddressId(entity.getReceiptAddress());

        orderAddDto.setPaymentMethod(entity.getPayMethod());
        orderAddDto.setPaymentStatus(1); // 默认未付款

        orderAddDto.setOrderType(orderAddDto.getReceiverCityId().equals(orderAddDto.getSenderCityId()) ? OrderType.INCITY.getCode() : OrderType.OUTCITY.getCode());
        orderAddDto.setPickupType(entity.getPickupType());
        Map map = orderFeign.getOrderMsg(orderAddDto);
        orderAddDto.setAmount(new BigDecimal(map.getOrDefault("amount", "23").toString()));
        //费用计算在订单服务中
//        orderAddDto.setAmount(new BigDecimal("23"));
        return orderAddDto;
    }

    private OrderCargoDto buildOrderCargo(MailingSaveDTO entity) {
        OrderCargoDto cargoDto = new OrderCargoDto();
        cargoDto.setName(entity.getGoodsName());
        cargoDto.setGoodsTypeId(entity.getGoodsType());
        cargoDto.setWeight(new BigDecimal(entity.getGoodsWeight()));
        cargoDto.setQuantity(1);
        cargoDto.setTotalWeight(cargoDto.getWeight().multiply(new BigDecimal(cargoDto.getQuantity())));
        return cargoDto;
    }

    /**
     * 预估总价
     *
     * @param entity
     * @return
     */
    @ApiOperation("预估总价")
    @PostMapping("totalPrice")
    public RespResult totalPrice(@RequestBody MailingSaveDTO entity) {
        log.info("预估总价：{}", entity);
        OrderDTO orderAddDto = buildOrderAndPrice(entity);
        log.info("结果：{}", orderAddDto);
        RespResult respResult = RespResult.ok().put("amount", orderAddDto.getAmount().toString());
        log.info("返回结果：{}", respResult);
        return respResult;
    }

    /**
     * 下单
     *
     * @param entity
     * @return
     */
    @ApiOperation("下单")
    @PostMapping("")
    public RespResult save(@RequestBody MailingSaveDTO entity) {
        log.info("下单：{}", entity);
        //获取userid
        String userId = RequestContext.getUserId();

        // 获取地址详细信息
        OrderDTO orderDTO = buildOrderAndPrice(entity);
        orderDTO.setMemberId(userId);
        log.info("构建订单信息：{}", orderDTO);

        // 根据下单发货地址调用百度坐标计算分配的网点
        RespResult respResult = getAgencyId(orderDTO);
        if (!respResult.get("code").toString().equals("0")) {
            return respResult;
        }
        String agencyId = respResult.get("agencyId").toString();
        orderDTO.setCurrentAgencyId(agencyId);
        // 根据下单发货地址地图所在区域id获取所有快递员作业范围并计算最短距离
        String sendLocation = respResult.get("location").toString();
        RespResult respResultCourier = getCourierId(sendLocation, orderDTO);

        if (!respResultCourier.get("code").toString().equals("0")) {
            return respResultCourier;
        }
        String courierId = respResultCourier.get("userId").toString();

        //获取收货地址调用地图计算分配网点
        RespResult respResultReceive = getReceiveAgencyId(orderDTO);
        if (!respResultReceive.get("code").toString().equals("0")) {
            return respResultReceive;
        }
        String receiveAgencyId = respResultReceive.get("agencyId").toString();
        String receiveAgentLocation = respResultReceive.get("location").toString();

        OrderCargoDto cargoDto = buildOrderCargo(entity);
        orderDTO.setOrderCargoDto(cargoDto);
        orderDTO = orderFeign.save(orderDTO);
        //保存订单位置信息
        OrderLocationDto orderLocationDto = new OrderLocationDto();
        orderLocationDto.setOrderId(orderDTO.getId());
        orderLocationDto.setSendLocation(sendLocation);
        orderLocationDto.setSendAgentId(agencyId);
        orderLocationDto.setReceiveLocation(receiveAgentLocation);
        orderLocationDto.setReceiveAgentId(receiveAgencyId);
        orderFeign.saveOrUpdateLoccation(orderLocationDto);

        if ("send error msg".equals(orderDTO.getSenderAddress())) {
            return RespResult.error("发货地址不存在,请输入真实地址便于计算距离");
        }
        if ("receive error msg".equals(orderDTO.getReceiverAddress())) {
            return RespResult.error("发货地址不存在,请输入真实地址便于计算距离");
        }

        if (orderDTO.getId() != null) {
            cargoDto.setOrderId(orderDTO.getId());
            cargoFeign.save(cargoDto);
        }

        log.info("下单分配网点:{},快递员:{}", agencyId, courierId);

        TaskPickupDispatchDTO pickupDispatchTaskDTO = new TaskPickupDispatchDTO();
        pickupDispatchTaskDTO.setOrderId(orderDTO.getId());
        pickupDispatchTaskDTO.setTaskType(PickupDispatchTaskType.PICKUP.getCode());
        pickupDispatchTaskDTO.setStatus(PickupDispatchTaskStatus.PENDING.getCode());
        pickupDispatchTaskDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.TO_BE_DISTRIBUTED.getCode());
        pickupDispatchTaskDTO.setCreateTime(LocalDateTime.now());
        pickupDispatchTaskDTO.setAgencyId(agencyId);
        pickupDispatchTaskDTO.setCourierId(courierId);
        pickupDispatchTaskDTO.setAgencyId(agencyId);
        String[] pickupTimes = entity.getPickUpTime().split("-");
        String[] pickupTimeStrs = pickupTimes[1].split(":");
        LocalDateTime pickupDateTime = LocalDateTime.now().withHour(Integer.parseInt(pickupTimeStrs[0])).withMinute(Integer.parseInt(pickupTimeStrs[1])).withSecond(00);
        pickupDispatchTaskDTO.setEstimatedStartTime(LocalDateTime.now());
        pickupDispatchTaskDTO.setEstimatedEndTime(DateUtils.getUTCTime(pickupDateTime));
        if (StringUtils.isNotBlank(courierId)) {
            pickupDispatchTaskDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.DISTRIBUTED.getCode());
        } else {
            pickupDispatchTaskDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.MANUAL_DISTRIBUTED.getCode());
        }

        pickupDispatchTaskFeign.save(pickupDispatchTaskDTO);
        System.out.println(1/0);
        return RespResult.ok().put("amount", orderDTO.getAmount());
    }

    private RespResult getReceiveAgencyId(OrderDTO orderDTO) {
        String address = receiverFullAddress(orderDTO);
        if (StringUtils.isBlank(address)) {
            return RespResult.error("下单时收货地址不能为空");
        }
        String location = EntCoordSyncJob.getCoordinate(address);
        log.info("订单收货地址和坐标-->" + address + "--" + location);
        if (StringUtils.isBlank(location)) {
            return RespResult.error("下单时收货地址不能为空");
        }
        //根据坐标获取区域检查区域是否正常
        Map map = EntCoordSyncJob.getLocationByPosition(location);
        if (ObjectUtils.isEmpty(map)) {
            return RespResult.error("根据地图获取区域信息为空");
        }
        String adcode = map.getOrDefault("adcode", "").toString();
        Result<Area> r = areaApi.getByCode(adcode + "000000");
        if (!r.getIsSuccess()) {
            RespResult.error(r.getMsg());
        }
        Area area = r.getData();
        if (area == null) {
            return RespResult.error("区域编码:" + adcode + "区域信息未从库中获取到");
        }
        Long areaId = area.getId();
        if (!orderDTO.getReceiverCountyId().equals(String.valueOf(areaId))) {
            return RespResult.error("参数中收货区域id和坐标计算出真实区域id不同，数据不合法");
        }
        List<AgencyScopeDto> agencyScopes = agencyScopeFeign.findAllAgencyScope(areaId + "", null, null, null);
        if (agencyScopes == null || agencyScopes.size() == 0) {
            return RespResult.error("根据区域无法从机构范围获取网点信息列表");
        }
        RespResult res = calcuate(location, agencyScopes);
        if (!res.get("code").toString().equals("0")) {
            return res;
        }
        RespResult respResult = new RespResult();
        respResult.put("agencyId", res.get("agencyId").toString());
        respResult.put("location", location);
        return respResult;
    }

    @SneakyThrows
    private String receiverFullAddress(OrderDTO orderDTO) {
        StringBuffer stringBuffer = new StringBuffer();

        Long province = Long.valueOf(orderDTO.getReceiverProvinceId());
        Long city = Long.valueOf(orderDTO.getReceiverCityId());
        Long county = Long.valueOf(orderDTO.getReceiverCountyId());

        Set areaIdSet = new HashSet();
        areaIdSet.add(province);
        areaIdSet.add(city);
        areaIdSet.add(county);

        CompletableFuture<Map<Long, Area>> areaMapFuture = PdCompletableFuture.areaMapFuture(areaApi, null, areaIdSet);
        Map<Long, Area> areaMap = areaMapFuture.get();

        stringBuffer.append(areaMap.get(province).getName());
        stringBuffer.append(areaMap.get(city).getName());
        stringBuffer.append(areaMap.get(county).getName());
        stringBuffer.append(orderDTO.getReceiverAddress());

        return stringBuffer.toString();
    }

    /**
     * 根据发货区域获取最近的快递员
     *
     * @param orderDTO
     * @return
     */
    private RespResult getCourierId(String location, OrderDTO orderDTO) {
        List<CourierScopeDto> courierScopeDtoList = courierScopeFeign.findAllCourierScope(orderDTO.getSenderCountyId(), null);
        if (courierScopeDtoList == null || courierScopeDtoList.size() == 0) {
            return RespResult.error("根据区域无法从快递员作业范围中获取网点信息列表");
        }

        RespResult res = calcuateCourier(location, courierScopeDtoList);
        if (!res.get("code").toString().equals("0")) {
            return res;
        }
        RespResult respResult = new RespResult();
        respResult.put("userId", res.get("userId").toString());
        return respResult;

    }

    /**
     * 循环计算距离发件地址最近的快递员
     *
     * @param location
     * @param courierScopeDtoList
     * @return
     */
    private RespResult calcuateCourier(String location, List<CourierScopeDto> courierScopeDtoList) {
        log.info("循环计算包含发件地址的快递员:{}  {}", location, JSONArray.toJSONString(courierScopeDtoList));
        try {
            for (CourierScopeDto courierScopeDto : courierScopeDtoList) {
                List<List<Map>> mutiPoints = courierScopeDto.getMutiPoints();
                for (List<Map> list : mutiPoints) {
                    String[] originArray = location.split(",");
                    boolean flag = EntCoordSyncJob.isInScope(list, Double.parseDouble(originArray[0]), Double.parseDouble(originArray[1]));
                    if (flag) {
                        log.info("找到包含发件地址的快递员:{}  {}", courierScopeDto.getUserId());
                        return RespResult.ok().put("userId", courierScopeDto.getUserId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取所属快递员异常", e);
            return RespResult.error(5000, "获取所属快递员失败");
        }
        return RespResult.error(5000, "获取所属快递员失败");
    }

    /**
     * 根据计算获取网点
     *
     * @param orderDTO
     * @return
     */
    private RespResult getAgencyId(OrderDTO orderDTO) {
        String address = senderFullAddress(orderDTO);
        if (StringUtils.isBlank(address)) {
            return RespResult.error("下单时发货地址不能为空");
        }
        String location = EntCoordSyncJob.getCoordinate(address);
        log.info("订单发货地址和坐标-->" + address + "--" + location);
        if (StringUtils.isBlank(location)) {
            return RespResult.error("下单时发货地址不能为空");
        }
        //根据坐标获取区域检查区域是否正常
        Map map = EntCoordSyncJob.getLocationByPosition(location);
        if (ObjectUtils.isEmpty(map)) {
            return RespResult.error("根据地图获取区域信息为空");
        }
        String adcode = map.getOrDefault("adcode", "").toString();
        Result<Area> r = areaApi.getByCode(adcode + "000000");
        if (!r.getIsSuccess()) {
            RespResult.error(r.getMsg());
        }
        Area area = r.getData();
        if (area == null) {
            return RespResult.error("区域编码:" + adcode + "区域信息未从库中获取到");
        }
        Long areaId = area.getId();
        if (!orderDTO.getSenderCountyId().equals(String.valueOf(areaId))) {
            log.info("参数中区域id和坐标计算出真实区域id不同,数据不合法。{},{}", orderDTO.getSenderCountyId(), areaId);
            return RespResult.error("参数中区域id和坐标计算出真实区域id不同，数据不合法");
        }
        List<AgencyScopeDto> agencyScopes = agencyScopeFeign.findAllAgencyScope(areaId + "", null, null, null);
        if (agencyScopes == null || agencyScopes.size() == 0) {
            return RespResult.error("根据区域无法从机构范围获取网点信息列表");
        }
        RespResult res = calcuate(location, agencyScopes);
        if (!res.get("code").toString().equals("0")) {
            return res;
        }
        RespResult respResult = new RespResult();
        respResult.put("agencyId", res.get("agencyId").toString());
        respResult.put("location", location);
        return respResult;
    }

    @SneakyThrows
    private String senderFullAddress(OrderDTO orderDTO) {
        StringBuffer stringBuffer = new StringBuffer();

        Long province = Long.valueOf(orderDTO.getSenderProvinceId());
        Long city = Long.valueOf(orderDTO.getSenderCityId());
        Long county = Long.valueOf(orderDTO.getSenderCountyId());

        Set areaIdSet = new HashSet();
        areaIdSet.add(province);
        areaIdSet.add(city);
        areaIdSet.add(county);

        CompletableFuture<Map<Long, Area>> areaMapFuture = PdCompletableFuture.areaMapFuture(areaApi, null, areaIdSet);
        Map<Long, Area> areaMap = areaMapFuture.get();

        stringBuffer.append(areaMap.get(province).getName());
        stringBuffer.append(areaMap.get(city).getName());
        stringBuffer.append(areaMap.get(county).getName());
        stringBuffer.append(orderDTO.getSenderAddress());

        return stringBuffer.toString();
    }

    /**
     * 循环计算距离发件地址最近的网点
     *
     * @param location
     * @param agencyScopes
     * @return
     */
    private RespResult calcuate(String location, List<AgencyScopeDto> agencyScopes) {
        log.info("循环计算包含发件地址的网点:{}  {}", location, JSONArray.toJSONString(agencyScopes));
        try {
            for (AgencyScopeDto agencyScopeDto : agencyScopes) {
                List<List<Map>> mutiPoints = agencyScopeDto.getMutiPoints();
                for (List<Map> list : mutiPoints) {
                    String[] originArray = location.split(",");
                    boolean flag = EntCoordSyncJob.isInScope(list, Double.parseDouble(originArray[0]), Double.parseDouble(originArray[1]));
                    if (flag) {
                        log.info("找到包含发件地址的网点:{}  {}", agencyScopeDto.getAgencyId());
                        return RespResult.ok().put("agencyId", agencyScopeDto.getAgencyId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取所属网点异常", e);
            return RespResult.error(5000, "获取所属网点失败");
        }
        return RespResult.error(5000, "获取所属网点失败");
    }


    /**
     * 拼坐标
     *
     * @param pointMap
     * @return
     */
    private String getPoint(Map pointMap) {
        String lng = pointMap.getOrDefault("lng", "").toString();
        String lat = pointMap.getOrDefault("lat", "").toString();
        return lng + "," + lat;
    }

    /**
     * 修改 下单
     *
     * @param entity
     * @return
     */
    @ApiOperation("修改订单")
    @PutMapping("/{id}")
    public RespResult update(@PathVariable("id") String id, @RequestBody MailingSaveDTO entity) {
        log.info("修改订单 id:{} params：{}", id, entity);
        //获取userid
        String userId = RequestContext.getUserId();

        OrderDTO order = orderFeign.findById(id);
        log.info("原订单 id:{} respResult：{}", id, order);
        String oldSenderCountId = order.getSenderCountyId();
        String oldSendAdress = order.getSenderAddress();
        // 获取地址详细信息
        OrderDTO orderDTO = buildOrderAndPrice(entity);
        orderDTO.setMemberId(userId);
        orderDTO.setCreateTime(LocalDateTime.now());
        orderDTO = orderFeign.updateById(order.getId(), orderDTO);
        log.info("新订单 id:{} params：{}", id, orderDTO);

        if (orderDTO.getId() != null) {
            List<OrderCargoDto> cargoDtos = cargoFeign.findAll(null, orderDTO.getId());
            log.info("原订单附加信息 id:{} respResult：{}", id, cargoDtos);
            for (OrderCargoDto item : cargoDtos) {
                OrderCargoDto cargoDto = buildOrderCargo(entity);
                cargoDto.setOrderId(orderDTO.getId());
                OrderCargoDto resultCargoDto = cargoFeign.update(item.getId(), cargoDto);
                log.info("原订单附加信息修改 id:{} params：{} respResult:{}", id, cargoDto, resultCargoDto);
            }
        }

        List<AgencyScopeDto> agencyScope = agencyScopeFeign.findAllAgencyScope(orderDTO.getSenderCountyId(), null, null, null);
//        String agencyId = null;
//        String courierId = null;
//        if (!CollectionUtils.isEmpty(agencyScope)) {
//            agencyId = agencyScope.get(0).getAgencyId();
//            //岗位id
//            Long stationId = StaticStation.COURIER_ID;
//            RespResult<List<User>> userRs = userApi.list(null, stationId, null, Long.valueOf(agencyId));
//            if (userRs.getData() != null && userRs.getData().size() > 0) {
//                User user = userRs.getData().get(0);
//                courierId = user.getId().toString();
//            }
//        }
        //通过计算得到最新的网点和快递员
        RespResult respResult = getAgencyId(orderDTO);
        if (!respResult.get("code").toString().equals("0")) {
            return respResult;
        }
        String agencyId = respResult.get("agencyId").toString();
        // 根据下单发货地址地图所在区域id获取所有快递员作业范围并计算最短距离
        RespResult respResultCourier = getCourierId(respResult.get("location").toString(), orderDTO);

        if (!respResultCourier.get("code").toString().equals("0")) {
            return respResultCourier;
        }
        String courierId = respResultCourier.get("userId").toString();


        String sendLocation = respResult.get("location").toString();
        //获取收货地址调用地图计算分配网点
        RespResult respResultReceive = getReceiveAgencyId(orderDTO);
        if (!respResultReceive.get("code").toString().equals("0")) {
            return respResultReceive;
        }
        String receiveAgencyId = respResultReceive.get("agencyId").toString();
        String receiveAgentLocation = respResultReceive.get("location").toString();
        OrderLocationDto orderLocationDto = orderFeign.selectByOrderId(order.getId());
        //更新位置信息
        OrderLocationDto orderLocationDtoUpdate = new OrderLocationDto();
        orderLocationDtoUpdate.setId(orderLocationDto.getId());
        orderLocationDtoUpdate.setOrderId(orderDTO.getId());
        orderLocationDtoUpdate.setSendLocation(sendLocation);
        orderLocationDtoUpdate.setSendAgentId(agencyId);
        orderLocationDtoUpdate.setReceiveLocation(receiveAgentLocation);
        orderLocationDtoUpdate.setReceiveAgentId(receiveAgencyId);
        orderFeign.saveOrUpdateLoccation(orderLocationDtoUpdate);

        TaskPickupDispatchDTO taskPickupDispatchDTO = pickupDispatchTaskFeign.findByOrderId(id, PickupDispatchTaskType.PICKUP.getCode());
        log.info("查询取件任务 id:{} respResult:{}", id, taskPickupDispatchDTO);

//        if (!oldSenderCountId.equals(orderDTO.getSenderCountyId())) {
        if (!oldSendAdress.equals(orderDTO.getSenderAddress())) {
            log.info("发件地址不一致 需要重新匹配快递员 id:{}", id);
            if (taskPickupDispatchDTO != null && StringUtils.isNotBlank(taskPickupDispatchDTO.getId())) {
                TaskPickupDispatchDTO taskPickupDispatchEditDTO = new TaskPickupDispatchDTO();
                taskPickupDispatchEditDTO.setStatus(PickupDispatchTaskStatus.CANCELLED.getCode());
                taskPickupDispatchEditDTO.setCancelTime(LocalDateTime.now());
                taskPickupDispatchEditDTO.setMark("客户更换寄件方地址");
                pickupDispatchTaskFeign.updateById(taskPickupDispatchDTO.getId(), taskPickupDispatchEditDTO);
            }

            if (!CollectionUtils.isEmpty(agencyScope)) {
                log.info("下单分配网点:{},快递员:{}", agencyId, courierId);
                TaskPickupDispatchDTO pickupDispatchTaskDTO = new TaskPickupDispatchDTO();
                pickupDispatchTaskDTO.setOrderId(order.getId());
                pickupDispatchTaskDTO.setTaskType(PickupDispatchTaskType.PICKUP.getCode());
                pickupDispatchTaskDTO.setStatus(PickupDispatchTaskStatus.PENDING.getCode());
                pickupDispatchTaskDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.TO_BE_DISTRIBUTED.getCode());
                pickupDispatchTaskDTO.setCreateTime(LocalDateTime.now());
                pickupDispatchTaskDTO.setAgencyId(agencyId);
                pickupDispatchTaskDTO.setCourierId(courierId);
                pickupDispatchTaskDTO.setAgencyId(agencyId);
                String[] pickupTimes = entity.getPickUpTime().split("-");
                String[] pickupTimeStrs = pickupTimes[1].split(":");
                LocalDateTime pickupDateTime = LocalDateTime.now().withHour(Integer.parseInt(pickupTimeStrs[0])).withMinute(Integer.parseInt(pickupTimeStrs[1])).withSecond(00);
                log.info("预计取件时间：{}  {}", pickupDateTime, LocalDateTime.now());
                pickupDispatchTaskDTO.setEstimatedStartTime(LocalDateTime.now());
                pickupDispatchTaskDTO.setEstimatedEndTime(DateUtils.getUTCTime(pickupDateTime));
                if (StringUtils.isNotBlank(courierId)) {
                    pickupDispatchTaskDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.DISTRIBUTED.getCode());
                } else {
                    pickupDispatchTaskDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.MANUAL_DISTRIBUTED.getCode());
                }

                pickupDispatchTaskFeign.save(pickupDispatchTaskDTO);
            }
        } else {
            log.info("发件地址相同，更新取件任务");

            log.info("修改订单更新网点:{},快递员:{}", agencyId, courierId);

            TaskPickupDispatchDTO pickupDispatchTaskDTO = new TaskPickupDispatchDTO();
            pickupDispatchTaskDTO.setOrderId(order.getId());
            pickupDispatchTaskDTO.setTaskType(PickupDispatchTaskType.PICKUP.getCode());
            pickupDispatchTaskDTO.setStatus(PickupDispatchTaskStatus.PENDING.getCode());
            pickupDispatchTaskDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.TO_BE_DISTRIBUTED.getCode());
            pickupDispatchTaskDTO.setCreateTime(LocalDateTime.now());
            pickupDispatchTaskDTO.setAgencyId(agencyId);
            pickupDispatchTaskDTO.setCourierId(courierId);
            pickupDispatchTaskDTO.setAgencyId(agencyId);
            String[] pickupTimes = entity.getPickUpTime().split("-");
            String[] pickupTimeStrs = pickupTimes[1].split(":");
            LocalDateTime pickupDateTime = LocalDateTime.now().withHour(Integer.parseInt(pickupTimeStrs[0])).withMinute(Integer.parseInt(pickupTimeStrs[1])).withSecond(00);
            pickupDispatchTaskDTO.setEstimatedStartTime(LocalDateTime.now());
            pickupDispatchTaskDTO.setEstimatedEndTime(DateUtils.getUTCTime(pickupDateTime));
            if (StringUtils.isNotBlank(courierId)) {
                pickupDispatchTaskDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.DISTRIBUTED.getCode());
            } else {
                pickupDispatchTaskDTO.setAssignedStatus(PickupDispatchTaskAssignedStatus.MANUAL_DISTRIBUTED.getCode());
            }

            pickupDispatchTaskFeign.updateById(taskPickupDispatchDTO.getId(), pickupDispatchTaskDTO);
            log.info("修改取件任务信息：{} , {}", taskPickupDispatchDTO.getId(), pickupDispatchTaskDTO);
        }

        return RespResult.ok();
    }

    /**
     * 支付 下单
     *
     * @return
     */
    @ApiOperation("支付")
    @PutMapping("/pay/{id}")
    public RespResult pay(@PathVariable("id") String id) {
        try {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setPaymentStatus(OrderPaymentStatus.PAID.getStatus());
            orderFeign.updateById(id, orderDTO);
            return RespResult.ok();
        } catch (Exception e) {
            return RespResult.error();
        }
    }


    /**
     * 取消 下单
     *
     * @return
     */
    @ApiOperation("取消")
    @PutMapping("/cancel/{id}")
    public RespResult cancel(@PathVariable("id") String id) {
        log.info("客户取消订单：{}", id);
        // 增加分布式锁，防止用户在揽收时取消订单，造成脏数据
        try {
            log.info("加锁成功：{}", id);
            // 获取地址详细信息
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setStatus(OrderStatus.CANCELLED.getCode());
            orderFeign.updateById(id, orderDTO);

            //删除位置信息
            OrderLocationDto orderLocationDto = new OrderLocationDto();
            orderLocationDto.setOrderId(id);
            orderFeign.deleteOrderLocation(orderLocationDto);

            TaskPickupDispatchDTO taskPickupDispatchDTO = pickupDispatchTaskFeign.findByOrderId(id, PickupDispatchTaskType.PICKUP.getCode());
            if (taskPickupDispatchDTO != null && StringUtils.isNotBlank(taskPickupDispatchDTO.getId())) {
                TaskPickupDispatchDTO taskPickupDispatchEditDTO = new TaskPickupDispatchDTO();
                taskPickupDispatchEditDTO.setStatus(PickupDispatchTaskStatus.CANCELLED.getCode());
                taskPickupDispatchEditDTO.setCancelTime(LocalDateTime.now());
                taskPickupDispatchEditDTO.setMark("客户取消");
                pickupDispatchTaskFeign.updateById(taskPickupDispatchDTO.getId(), taskPickupDispatchEditDTO);
            }
            return RespResult.ok();
        } catch (Exception e) {
            log.error("客户取消下单失败", e);
        }
        return RespResult.error();
    }


    @ApiOperation("待处理数量查询")
    @GetMapping("count")
    public RespResult count(MailingQueryDTO dto) {
        //获取userid
        String userId = RequestContext.getUserId();

        OrderSearchDTO orderSearchDto = new OrderSearchDTO();
        orderSearchDto.setPage(1);
        orderSearchDto.setPageSize(1);
        orderSearchDto.setKeyword(dto.getKeyword());
        if (0 == dto.getMailType()) {
            // 我寄的
            orderSearchDto.setMemberId(userId);
        } else {
            // 我收的 通过收件人手机号查询
            Member member = memberService.detail(userId);
            if (member == null || StringUtils.isEmpty(member.getPhone())) {
                return RespResult.ok().put("count", 0);
            }
            String phone = member.getPhone();
            orderSearchDto.setReceiverPhone(phone);
        }

        PageResponse<OrderDTO> result = orderFeign.pageLikeForCustomer(orderSearchDto);

        return RespResult.ok().put("count", result.getCounts());
    }

    /**
     * 查询
     *
     * @param dto
     * @return
     */
    @ApiOperation("分页查询")
    @SneakyThrows
    @GetMapping("page")
    public RespResult page(MailingQueryDTO dto) {
        //获取userid
        String userId = RequestContext.getUserId();

        OrderSearchDTO orderSearchDto = new OrderSearchDTO();
        orderSearchDto.setPage(dto.getPage());
        orderSearchDto.setPageSize(dto.getPagesize());
        orderSearchDto.setKeyword(dto.getKeyword());
        if (0 == dto.getMailType()) {
            // 我寄的
            orderSearchDto.setMemberId(userId);
        } else {
            // 我收的 通过收件人手机号查询
            Member member = memberService.detail(userId);
            if (member == null || StringUtils.isEmpty(member.getPhone())) {
                return new RespResult().ok().put("data", PageResponse.<CustomerOrderDTO>builder().page(dto.getPage()).pagesize(dto.getPagesize()).pages(0L).counts(0L).build());
            }
            String phone = member.getPhone();
            orderSearchDto.setReceiverPhone(phone);
        }
        PageResponse<OrderDTO> result = orderFeign.pageLikeForCustomer(orderSearchDto);
        if (result.getItems().size() > 0) {
            // 地址信息
            Set<Long> addressSet = new HashSet<>();
            addressSet.addAll(result.getItems().stream().filter(item -> item.getReceiverProvinceId() != null).map(item -> Long.valueOf(item.getReceiverProvinceId())).collect(Collectors.toSet()));
            addressSet.addAll(result.getItems().stream().filter(item -> item.getReceiverCityId() != null).map(item -> Long.valueOf(item.getReceiverCityId())).collect(Collectors.toSet()));
            addressSet.addAll(result.getItems().stream().filter(item -> item.getReceiverCountyId() != null).map(item -> Long.valueOf(item.getReceiverCountyId())).collect(Collectors.toSet()));
            addressSet.addAll(result.getItems().stream().filter(item -> item.getSenderProvinceId() != null).map(item -> Long.valueOf(item.getSenderProvinceId())).collect(Collectors.toSet()));
            addressSet.addAll(result.getItems().stream().filter(item -> item.getSenderCityId() != null).map(item -> Long.valueOf(item.getSenderCityId())).collect(Collectors.toSet()));
            addressSet.addAll(result.getItems().stream().filter(item -> item.getSenderCountyId() != null).map(item -> Long.valueOf(item.getSenderCountyId())).collect(Collectors.toSet()));
            CompletableFuture<Map<Long, Area>> areaMapFuture = PdCompletableFuture.areaMapFuture(areaApi, null, addressSet);

            // 物品详细  数量
            Set<String> cargoSet = result.getItems().stream().map(item -> item.getId()).collect(Collectors.toSet());
            CompletableFuture<Map<String, OrderCargoDto>> cargoMapFuture = PdCompletableFuture.cargoMapFuture(cargoFeign, cargoSet);

            // 运单
            Set<String> transportOrderDTOSet = result.getItems().stream().map(item -> item.getId()).collect(Collectors.toSet());
            CompletableFuture<Map<String, TransportOrderDTO>> transportOrderMapFuture = PdCompletableFuture.transportOrderMapFuture(transportOrderFeign, transportOrderDTOSet);
            //派送信息
            Set<String> iPickupDispatchTaskSet = result.getItems().stream().map(item -> item.getId()).collect(Collectors.toSet());
            // 任务类型，1为取件任务，2为派件任务
            CompletableFuture<Map<String, TaskPickupDispatchDTO>> taskPickupDispatchPullMapFuture = PdCompletableFuture.taskTranSportMapFuture(pickupDispatchTaskFeign, iPickupDispatchTaskSet, PickupDispatchTaskType.PICKUP.getCode());
            CompletableFuture<Map<String, TaskPickupDispatchDTO>> taskPickupDispatchPushMapFuture = PdCompletableFuture.taskTranSportMapFuture(pickupDispatchTaskFeign, iPickupDispatchTaskSet, PickupDispatchTaskType.DISPATCH.getCode());

            // 统一获取
            Map<Long, Area> areaMap = areaMapFuture.get();
            Map<String, OrderCargoDto> cargoMap = cargoMapFuture.get();
            Map<String, TransportOrderDTO> transportOrderMap = transportOrderMapFuture.get();
            Map<String, TaskPickupDispatchDTO> taskPickupDispatchPullMap = taskPickupDispatchPullMapFuture.get();
            Map<String, TaskPickupDispatchDTO> taskPickupDispatchPushMap = taskPickupDispatchPushMapFuture.get();

            List<CustomerOrderDTO> newItem = result.getItems().stream().map(item -> {
                CustomerOrderDTO customerOrderDto = new CustomerOrderDTO(item, areaMap, cargoMap, transportOrderMap, taskPickupDispatchPullMap, taskPickupDispatchPushMap);
                String id = customerOrderDto.getId();
                List<RouteDTO> routeList = (List<RouteDTO>) route(id).get("data");
                if (!CollectionUtils.isEmpty(routeList)) {
                    customerOrderDto.setRouteDTO(routeList.get(0));
                } else {
                    customerOrderDto.setRouteDTO(RouteDTO.builder().build());
                }
                return customerOrderDto;
            }).collect(Collectors.toList());

            return new RespResult().ok().put("data", PageResponse.<CustomerOrderDTO>builder()
                    .page(dto.getPage())
                    .pagesize(dto.getPagesize())
                    .pages(result.getPages())
                    .counts(result.getCounts())
                    .items(newItem)
                    .build());
        } else {
            return new RespResult().ok().put("data", PageResponse.<CustomerOrderDTO>builder()
                    .page(dto.getPage())
                    .pagesize(dto.getPagesize())
                    .pages(0L)
                    .counts(0L)
                    .items(Lists.newArrayList())
                    .build());
        }

    }

    /**
     * 查询 明细
     *
     * @param id 订单id
     * @return
     */
    @ApiOperation("明细")
    @SneakyThrows
    @GetMapping("detail")
    public RespResult detail(String id) {
        CustomerOrderDetailDTO.CustomerOrderDetailDTOBuilder builder = CustomerOrderDetailDTO.builder();
        //获取userid
        String userId = RequestContext.getUserId();
        //订单信息
        OrderDTO orderDTO = orderFeign.findById(id);
        builder.id(orderDTO.getId());
        builder.status(orderDTO.getStatus());
        //快递员信息
        TaskPickupDispatchDTO queryDTO = new TaskPickupDispatchDTO();
        queryDTO.setOrderId(id);
        List<TaskPickupDispatchDTO> pickupDispatchTasks = pickupDispatchTaskFeign.findAll(queryDTO);
        log.info("根据订单id查询取件任务：{}", pickupDispatchTasks);
        if (orderDTO.getStatus().equals(OrderStatus.CANCELLED.getCode())) {
            pickupDispatchTasks = pickupDispatchTasks.stream().filter(item -> (PickupDispatchTaskStatus.CANCELLED.getCode().equals(item.getStatus()))).collect(Collectors.toList());
        } else {
            pickupDispatchTasks = pickupDispatchTasks.stream().filter(item -> (!PickupDispatchTaskStatus.CANCELLED.getCode().equals(item.getStatus()))).collect(Collectors.toList());
        }
        Map<Integer, TaskPickupDispatchDTO> taskPickupDispatchDTOMap = pickupDispatchTasks.stream().
                collect(Collectors.toMap(TaskPickupDispatchDTO::getTaskType, taskPickupDispatchDTO -> taskPickupDispatchDTO, (v1, v2) -> v1));
        log.info("解析取件任务：{}", taskPickupDispatchDTOMap);
        //预计上门取件时间
        TaskPickupDispatchDTO taskPickupDispatchPullDTO = taskPickupDispatchDTOMap.get(PickupDispatchTaskType.PICKUP.getCode());//iPickupDispatchTaskClient.findByOrderIdAndTaskType(id, 1);
        //派送时间
        TaskPickupDispatchDTO taskPickupDispatchPushDTO = taskPickupDispatchDTOMap.get(PickupDispatchTaskType.DISPATCH.getCode());//iPickupDispatchTaskClient.findByOrderIdAndTaskType(id, 2);

        TaskPickupDispatchDTO taskPickupDispatchCurrentDTO = null;
        if (null != taskPickupDispatchPullDTO) {
            taskPickupDispatchCurrentDTO = taskPickupDispatchPullDTO;
        }
        if (null != taskPickupDispatchPushDTO) {
            taskPickupDispatchCurrentDTO = taskPickupDispatchPushDTO;
        }
        if (null != taskPickupDispatchCurrentDTO) {
            builder.planPickUpTime(taskPickupDispatchCurrentDTO.getEstimatedEndTime());
            builder.actualDispathedTime(taskPickupDispatchCurrentDTO.getActualEndTime());
            builder.cancelTime(taskPickupDispatchCurrentDTO.getCancelTime());

            //快递员id
            String courierId = taskPickupDispatchCurrentDTO.getCourierId();
            if (courierId != null) {
                Result<User> userResult = userApi.get(Long.valueOf(courierId));
                User user = userResult.getData();
                builder.name(user.getName());
                builder.mobile(user.getMobile());
                builder.avatar(user.getAvatar());
            }
        }
        // 运单号
        TransportOrderDTO transportOrderDTO = transportOrderFeign.findByOrderId(orderDTO.getId());
        builder.tranOrderId(transportOrderDTO != null ? transportOrderDTO.getId() : "");
        CustomerOrderDetailDTO data = builder.build();
        log.info("result:{}", data);
        return RespResult.ok().put("data", data);
    }

    /**
     * 查询 路由信息
     *
     * @param id 订单id
     * @return
     */
    @SneakyThrows
    @ApiOperation("路由")
    @GetMapping("route")
    public RespResult route(String id) {
        List<RouteDTO> result = new ArrayList<>();
        //订单信息
        OrderDTO orderDTO = orderFeign.findById(id);
        //快递员信息
        TaskPickupDispatchDTO queryDTO = new TaskPickupDispatchDTO();
        queryDTO.setOrderId(id);

        List<TaskPickupDispatchDTO> pickupDispatchTasks = pickupDispatchTaskFeign.findAll(queryDTO);
        log.info("根据订单id查询取件任务：{}", pickupDispatchTasks);
        if (orderDTO.getStatus().equals(OrderStatus.CANCELLED.getCode())) {
            pickupDispatchTasks = pickupDispatchTasks.stream().filter(item -> (PickupDispatchTaskStatus.CANCELLED.getCode().equals(item.getStatus()))).collect(Collectors.toList());
        } else {
            pickupDispatchTasks = pickupDispatchTasks.stream().filter(item -> (!PickupDispatchTaskStatus.CANCELLED.getCode().equals(item.getStatus()))).collect(Collectors.toList());
        }
        Map<Integer, TaskPickupDispatchDTO> taskPickupDispatchDTOMap = pickupDispatchTasks.stream().
                collect(Collectors.toMap(TaskPickupDispatchDTO::getTaskType, taskPickupDispatchDTO -> taskPickupDispatchDTO, (v1, v2) -> v1));
        log.info("解析取件任务：{}", taskPickupDispatchDTOMap);
        //取件任务
        TaskPickupDispatchDTO taskPickupDispatchPullDTO = taskPickupDispatchDTOMap.get(1);
        //派送任务
        TaskPickupDispatchDTO taskPickupDispatchPushDTO = taskPickupDispatchDTOMap.get(2);

        RouteDTO.RouteDTOBuilder builder;
        /**
         * 构建取件阶段路由 可能是取消
         */
        builder = RouteDTO.builder();
        // 已取消

        // 待揽收  计划取件时间  已揽收 实际取件时间
        if (null != taskPickupDispatchPullDTO) {
            if (PickupDispatchTaskStatus.CANCELLED.getCode() == taskPickupDispatchPullDTO.getStatus()) {
                builder.time(taskPickupDispatchPullDTO.getCreateTime());
                builder.status(OrderStatus.CANCELLED.getCode());
                builder.msg("取消订单");
            } else {
                if (PickupDispatchTaskStatus.PENDING.getCode() == taskPickupDispatchPullDTO.getStatus()) {
                    builder.time(taskPickupDispatchPullDTO.getEstimatedStartTime());
                    builder.status(OrderStatus.PENDING.getCode());
                    builder.msg("等待快递员上门取件");
                } else {
                    builder.time(taskPickupDispatchPullDTO.getActualStartTime());
                    builder.status(OrderStatus.PICKED_UP.getCode());
                    builder.msg("已收取快递");
                }
            }
        }
        result.add(builder.build());

        /**
         * 构建运送阶段路由
         */
        TransportOrderDTO transportOrderDTO = transportOrderFeign.findByOrderId(orderDTO.getId());
        if (transportOrderDTO == null) {
            Collections.reverse(result);
            return RespResult.ok().put("data", result);
        }

        List<TaskTransportDTO> transportTaskDTOs = transportTaskFeign.findAllByOrderIdOrTaskId(transportOrderDTO.getId(), null);
        if (transportTaskDTOs != null && transportTaskDTOs.size() > 0) {
            Set<String> agencySet = new HashSet<>();
            agencySet.addAll(transportTaskDTOs.stream().map(item -> item.getStartAgencyId()).collect(Collectors.toSet()));
            agencySet.addAll(transportTaskDTOs.stream().map(item -> item.getEndAgencyId()).collect(Collectors.toSet()));

            CompletableFuture<Map<Long, Org>> orgMapFeture = PdCompletableFuture.agencyMapFuture(orgApi, null, agencySet, null);
            Map<Long, Org> orgMap = orgMapFeture.get();
            for (TaskTransportDTO taskTranSport : transportTaskDTOs) {
                if (null != taskTranSport.getActualPickUpGoodsTime()) {
                    //实际提货时间 不为空证明已提货
                    builder = RouteDTO.builder();
                    builder.status(OrderStatus.IN_TRANSIT.getCode());
                    builder.time(taskTranSport.getActualPickUpGoodsTime());
                    // 获取发车机构
                    Org org = orgMap.get(Long.valueOf(taskTranSport.getStartAgencyId()));
                    builder.msg("快递在【" + org.getName() + "】已装车，准备发往下一站");
                    result.add(builder.build());
                }
                if (null != taskTranSport.getActualDepartureTime()) {
                    //实际发车时间
                    builder = RouteDTO.builder();
                    builder.status(OrderStatus.IN_TRANSIT.getCode());
                    builder.time(taskTranSport.getActualDepartureTime());
                    builder.msg("快递已发车");
                    result.add(builder.build());
                }

                if (null != taskTranSport.getActualArrivalTime()) {
                    //实际发车时间
                    builder = RouteDTO.builder();
                    builder.status(OrderStatus.IN_TRANSIT.getCode());
                    builder.time(taskTranSport.getActualArrivalTime());
                    // 获取发车机构
                    Org org = orgMap.get(Long.valueOf(taskTranSport.getEndAgencyId()));
                    builder.msg("快递已到达【" + org.getName() + "】");
                    result.add(builder.build());
                }
            }
        }


        /**
         * 构建派送阶段路由
         */
        if (null != taskPickupDispatchPushDTO) {
            // 派送中 有出库时间表示正在派送
            if (taskPickupDispatchPushDTO.getActualStartTime() != null) {
                builder = RouteDTO.builder();
                builder.time(taskPickupDispatchPushDTO.getActualStartTime());
                builder.status(OrderStatus.DISPATCHING.getCode());
                String userId = taskPickupDispatchPushDTO.getCourierId();
                if (StringUtils.isNotEmpty(userId)) {
                    Result<User> userResult = userApi.get(Long.valueOf(userId));
                    User user = userResult.getData();
                    String mobile = user.getMobile();
                    String name = user.getName();
                    builder.msg("快件交给" + name + "，正在派送中（联系电话：" + mobile + "）");
                }
                result.add(builder.build());
            }
            // 已签收 派送完成时间
            if (taskPickupDispatchPushDTO.getActualEndTime() != null && taskPickupDispatchPushDTO.getSignStatus() != null) {
                builder = RouteDTO.builder();
                builder.time(taskPickupDispatchPushDTO.getActualEndTime());
                builder.status(OrderStatus.RECEIVED.getCode());
                List<OrderCargoDto> cargos = cargoFeign.findAll(null, id);
                Integer quantity = 1;
                if (!CollectionUtils.isEmpty(cargos)) {
                    quantity = cargos.get(0).getQuantity();
                }
                builder.quantity(quantity);
                if (taskPickupDispatchPushDTO.getSignStatus().equals(1)) {
                    // 已签收
                    builder.msg(taskPickupDispatchPushDTO.getMark() != null ? taskPickupDispatchPushDTO.getMark() : "已签收! ");
                } else {
                    //拒收
                    builder.msg(taskPickupDispatchPushDTO.getMark() != null ? taskPickupDispatchPushDTO.getMark() : "已拒收! ");
                }
                result.add(builder.build());
            }

        }
        Collections.reverse(result);
        result.forEach(item -> {
            log.info("路由信息：{}", item);
        });
        return RespResult.ok().put("data", result);
    }
}
