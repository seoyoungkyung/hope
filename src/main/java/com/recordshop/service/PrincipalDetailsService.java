package com.recordshop.service;

import com.recordshop.detail.PrincipalDetails;
import com.recordshop.entity.Member;
import com.recordshop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        System.out.println("username: " + username);
        Member memberEntity = memberRepository.findByUsername(username);

        if(memberEntity != null){
            return new PrincipalDetails(memberEntity);
        }
        return null;
    }

}
