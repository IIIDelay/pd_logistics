package org.iiidev.pinda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.iiidev.pinda.DTO.angency.AgencyScopeDto;
import org.iiidev.pinda.authority.api.AreaApi;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.api.RoleApi;
import org.iiidev.pinda.authority.api.UserApi;
import org.iiidev.pinda.authority.dto.core.OrgTreeDTO;
import org.iiidev.pinda.authority.entity.auth.User;
import org.iiidev.pinda.authority.entity.common.Area;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.authority.enumeration.core.OrgEnum;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.common.utils.EntCoordSyncJob;
import org.iiidev.pinda.common.utils.PageResponse;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.feign.agency.AgencyScopeFeign;
import org.iiidev.pinda.util.BeanUtil;
import org.iiidev.pinda.vo.base.AreaSimpleVo;
import org.iiidev.pinda.vo.base.angency.AgencyScopeVo;
import org.iiidev.pinda.vo.base.angency.AgencySimpleVo;
import org.iiidev.pinda.vo.base.angency.AgencyVo;
import org.iiidev.pinda.vo.base.userCenter.SysUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("agency")
@Api(tags = "组织管理")
@RequiredArgsConstructor
@Slf4j
public class AgencyController {
    @Autowired
    private OrgApi orgApi;
    @Autowired
    private AreaApi areaApi;
    @Autowired
    private UserApi userApi;
    @Autowired
    private RoleApi roleApi;
    @Autowired
    private AgencyScopeFeign agencyScopeFeign;

    @ApiOperation(value = "获取树状机构信息")
    @GetMapping("/tree")
    public List<AgencySimpleVo> treeAgency() {
        List<AgencySimpleVo> resultList = new ArrayList<>();

        Result<List<OrgTreeDTO>> result = orgApi.tree(null, true);
        if (result.isSuccess() && result.getData() != null && result.getData().size() > 0) {
            resultList.addAll(result.getData().stream().map(orgTreeDTO -> {
                AgencySimpleVo simpleVo = BeanUtil.parseOrg2SimpleVo(orgTreeDTO);
                simpleVo.setSubAgencies(getNode(orgTreeDTO.getChildren()));
                return simpleVo;
            }).collect(Collectors.toList()));
        }
        return resultList;
    }

