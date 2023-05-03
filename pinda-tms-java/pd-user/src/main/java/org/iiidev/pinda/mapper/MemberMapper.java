package org.iiidev.pinda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.iiidev.pinda.entity.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface MemberMapper extends BaseMapper<Member> {
}