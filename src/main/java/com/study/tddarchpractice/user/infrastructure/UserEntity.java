package com.study.tddarchpractice.user.infrastructure;

import com.study.tddarchpractice.user.domain.User;
import com.study.tddarchpractice.user.domain.UserStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "address")
    private String address;

    @Column(name = "certification_code")
    private String certificationCode;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "last_login_at")
    private Long lastLoginAt;

    @Builder
    public UserEntity(String email, String nickname, String address, String certificationCode, UserStatus status, Long lastLoginAt) {
        this.email = email;
        this.nickname = nickname;
        this.address = address;
        this.certificationCode = certificationCode;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
    }

    public static UserEntity fromModel(User user) {
        return UserEntity.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .address(user.getAddress())
                .certificationCode(user.getCertificationCode())
                .status(user.getStatus())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    public User toModel() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .nickname(this.nickname)
                .address(this.address)
                .certificationCode(this.certificationCode)
                .status(this.status)
                .lastLoginAt(this.lastLoginAt)
                .build();
    }
}