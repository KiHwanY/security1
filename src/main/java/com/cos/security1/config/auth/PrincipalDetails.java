package com.cos.security1.config.auth;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인의 진행이 완료가 되면 시큐리티 session을 만들어준다.(Security ContextHolder)
//오브젝트 = > Authentication 타입 객체
//Authentication 안에 User 정보가 있어야 됨
//User 오브젝트 타입 => UserDetails 타입 객체

//Security Session => Authentication => UserDetails(PrincipalDetails)

import com.cos.security1.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {


    private User user; // 콤포지션

    public PrincipalDetails(User user) {
        this.user=user;
    }
    //해당 User의 권한을 리턴하는 곳!!
    @Override //
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>(); // ArrayList는 Collection의 자식 타입이다.
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole(); // Object 타입을 String 으로 변환해서 return
            }
        });
        return collect;
    }

    @Override // password
    public String getPassword() {
        return user.getPassword();
    }

    @Override // name
    public String getUsername() {
        return user.getUsername();
    }
    // 계정 만료 여부
    @Override // 해당 계정 만료 했는지?
    public boolean isAccountNonExpired() {
        return true;
    }
    // 계정 잠심 여부
    @Override // 해당 계정이 잠겨 있는지?
    public boolean isAccountNonLocked() {
        return true;
    }
    // 계정만료 기간이 지났는지 여부
    @Override // 해당 계정의 비밀번호가 1년이 지났는지?
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화 여부
    @Override // 해당 계정이 활성화 되어있는지?
    public boolean isEnabled() {

        // 우리 사이트!! 1년동안 회원이 로그인을 안하면 !! 휴먼 계정으로 하기로 함.
        // 현재시간 - 로긴시간 => 1년을 초과하면 return false;
        return true;
    }
}
