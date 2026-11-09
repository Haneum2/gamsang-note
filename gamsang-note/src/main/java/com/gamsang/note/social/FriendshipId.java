package com.gamsang.note.social;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** friendship 의 기본키는 (requester_id, receiver_id) 두 컬럼이다. 복합키는 @Embeddable 클래스로 묶는다. */
@Embeddable
public class FriendshipId implements Serializable {

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "receiver_id")
    private Long receiverId;

    protected FriendshipId() {
    }

    public FriendshipId(Long requesterId, Long receiverId) {
        this.requesterId = requesterId;
        this.receiverId = receiverId;
    }

    public Long getRequesterId() { return requesterId; }
    public Long getReceiverId() { return receiverId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendshipId that)) return false;
        return Objects.equals(requesterId, that.requesterId) && Objects.equals(receiverId, that.receiverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requesterId, receiverId);
    }
}
