package com.recordshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class MemberModifyFormDto {

    @NotBlank(message = "닉네임을 필수로 설정해주세요.")
    private String nickName;

    @NotEmpty(message = "비밀번호를 필수 입력 값입니다.")
    @Length(min = 4, max=20, message = "비밀번호는 4자 이상, 20자 이하로 입력해주세요")
    private String password;

    @NotEmpty(message = "핸드폰 번호는 필수 입력 값입니다.")
    private String phoneNumber;

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;

    @Override
    public String toString() {
        return "MemberModifyFormDto{nickName='" + nickName + "', address='" + address + "', phone='" + phoneNumber + "'}";
    }

}
