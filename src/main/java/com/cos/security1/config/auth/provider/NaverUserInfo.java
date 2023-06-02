package com.cos.security1.config.auth.provider;

import java.util.Map;

public class NaverUserInfo implements OAuth2Userinfo{

    private Map<String, Object> attributes; // => oAuth2User.getAttributes()

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes =attributes;
    }

    @Override
    public String getProviderId() {
        return (String)attributes.get("id");
    }

    @Override
    public String getGetProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String)attributes.get("email");
    }

    @Override
    public String getName() {
        return (String)attributes.get("name");
    }
}
