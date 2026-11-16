package com.gamsang.note.social;

import java.time.LocalDateTime;

public final class SocialDtos {

    private SocialDtos() {
    }

    public record CommentRequest(String content) {
    }

    // 친구 요청: 상대(receiverId)만 쓴다. 수락: 요청한 사람(requesterId)만 쓴다. 나머지 한쪽은 로그인한 나.
    public record FriendRequest(Long requesterId, Long receiverId) {
    }

    public record LikeResponse(Long reviewId, boolean liked, long likeCount) {
    }

    public record CommentResponse(Long id, Long reviewId, Long userId, String nickname,
                                  String content, LocalDateTime createdAt) {
    }

    public record SocialStats(Long reviewId, long likeCount, long commentCount, boolean likedByMe) {
    }

    public record FriendResponse(Long userId, String nickname) {
    }

    public record FriendshipResponse(Long requesterId, Long receiverId, FriendStatus status) {
        static FriendshipResponse from(Friendship f) {
            return new FriendshipResponse(f.getId().getRequesterId(), f.getId().getReceiverId(), f.getStatus());
        }
    }
}
