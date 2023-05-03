package org.iiidev.pinda.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.iiidev.pinda.entity.AddressBook;
import org.iiidev.pinda.mapper.AddressBookMapper;
import org.iiidev.pinda.service.IAddressBookService;
import org.springframework.stereotype.Service;

/**
 * 地址簿服务类实现
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {

}