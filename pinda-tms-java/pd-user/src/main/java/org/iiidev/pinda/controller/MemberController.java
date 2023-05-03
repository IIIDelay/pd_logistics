package org.iiidev.pinda.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.iiidev.pinda.common.utils.PageResponse;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.entity.Member;
import org.iiidev.pinda.service.IMemberService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户前端控制器
 */
@Log4j2
@RestController
@RequestMapping("member")
public class MemberController {
    @Autowired
    private IMemberService memberService;

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("")
    public RespResult save(@RequestBody Member entity) {
        boolean result = memberService.save(entity);
        if (result) {
            return RespResult.ok();
        }
        return RespResult.error();
    }
    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public Member detail(@PathVariable(name = "id") String id) {
        Member Member = memberService.getById(id);
        return Member;
    }
    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("page")
    public PageResponse<Member> page(Integer page, Integer pageSize) {
        Page<Member> iPage = new Page(page, pageSize);
        LambdaQueryWrapper<Member> queryWrapper = new LambdaQueryWrapper<>();
        Page<Member> pageResult = memberService.page(iPage, queryWrapper);

        return PageResponse.<Member>builder()
                .items(pageResult.getRecords())
                .page(page)
                .pagesize(pageSize)
                .pages(pageResult.getPages())
                .counts(pageResult.getTotal())
                .build();
    }
    /**
     * 修改
     *
     * @param id
     * @param entity
     * @return
     */
    @PutMapping("/{id}")
    public RespResult update(@PathVariable(name = "id") String id, @RequestBody Member entity) {
        entity.setId(id);
        boolean result = memberService.updateById(entity);
        if (result) {
            return RespResult.ok();
        }
        return RespResult.error();
    }
    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public RespResult del(@PathVariable(name = "id") String id) {
        boolean result = memberService.removeById(id);
        if (result) {
            return RespResult.ok();
        }
        return RespResult.error();
    }

}