package com.cos.security1.config.auth;

//시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인의 진행이 완료가 되면 시큐리티 session을 만들어준다.(Security ContextHolder)
//오브젝트 = > Authentication 타입 객체
//Authentication 안에 User 정보가 있어야 됨
//User 오브젝트 타입 => UserDetails 타입 객체

//Security Session => Authentication => UserDetails(PrincipalDetails)

import com.cos.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
//PrincipalDetails를 생성한 이유?
// 시큐리티 세션 정보에는 하나만 들어갈 수 있다. Authentication 객체이다.
// Authentication에는 두개의 필드가 있다.
// OAuth2User 타입, UserDetails 타입이다.

//만약 회원가입을 한다면? 회원가입 -> user 오브젝트가 필요하다.
//하지만 OAuth2User,UserDetails 타입은 user 오브젝트를 포함하고 있지 않다.
//세션에 저장되는데, 세션을 가지고 와도 user 오브젝트를 찾을 수 없다.
// 그래서 PrincipalDetails 라는 클래스를 만들고
// UserDetails Impl 해서 같은 타입으로 묶어서
// 여기에다 user 오브젝트를 품어논다.
// 그러면 userDetails 를 대신해서 PrincipalDetails를 사용하므로
// user 오브젝트에 접근할 수 있다.
// 두개의 타입을 별도의 클래스를 사용해서 만들면 복잡해 지기 때문에
// 부모 클래스에 같이 묶어서 꺼내쓴다.
// 그렇다면 꺼내 쓸때 PrincipalDetails 타입만 꺼내 쓰면 된다.
// 그 안에는 user 오브젝트가 있다.

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {


    private User user; // 콤포지션
    private Map<String, Object> attributes;

    // 일반 로그인 생성자
    public PrincipalDetails(User user) {
        this.user=user;
    }
    //OAuth 로그인 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user=user;
        this.attributes=attributes;
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
    // OAuth2User 등록하면 getAttributes,getName 메서드가 오버라이드 된다.
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override // 사용안함
    public String getName() {
        return null;

    }
}
