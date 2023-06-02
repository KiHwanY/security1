package com.cos.security1.config.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.auth.provider.FacebookUserInfo;
import com.cos.security1.config.auth.provider.GoogleUserInfo;
import com.cos.security1.config.auth.provider.NaverUserInfo;
import com.cos.security1.config.auth.provider.OAuth2Userinfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    //구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수이다.
    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override // 후처리 기능
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //getClientRegistration -> registrationId로 어떤 OAuth로 로그인 했는지 확인 가능.
        System.out.println("getClientRegistration = " + userRequest.getClientRegistration()); // 서버의 기본 정보를 출력한다.
        System.out.println("getAccessToken = " + userRequest.getAccessToken().getTokenValue()); // 패스워드 토큰 값
        // 구글로그인 버튼 클릭 -> 구글로그인 창-> 로그인을 완료-> code를 리턴(OAuth-Client 라이브러리)-> AccessToken 요청
        // 위 까지가 userRequest 정보이다.

        OAuth2User oAuth2User = super.loadUser(userRequest);
        // userRequest 정보 -> loadUser 함수 호출->구글로부터 회원프로필 받아준다.
        System.out.println("getAttributes = " + oAuth2User.getAttributes()); // 회원 정보 출력

        // 회원 가입을 강제로 진행해볼 예정
        OAuth2Userinfo oAuth2Userinfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2Userinfo = new GoogleUserInfo(oAuth2User.getAttributes()); // GoogleUserInfo에 요청

        }else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");
            oAuth2Userinfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2Userinfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }else {
            System.out.println("우리는 구글과 페이스북과 네이버만 지원해요.");
        }


        String provider = oAuth2Userinfo.getGetProvider(); // google
        String providerId = oAuth2Userinfo.getProviderId(); // null값
//        String providerId = oAuth2User.getAttribute("id"); // null값 facebook name
        String username = provider + "_" + providerId; //google_113058107580252297024
        String password = bCryptPasswordEncoder.encode("겟인데어"); // 암호화 설정
        String email = oAuth2Userinfo.getEmail();
        String role = "ROLE_USER";

        //해당 아이디가 있는지 확인
        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null){ // 해당 아이디를 못찾았다면
            System.out.println("OAuth 로그인이 최초입니다.");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else {
            System.out.println("로그인을 이미 한적이 있습니다. 당신은 자동 회원가입이 되어 있습니다.");
        }


        // {sub=113058107580252297024,

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
        // 생성되면 Authentication 객체 안에 들어간다.

    }
}


//getAttributes =
// {sub=113058107580252297024,
// name=윤기환,
// given_name=기환,
// family_name=윤,
// picture=https://lh3.googleusercontent.com/a/AAcHTteKMDtYnLEucfasGgmirkB-_t6T6i0XALMykuyj=s96-c,
// email=drg2524@gmail.com,
// email_verified=true,
// locale=ko}

// 구글 회원가입 맵핑 (getAttributes 출력 정보를 토대로)

// username = google_113058107580252297024
// password = "암호화(겟인데어)"
// email = "drg2524@gmail.com"
// role = "ROLE_USER"
// provider = "google"
// providerId = 113058107580252297024
