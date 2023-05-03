package org.iiidev.pinda.authority.api.hystrix;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.iiidev.pinda.authority.api.OrgApi;
import org.iiidev.pinda.authority.dto.core.OrgTreeDTO;
import org.iiidev.pinda.authority.entity.core.Org;
import org.iiidev.pinda.base.Result;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class OrgApiFallback implements OrgApi {
    public OrgApiFallback() {
    }

    public Result<Org> get(Long id) {
        return Result.fail("未获取到组织");
    }

    public Result<List<Org>> list(Integer orgType, List<Long> ids, Long countyId, Long pid, List<Long> pids) {
        return Result.success(new ArrayList());
    }

    public Result<List<OrgTreeDTO>> tree(String name, Boolean status) {
        return Result.success(new ArrayList());
    }

    public Result<Page> pageLike(Integer size, Integer current, String keyword, Long cityId, String latitude, String longitude) {
        return Result.timeout();
    }

    public Result<List<Org>> listByCountyIds(Integer orgType, List<Long> countyIds) {
        return Result.timeout();
    }
}
