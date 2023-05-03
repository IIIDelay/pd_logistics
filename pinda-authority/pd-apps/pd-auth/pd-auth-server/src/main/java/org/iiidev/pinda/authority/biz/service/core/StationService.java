package org.iiidev.pinda.authority.biz.service.core;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.iiidev.pinda.authority.dto.core.StationPageDTO;
import org.iiidev.pinda.authority.entity.core.Station;
/**
 * 业务接口
 * 岗位
 */
public interface StationService extends IService<Station> {
    /**
     * 分页
     */
    IPage<Station> findStationPage(Page page, StationPageDTO data);
}
