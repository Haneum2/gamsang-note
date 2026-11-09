package com.gamsang.note.social;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "friendship")
public class Friendship {

    @EmbeddedId
    private FriendshipId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private FriendStatus status;

    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    protected Friendship() {
    }

    public Friendship(Long requesterId, Long receiverId) {
        this.id = new FriendshipId(requesterId, receiverId);
        this.status = FriendStatus.PENDING;
    }

    public void accept() {
        this.status = FriendStatus.ACCEPTED;
    }

    public FriendshipId getId() { return id; }
    public FriendStatus getStatus() { return status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
}