    @ApiOperation(value = "获取机构详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "机构id", required = true, example = "1", paramType = "{path}")
    })
    @GetMapping("/{id}")
    public AgencyVo findAgencyById(@PathVariable(name = "id") String id) {
        Result<Org> result = orgApi.get(Long.valueOf(id));
        if (result.isSuccess()) {
            Org org = result.getData();
            if (org != null) {
                AgencyVo vo = BeanUtil.parseOrg2Vo(org, orgApi, areaApi);
                return vo;
            }
        }
        return null;
    }

    @ApiOperation(value = "获取员工详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "员工id", required = true, example = "1", paramType = "{path}")
    })
    @GetMapping("/user/{id}")
    public SysUserVo findById(@PathVariable(name = "id") String id) {
        Result<User> result = userApi.get(Long.valueOf(id));
        SysUserVo vo = null;
        if (result.isSuccess() && result.getData() != null) {
            vo = BeanUtil.parseUser2Vo(result.getData(), roleApi, orgApi);
        }
        return vo;
    }

    @ApiOperation(value = "获取员工分页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页尺寸", required = true, example = "10"),
            @ApiImplicitParam(name = "agencyId", value = "机构id")
    })
    @GetMapping("/user/page")
    public PageResponse<SysUserVo> findUserByPage(@RequestParam(name = "page") Integer page,
                                                  @RequestParam(name = "pageSize") Integer pageSize,
                                                  @RequestParam(name = "agencyId", required = false) String agencyId) {
        Result<Page<User>> result = userApi.page(page.longValue(), pageSize.longValue(), StringUtils.isNotEmpty(agencyId) ? Long.valueOf(agencyId) : null, null, null, null, null);
        if (result.isSuccess() && result.getData() != null) {
            IPage<User> userPage = result.getData();
            //处理对象转换
            List<SysUserVo> voList = userPage.getRecords().stream().map(user -> BeanUtil.parseUser2Vo(user, roleApi, orgApi)).collect(Collectors.toList());
            return PageResponse.<SysUserVo>builder().items(voList).page(page).pagesize(pageSize).counts(userPage.getTotal()).pages(userPage.getPages()).build();
        }
        return PageResponse.<SysUserVo>builder().items(new ArrayList<>()).page(page).pagesize(pageSize).counts(0L).pages(0L).build();
    }

    @ApiOperation(value = "保存机构业务范围")
    @PostMapping("/scope")
    public RespResult saveScope(@RequestBody AgencyScopeVo vo) {
        //验证和处理范围和区域信息
        RespResult respResult = validateParam(vo);
        if (!"0".equals(respResult.get("code").toString())) {
            return respResult;
        }
        //保存前先清理一遍
        AgencyScopeDto deleteDto = new AgencyScopeDto();
        deleteDto.setAgencyId(vo.getAgency().getId());
        agencyScopeFeign.deleteAgencyScope(deleteDto);

        //保存数据
        List<AgencyScopeDto> saveList = vo.getAreas().stream().map(areaVo -> {
            AgencyScopeDto dto = new AgencyScopeDto();
            dto.setAreaId(areaVo.getId());
            dto.setAgencyId(vo.getAgency().getId());
            dto.setMutiPoints(areaVo.getMutiPoints());
            return dto;
        }).collect(Collectors.toList());
        agencyScopeFeign.batchSaveAgencyScope(saveList);
        return RespResult.ok();
    }

    /**
     * 验证范围参数设置区域id
     *
     * @param vo
     * @return
     */
    private RespResult validateParam(AgencyScopeVo vo) {
        List<AreaSimpleVo> areas = vo.getAreas();
        if (areas == null || areas.size() == 0) {
            return RespResult.error(5000, "范围信息为空");
        } else {
            for (AreaSimpleVo areaSimpleVo : areas) {
                String adcodeOld = "";
                Area area = new Area();
                //一个区域的多个范围
                List<List<Map>> list = areaSimpleVo.getMutiPoints();
                if (list == null || list.size() == 0) {
                    return RespResult.error(5000, "范围信息为空");
                } else {
                    for (List<Map> listMap : list) {
                        for(int i=0;i<listMap.size();i++){
                            Map pointMap = listMap.get(i);
                            String point = getPoint(pointMap);
                            Map map = EntCoordSyncJob.getLocationByPosition(point);
                            String adcode = map.getOrDefault("adcode", "").toString();
                            if (StringUtils.isBlank(adcode)) {
                                return RespResult.error(5000, "根据地图获取区划编码为空");
                            } else {
                                if (!StringUtils.equals(adcode, adcodeOld) && i>0) {
                                    return RespResult.error(5000, "一个机构作业范围必须在一个区域内");
                                }
                                Result<Area> result = areaApi.getByCode(adcode + "000000");
                                if (result.isSuccess() && result.getData() != null) {
                                    area = result.getData();
                                }
                            }
                            adcodeOld = adcode;
                        }

                    }
                }
                areaSimpleVo.setId(area.getId() + "");
                areaSimpleVo.setName(area.getName());
            }

        }
        return RespResult.ok();
    }

    private String getPoint(Map pointMap) {
        String lng = pointMap.getOrDefault("lng","").toString();
        String lat = pointMap.getOrDefault("lat","").toString();
        return lng+","+lat;
    }

    @ApiOperation(value = "获取机构业务范围")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "机构id", required = true, example = "1", paramType = "{path}")
    })
    @GetMapping("/{id}/scope")
    public AgencyScopeVo findAllAgencyScope(@PathVariable(name = "id") String id) {
        Result<Org> result = orgApi.get(Long.valueOf(id));
        if (result.isSuccess()) {
            Org org = result.getData();
            List<AgencyScopeDto> agencyScopeDtoList = null;
            if (org != null && org.getOrgType() != null) {
                if (org.getOrgType() == OrgEnum.BUSINESS_HALL.getType()) {
                    //当前机构为网点
                    agencyScopeDtoList = getAgencyScopes(id, null);
                } else if (org.getOrgType() == OrgEnum.SECONDARY_TRANSFER_CENTER.getType()) {
                    //当前机构为二级转运中心¬
                    List<String> agencyIds = getOrgIds(Long.valueOf(id), null).stream().map(item -> String.valueOf(item)).collect(Collectors.toList());
                    agencyScopeDtoList = getAgencyScopes(null, agencyIds);
                } else if (org.getOrgType() == OrgEnum.TOP_TRANSFER_CENTER.getType()) {
                    //当前机构为一级转运中心
                    List<Long> parentIds = getOrgIds(Long.valueOf(id), null);
                    if (parentIds.size() > 0) {
                        List<String> agencyIds = getOrgIds(null, parentIds).stream().map(item -> String.valueOf(item)).collect(Collectors.toList());
                        agencyScopeDtoList = getAgencyScopes(null, agencyIds);
                    }
                } else if (org.getOrgType() == OrgEnum.BRANCH_OFFICE.getType()) {
                    //当前机构为分公司
                    List<Long> firstLevels = getOrgIds(Long.valueOf(id), null);
                    if (firstLevels.size() > 0) {
                        List<Long> secondLevels = getOrgIds(null, firstLevels);
                        if (secondLevels.size() > 0) {
                            List<String> agencyIds = getOrgIds(null, secondLevels).stream().map(item -> String.valueOf(item)).collect(Collectors.toList());
                            agencyScopeDtoList = getAgencyScopes(null, agencyIds);
                        }
                    }
                }
            }
            //处理返回信息
            AgencyScopeVo vo = new AgencyScopeVo();
            AgencyVo agencyVo = org == null ? null : BeanUtil.parseOrg2Vo(org, orgApi, areaApi);
            vo.setAgency(agencyVo);
            List<AreaSimpleVo> areas = new ArrayList<>();
            if (agencyScopeDtoList != null) {
                List<Long> areaIds = agencyScopeDtoList.stream().map(dto -> Long.valueOf(dto.getAreaId())).collect(Collectors.toList());
                if (areaIds.size() > 0) {
                    Result<List<Area>> areaResult = areaApi.findAll(null, areaIds);
                    if (areaResult.isSuccess() && areaResult.getData() != null) {
                        areas.addAll(areaResult.getData().stream().map(BeanUtil::parseArea2Vo).collect(Collectors.toList()));
                    }
                }
            }
            vo.setAreas(addMutiPoints(areas, agencyScopeDtoList));
            return vo;
        }
        return null;
    }

    /**
     * 返回结果中添加区域内的作业范围
     * @param areas
     * @param agencyScopeDtoList
     * @return
     */
    private List<AreaSimpleVo> addMutiPoints(List<AreaSimpleVo> areas, List<AgencyScopeDto> agencyScopeDtoList) {
        for (AreaSimpleVo areaSimpleVo : areas) {
            for (AgencyScopeDto agencyScopeDto : agencyScopeDtoList) {
                if (agencyScopeDto.getAreaId().equals(areaSimpleVo.getId())){
                    areaSimpleVo.setMutiPoints(agencyScopeDto.getMutiPoints());
                }
            }
        }
        return areas;
    }

    /**
     * 获取子级组织id列表
     *
     * @param id  组织id
     * @param ids 组织id列表
     * @return 子级组织id列表
     */
    private List<Long> getOrgIds(Long id, List<Long> ids) {
        Result<List<Org>> listResult = orgApi.list(null, null, null, id, ids);
        if (listResult.isSuccess() && listResult.getData() != null && listResult.getData().size() > 0) {
            return listResult.getData().stream().map(Org::getId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<AgencyScopeDto> getAgencyScopes(String id, List<String> ids) {
        boolean idOk = StringUtils.isNotEmpty(id) && !id.equals("0");
        boolean idsOk = ids != null && ids.size() > 0;
        if (idOk || idsOk) {
            return agencyScopeFeign.findAllAgencyScope(null, id, ids, null);
        }
        return new ArrayList<>();
    }

    /**
     * 递归获取组织树
     *
     * @param dtoList
     * @return
     */
    private List<AgencySimpleVo> getNode(List<OrgTreeDTO> dtoList) {
        List<AgencySimpleVo> list = new ArrayList<>();
        if (dtoList != null && dtoList.size() > 0) {
            for (int i = 0; i < dtoList.size(); i++) {
                AgencySimpleVo vo = BeanUtil.parseOrg2SimpleVo(dtoList.get(i));
                vo.setSubAgencies(getNode(dtoList.get(i).getChildren()));
                list.add(vo);
            }
        }
        return list;
    }
}
