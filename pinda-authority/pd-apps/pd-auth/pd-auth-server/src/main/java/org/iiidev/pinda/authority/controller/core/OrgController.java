package org.iiidev.pinda.authority.controller.core;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.iiidev.pinda.authority.biz.service.core.OrgService;
import org.iiidev.pinda.authority.dto.core.OrgSaveDTO;
import org.iiidev.pinda.authority.dto.core.OrgTreeDTO;
import org.iiidev.pinda.authority.dto.core.OrgUpdateDTO;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.base.BaseController;
import org.iiidev.pinda.base.Result;
import org.iiidev.pinda.base.entity.SuperEntity;
import org.iiidev.pinda.database.mybatis.conditions.Wraps;
import org.iiidev.pinda.database.mybatis.conditions.query.LbqWrapper;
import org.iiidev.pinda.dozer.DozerUtils;
import org.iiidev.pinda.log.annotation.SysLog;
import org.iiidev.pinda.utils.BizAssert;
import org.iiidev.pinda.utils.TreeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import static org.iiidev.pinda.utils.StrPool.DEF_PARENT_ID;
import static org.iiidev.pinda.utils.StrPool.DEF_ROOT_PATH;
/**
 * 前端控制器
 * 组织
 */
@Slf4j
@RestController
@RequestMapping("/org")
@Api(value = "Org", tags = "组织")
public class OrgController extends BaseController {
    @Autowired
    private OrgService orgService;
    @Autowired
    private DozerUtils dozer;
    /**
     * 分页查询组织
     */
    @ApiOperation(value = "分页查询组织", notes = "分页查询组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页", dataType = "long", paramType = "query", defaultValue = "1"),
            @ApiImplicitParam(name = "size", value = "每页显示几条", dataType = "long", paramType = "query", defaultValue = "10"),
    })
    @GetMapping("/page")
    @SysLog("分页查询组织")
    public Result<IPage<Org>> page(Org data) {
        IPage<Org> page = this.getPage();
        // 构建值不为null的查询条件
        LbqWrapper<Org> query = Wraps.lbQ(data);
        this.orgService.page(page, query);
        return this.success(page);
    }

    /**
     * 查询组织
     */
    @ApiOperation(value = "查询组织", notes = "查询组织")
    @GetMapping("/{id}")
    @SysLog("查询组织")
    public Result<Org> get(@PathVariable Long id) {
        return this.success(this.orgService.getById(id));
    }

    /**
     * 新增组织
     */
    @ApiOperation(value = "新增组织", notes = "新增组织不为空的字段")
    @PostMapping
    @SysLog("新增组织")
    public Result<Org> save(@RequestBody @Validated OrgSaveDTO data) {


        Org org = this.dozer.map(data, Org.class);
        if (org.getParentId() == null || org.getParentId() <= 0) {
            org.setParentId(DEF_PARENT_ID);
            org.setTreePath(DEF_ROOT_PATH);
        } else {
            Org parent = this.orgService.getById(org.getParentId());
            BizAssert.notNull(parent, "父组织不能为空");

            org.setTreePath(StringUtils.join(parent.getTreePath(), parent.getId(), DEF_ROOT_PATH));
        }
        this.orgService.save(org);
        return this.success(org);
    }

    /**
     * 修改组织
     */
    @ApiOperation(value = "修改组织", notes = "修改组织不为空的字段")
    @PutMapping
    @SysLog("修改组织")
    public Result<Org> update(@RequestBody @Validated(SuperEntity.Update.class) OrgUpdateDTO data) {
        Org org = this.dozer.map(data, Org.class);
        this.orgService.updateById(org);
        return this.success(org);
    }

    /**
     * 删除组织
     */
    @ApiOperation(value = "删除组织", notes = "根据id物理删除组织")
    @SysLog("删除组织")
    @DeleteMapping
    public Result<Boolean> delete(@RequestParam("ids[]") List<Long> ids) {
        this.orgService.remove(ids);
        return this.success(true);
    }

    /**
     * 查询系统所有的组织树
     */
    @ApiOperation(value = "查询系统所有的组织树", notes = "查询系统所有的组织树")
    @GetMapping("/tree")
    @SysLog("查询系统所有的组织树")
    public Result<List<OrgTreeDTO>> tree(@RequestParam(value = "name", required = false) String name,
                                         @RequestParam(value = "status", required = false) Boolean status) {
        List<Org> list = this.orgService.list(Wraps.<Org>lbQ().like(Org::getName, name)
                .eq(Org::getStatus, status).orderByAsc(Org::getSortValue));
        List<OrgTreeDTO> treeList = this.dozer.mapList(list, OrgTreeDTO.class);
        return this.success(TreeUtil.build(treeList));
    }
    //    @GetMapping
//    Result<List<Org>> list(@RequestParam(name = "orgType",required = false) Integer orgType, @RequestParam(name = "ids",required = false) List<Long> ids, @RequestParam(name = "countyId",required = false) Long countyId, @RequestParam(name = "pid",required = false) Long pid, @RequestParam(name = "pids",required = false) List<Long> pids);
    /**
     *  查询所属机构信息列表
     */
    @ApiOperation(value = "查询所属机构信息列表", notes = "查询所属机构信息列表")
    @GetMapping
    @SysLog("查询所属机构信息列表")
    public Result<List<Org>> list(@RequestParam(name = "orgType",required = false) Integer orgType,
                                  @RequestParam(name = "ids",required = false) List<Long> ids,
                                  @RequestParam(name = "countyId",required = false) Long countyId,
                                  @RequestParam(name = "pid",required = false) Long pid,
                                  @RequestParam(name = "pids",required = false) List<Long> pids) {

        List<Org> list=new ArrayList<>();
        for (Long id : ids) {
            Org org = this.orgService.getById(id);
            list.add(org);
        }
        return this.success(list);
    }
}