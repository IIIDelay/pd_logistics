package org.iiidev.pinda.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.iiidev.j2cache.annotation.Cache;
import org.iiidev.j2cache.annotation.CacheEvictor;
import org.iiidev.pinda.common.utils.PageResponse;
import org.iiidev.pinda.common.utils.RespResult;
import org.iiidev.pinda.entity.AddressBook;
import org.iiidev.pinda.service.IAddressBookService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import net.oschina.j2cache.CacheChannel;

/**
 * 地址簿
 */
@Log4j2
@RestController
@RequestMapping("addressBook")
public class AddressBookController {
    @Autowired
    private IAddressBookService addressBookService;

    @Autowired
    private CacheChannel cacheChannel;

    private String region = "addressBook";

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping("")
    public RespResult save(@RequestBody AddressBook entity) {
        if (1 == entity.getIsDefault()) {
            addressBookService.lambdaUpdate().set(AddressBook::getIsDefault, 0).eq(AddressBook::getUserId, entity.getUserId()).update();
        }

        boolean result = addressBookService.save(entity);
        if (result) {
            //载入缓存
            cacheChannel.set(region,entity.getId(),entity);
            return RespResult.ok();
        }
        return RespResult.error();
    }
    /**
     * 查询地址簿详情
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    @Cache(region = "addressBook",key = "ab",params = "id")
    public AddressBook detail(@PathVariable(name = "id") String id) {
        AddressBook addressBook = addressBookService.getById(id);
        return addressBook;
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param userId
     * @return
     */
    @GetMapping("page")
    public PageResponse<AddressBook> page(Integer page, Integer pageSize, String userId, String keyword) {
        Page<AddressBook> iPage = new Page(page, pageSize);
        Page<AddressBook> pageResult = addressBookService.lambdaQuery()
                .eq(StringUtils.isNotEmpty(userId), AddressBook::getUserId, userId)
                .and(StringUtils.isNotEmpty(keyword), wrapper ->
                        wrapper.like(AddressBook::getName, keyword).or()
                                .like(AddressBook::getPhoneNumber, keyword).or()
                                .like(AddressBook::getCompanyName, keyword))
                .page(iPage);

        return PageResponse.<AddressBook>builder()
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
    @CacheEvictor({@Cache(region = "addressBook",key = "ab",params = "1.id")})
    public RespResult update(@PathVariable(name = "id") String id, @RequestBody AddressBook entity) {
        entity.setId(id);
        if (1 == entity.getIsDefault()) {
            addressBookService.lambdaUpdate().set(AddressBook::getIsDefault, 0).eq(AddressBook::getUserId, entity.getUserId()).update();
        }
        boolean result = addressBookService.updateById(entity);
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
    @CacheEvictor({@Cache(region = "addressBook",key = "ab",params = "id")})
    public RespResult del(@PathVariable(name = "id") String id) {
        boolean result = addressBookService.removeById(id);
        if (result) {
            return RespResult.ok();
        }
        return RespResult.error();
    }
}