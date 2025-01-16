package com.recordshop.service;

import com.recordshop.constant.Role;
import com.recordshop.detail.PrincipalDetails;
import com.recordshop.entity.*;
import com.recordshop.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {


    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{

        System.out.println("getClientRegistration= " + userRequest.getClientRegistration());
        System.out.println("getAccessToken= " + userRequest.getAccessToken());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println("getAttributes = " + oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")){
            System.out.println("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        } else if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        } else {
            System.out.println("우리는 구글,네이버,카카오 로그인만 지원합니다.");
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_ " + providerId;
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
        Role role = Role.USER;

        Member memberEntity = memberRepository.findByUsername(username);

       if(memberEntity == null) {
            System.out.println(("로그인이 처음입니다."));
            Member member = new Member();
            member.setUsername(username);
            member.setEmail(email);
            member.setPassword(password);
            member.setRole(role);
            member.setProvider(provider);
            member.setProviderId(providerId);
            member.setName(username);
            member.setNickName("webUser");
            memberRepository.save(member);
            return new PrincipalDetails(member,oAuth2User.getAttributes());
        } else {
            System.out.println("이미 가입되어있습니다.");
            return new PrincipalDetails(memberEntity,oAuth2User.getAttributes());
        }
    }
}
