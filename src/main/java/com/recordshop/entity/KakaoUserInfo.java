package com.recordshop.entity;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.LinkedHashMap;
import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        Object object = attributes.get("kakao_account");
        LinkedHashMap accountMap = (LinkedHashMap) object;
        return (String) accountMap.get("email");
    }

}
