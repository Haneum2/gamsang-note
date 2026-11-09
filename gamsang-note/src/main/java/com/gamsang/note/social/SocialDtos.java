package com.gamsang.note.social;

import java.time.LocalDateTime;

public final class SocialDtos {

    private SocialDtos() {
    }

    public record UserIdRequest(Long userId) {
    }

    public record CommentRequest(Long userId, String content) {
    }

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
