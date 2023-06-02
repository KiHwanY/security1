package com.cos.security1.config.auth.provider;

public interface OAuth2Userinfo {

    String getProviderId(); // google,naver, facebook
    String getGetProvider();// google, naver, facebook
    String getEmail();
    String getName();

}
