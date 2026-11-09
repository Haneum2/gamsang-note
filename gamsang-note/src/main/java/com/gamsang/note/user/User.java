package com.gamsang.note.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** users 테이블. (USER 는 Oracle 예약어라 테이블 이름이 users 이다) */
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "nickname", nullable = false, length = 30)
    private String nickname;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected User() {
    }

    public Long getId() { return id; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
