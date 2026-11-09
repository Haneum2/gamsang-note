package com.gamsang.note.social;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, FriendshipId> {

    // a→b 또는 b→a 요청이 이미 있는지 (방향 상관없이)
    @Query("""
            select f from Friendship f
            where (f.id.requesterId = :a and f.id.receiverId = :b)
               or (f.id.requesterId = :b and f.id.receiverId = :a)
            """)
    List<Friendship> findBetween(@Param("a") Long a, @Param("b") Long b);

    // 내가 요청했거나 받은 '수락된' 친구 관계
    @Query("""
            select f from Friendship f
            where f.status = com.gamsang.note.social.FriendStatus.ACCEPTED
              and (f.id.requesterId = :userId or f.id.receiverId = :userId)
            """)
    List<Friendship> findAccepted(@Param("userId") Long userId);

    // 내가 받은 '대기 중' 요청
    @Query("""
            select f from Friendship f
            where f.status = com.gamsang.note.social.FriendStatus.PENDING
              and f.id.receiverId = :userId
            order by f.requestedAt desc
            """)
    List<Friendship> findPendingReceived(@Param("userId") Long userId);
}
