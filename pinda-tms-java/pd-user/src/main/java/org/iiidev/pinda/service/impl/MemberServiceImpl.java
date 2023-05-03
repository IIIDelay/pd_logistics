package org.iiidev.pinda.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.iiidev.pinda.entity.Member;
import org.iiidev.pinda.mapper.MemberMapper;
import org.iiidev.pinda.service.IMemberService;
import org.springframework.stereotype.Service;

/**
 * 用户服务类实现
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements IMemberService {

}