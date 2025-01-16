
package com.recordshop.entity;

import com.recordshop.constant.Role;
import com.recordshop.dto.MemberFormDto;
import com.recordshop.dto.MemberModifyFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Table(name="member")
@ToString
@Getter
@Setter
public class Member extends BaseEntity {

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String name;

    private String nickName;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;


    private String providerId;
    private String provider;


    /*public Member() {

    }*/

  public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setUsername(memberFormDto.getUsername());
        member.setName(memberFormDto.getName());
        member.setNickName(memberFormDto.getNickName());
        member.setEmail(memberFormDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberFormDto.getPassword()));
        member.setPhoneNumber(memberFormDto.getPhoneNumber());
        member.setAddress(memberFormDto.getAddress());
        member.setRole(Role.USER);
        return member;
    }




    //회원정보 수정
    public void modifyMember(MemberModifyFormDto memberModifyFormDto, PasswordEncoder passwordEncoder) {
        if (memberModifyFormDto.getNickName() != null && !memberModifyFormDto.getNickName().isEmpty()) {
            this.nickName = memberModifyFormDto.getNickName();
        }
        if (memberModifyFormDto.getPassword() != null && !memberModifyFormDto.getPassword().isEmpty()) {
            this.password = passwordEncoder.encode(memberModifyFormDto.getPassword());
        }
        if (memberModifyFormDto.getPhoneNumber() != null && !memberModifyFormDto.getPhoneNumber().isEmpty()) {
            this.phoneNumber = memberModifyFormDto.getPhoneNumber();
        }
        if (memberModifyFormDto.getAddress() != null && !memberModifyFormDto.getAddress().isEmpty()) {
            this.address = memberModifyFormDto.getAddress();
        }
        System.out.println("Modified Member: " + this);
    }


}
