package com.gamsang.note.social;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.gamsang.note.social.SocialDtos.CommentResponse;
import com.gamsang.note.social.SocialDtos.FriendResponse;
import com.gamsang.note.social.SocialDtos.FriendshipResponse;
import com.gamsang.note.social.SocialDtos.LikeResponse;
import com.gamsang.note.social.SocialDtos.SocialStats;
import com.gamsang.note.user.User;
import com.gamsang.note.user.UserRepository;
import com.gamsang.note.viewing.VisibleReviewId;
import com.gamsang.note.viewing.VisibleReviewRepository;

@Service
@Transactional
public class SocialService {

    private final ReviewLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final FriendshipRepository friendshipRepository;
    private final VisibleReviewRepository visibleReviewRepository;
    private final UserRepository userRepository;

    public SocialService(ReviewLikeRepository likeRepository, CommentRepository commentRepository,
                         FriendshipRepository friendshipRepository,
                         VisibleReviewRepository visibleReviewRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.friendshipRepository = friendshipRepository;
        this.visibleReviewRepository = visibleReviewRepository;
        this.userRepository = userRepository;
    }

    // ================= 좋아요 · 댓글 =================

    public LikeResponse like(Long reviewId, Long userId) {
        requireVisible(userId, reviewId);
        var id = new ReviewLikeId(reviewId, userId);
        if (!likeRepository.existsById(id)) {   // 이미 눌렀다면 그대로 둔다(여러 번 눌러도 결과 같음)
            likeRepository.save(new ReviewLike(reviewId, userId));
        }
        return new LikeResponse(reviewId, true, likeRepository.countByReview(reviewId));
    }

    public LikeResponse unlike(Long reviewId, Long userId) {
        requireVisible(userId, reviewId);
        var id = new ReviewLikeId(reviewId, userId);
        if (likeRepository.existsById(id)) {
            likeRepository.deleteById(id);
            likeRepository.flush();   // 삭제를 먼저 DB 에 반영한 뒤 개수를 센다
        }
        return new LikeResponse(reviewId, false, likeRepository.countByReview(reviewId));
    }

    public CommentResponse addComment(Long reviewId, Long userId, String content) {
        if (content == null || content.isBlank()) {
            throw bad("댓글 내용을 입력해 주세요.");
        }
        requireVisible(userId, reviewId);
        Comment saved = commentRepository.save(new Comment(reviewId, userId, content.trim()));
        User author = userRepository.findById(userId).orElseThrow();
        return new CommentResponse(saved.getId(), reviewId, userId, author.getNickname(),
                saved.getContent(), saved.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(Long reviewId, Long viewerId) {
        requireVisible(viewerId, reviewId);
        List<Comment> comments = commentRepository.findByReviewIdOrderByIdAsc(reviewId);
        Map<Long, String> nicknames = nicknames(comments.stream().map(Comment::getUserId).collect(Collectors.toSet()));
        return comments.stream()
                .map(c -> new CommentResponse(c.getId(), c.getReviewId(), c.getUserId(),
                        nicknames.get(c.getUserId()), c.getContent(), c.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public SocialStats stats(Long reviewId, Long viewerId) {
        requireVisible(viewerId, reviewId);
        return new SocialStats(reviewId, likeRepository.countByReview(reviewId),
                commentRepository.countByReviewId(reviewId),
                likeRepository.existsById(new ReviewLikeId(reviewId, viewerId)));
    }

    // ================= 친구 =================

    public FriendshipResponse requestFriend(Long requesterId, Long receiverId) {
        if (requesterId == null || receiverId == null) {
            throw bad("requesterId 와 receiverId 는 필수입니다.");
        }
        if (requesterId.equals(receiverId)) {
            throw bad("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        }
        if (!userRepository.existsById(requesterId) || !userRepository.existsById(receiverId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.");
        }
        // 반대 방향 요청이 있어도 막는다. (DB 의 uq_friendship_pair 유일 인덱스가 최종 방어선)
        if (!friendshipRepository.findBetween(requesterId, receiverId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 친구이거나 요청이 진행 중입니다.");
        }
        return FriendshipResponse.from(friendshipRepository.save(new Friendship(requesterId, receiverId)));
    }

    public FriendshipResponse acceptFriend(Long requesterId, Long receiverId) {
        Friendship f = friendshipRepository.findById(new FriendshipId(requesterId, receiverId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."));
        f.accept();   // 변경 감지로 UPDATE 가 실행된다
        return FriendshipResponse.from(f);
    }

    public void rejectFriend(Long requesterId, Long receiverId) {
        Friendship f = friendshipRepository.findById(new FriendshipId(requesterId, receiverId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."));
        if (f.getStatus() != FriendStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "대기 중인 요청만 거절·취소할 수 있습니다.");
        }
        friendshipRepository.delete(f);
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> listFriends(Long userId) {
        List<Friendship> accepted = friendshipRepository.findAccepted(userId);
        Set<Long> friendIds = new HashSet<>();
        for (Friendship f : accepted) {   // 요청한 쪽/받은 쪽 어느 쪽이든 '상대'를 골라낸다
            friendIds.add(f.getId().getRequesterId().equals(userId)
                    ? f.getId().getReceiverId() : f.getId().getRequesterId());
        }
        Map<Long, String> nicknames = nicknames(friendIds);
        return friendIds.stream().sorted()
                .map(id -> new FriendResponse(id, nicknames.get(id))).toList();
    }

    @Transactional(readOnly = true)
    public List<FriendResponse> listPendingReceived(Long userId) {
        List<Friendship> pending = friendshipRepository.findPendingReceived(userId);
        Map<Long, String> nicknames = nicknames(
                pending.stream().map(f -> f.getId().getRequesterId()).collect(Collectors.toSet()));
        return pending.stream()
                .map(f -> new FriendResponse(f.getId().getRequesterId(), nicknames.get(f.getId().getRequesterId())))
                .toList();
    }

    // ================= 내부 도우미 =================

    // 내가 볼 수 있는 후기에만 좋아요·댓글을 달 수 있다. (공개 범위 규칙을 뷰 v_visible_review 로 확인)
    private void requireVisible(Long viewerId, Long reviewId) {
        if (viewerId == null) {
            throw bad("userId 는 필수입니다.");
        }
        if (!visibleReviewRepository.existsById(new VisibleReviewId(viewerId, reviewId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "볼 수 있는 후기가 아닙니다.");
        }
    }

    private Map<Long, String> nicknames(Set<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getNickname, (a, b) -> a, java.util.HashMap::new));
    }

    private ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
