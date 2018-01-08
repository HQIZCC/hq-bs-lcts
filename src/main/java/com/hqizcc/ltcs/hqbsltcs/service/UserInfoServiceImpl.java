package com.hqizcc.ltcs.hqbsltcs.service;

import com.hqizcc.ltcs.hqbsltcs.mapper.UserInfoMapper;
import com.hqizcc.ltcs.hqbsltcs.model.UserInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo findByUsername(String username) {

        System.out.println("UserInfoServiceImpl.findByUsername()");

        return userInfoMapper.findByUsername(username);
    }

}
