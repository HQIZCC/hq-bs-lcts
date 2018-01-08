package com.hqizcc.ltcs.hqbsltcs.mapper;

import com.hqizcc.ltcs.hqbsltcs.model.UserInfo;
import org.springframework.data.repository.CrudRepository;

public interface UserInfoMapper extends CrudRepository<UserInfo, Long>{
    public UserInfo findByUsername(String username);
}
