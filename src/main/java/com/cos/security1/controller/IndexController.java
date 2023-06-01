package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // View를 리턴하겠다!!
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //스프링 시큐리티는 자기만의 시큐리티 세션을 갖고 있다. DI를 주입하려면 Authentication 객체가 꼭 필요하다.

    //Authentication 안에 들어갈 수 있는 타입은 딱 두개다.
    // UserDetails -> 일반 로그인
    // OAuth2User -> facebook, sns 등 OAuth 타입으로 로그인
    // 그럼 컨트롤러에 사용하려면 어떻게 해야할까?
    // 부모 클래스를 만들어서 상속 시켜주면 된다.

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
    @AuthenticationPrincipal PrincipalDetails userDetails){ // DI(의존성 주입)
        // @AuthenticationPrincipal=> 세션 정보에 접근할 수 있게 해준다.
        // PrincipalDetails로도 세션 정보에 접근할 수 있다. 왜냐면 UserDetails를 implements 했기 때문에,
        // 그렇다면 getUsername()으로 호출하는게 아니라 getUser()로 호출해줘야 한다.
        System.out.println("/test/login ======================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal() ;
        System.out.println("authentication = " + principalDetails.getUser()); // authentication.getPrincipal() 리턴 타입 => Object

        System.out.println("userDetails = " + userDetails.getUser());
        return "세션 정보 확인하기";

    }
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication , @AuthenticationPrincipal OAuth2User oAuth){ // DI(의존성 주입)

        System.out.println("/test/oauth/login ======================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal() ;
        System.out.println("authentication = " + oAuth2User.getAttributes()); // authentication.getPrincipal() 리턴 타입 => Object

        System.out.println("oAuth2User = " + oAuth.getAttributes());


        return "OAuth 세션 정보 확인하기";

    }

    //localhost:8080/
    //localhost:8080
    @GetMapping({"","/"})
    public String index(){

        //머스테치 기본폴더 src/main/resources/
        //뷰리졸버 설정 : templates (prefix), .mustache(suffix) 생략가능!!
        return "index";
    }
    //OAuth 로그인을 해도 PrincipalDetails
    //일반 로그인을 해도 PrincipalDetails
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails = " + principalDetails.getUser());
        return "user";
    }
    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "admin";
    }
    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }
    // 스프링 시큐리티 해당주소를 낚아채버리네요!! - SecurityConfig 파일 생성 후 작동 안함.
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }
    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user){
        System.out.println("user = " + user);
        user.setRole("ROLE_USER");
        // 이유는 패스워드가 암호화가 안되었기 때문에
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user); // 회원가입 잘됨. 비밀번호 : 1234 => 시큐리티로 로그인을 할 수 없다.
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN") //하나만 걸고 싶다면 Secured 만 하면 된다.
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") //여러개 걸고 싶다면?
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터정보";
    }



}
