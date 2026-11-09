package com.gamsang.note.social;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gamsang.note.social.SocialDtos.CommentRequest;
import com.gamsang.note.social.SocialDtos.CommentResponse;
import com.gamsang.note.social.SocialDtos.FriendRequest;
import com.gamsang.note.social.SocialDtos.FriendResponse;
import com.gamsang.note.social.SocialDtos.FriendshipResponse;
import com.gamsang.note.social.SocialDtos.LikeResponse;
import com.gamsang.note.social.SocialDtos.SocialStats;
import com.gamsang.note.social.SocialDtos.UserIdRequest;

@RestController
public class SocialController {

    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    // ---- 좋아요 ----
    @PostMapping("/api/reviews/{id}/likes")
    public LikeResponse like(@PathVariable Long id, @RequestBody UserIdRequest request) {
        return socialService.like(id, request.userId());
    }

    @DeleteMapping("/api/reviews/{id}/likes")
    public LikeResponse unlike(@PathVariable Long id, @RequestParam Long userId) {
        return socialService.unlike(id, userId);
    }

    // ---- 댓글 ----
    @PostMapping("/api/reviews/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@PathVariable Long id, @RequestBody CommentRequest request) {
        return socialService.addComment(id, request.userId(), request.content());
    }

    @GetMapping("/api/reviews/{id}/comments")
    public List<CommentResponse> comments(@PathVariable Long id, @RequestParam Long viewerId) {
        return socialService.listComments(id, viewerId);
    }

    // 좋아요 수 · 댓글 수 · 내가 눌렀는지
    @GetMapping("/api/reviews/{id}/social")
    public SocialStats stats(@PathVariable Long id, @RequestParam Long viewerId) {
        return socialService.stats(id, viewerId);
    }

    // ---- 친구 ----
    @PostMapping("/api/friends/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendshipResponse request(@RequestBody FriendRequest request) {
        return socialService.requestFriend(request.requesterId(), request.receiverId());
    }

    @PostMapping("/api/friends/requests/accept")
    public FriendshipResponse accept(@RequestBody FriendRequest request) {
        return socialService.acceptFriend(request.requesterId(), request.receiverId());
    }

    @DeleteMapping("/api/friends/requests")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@RequestParam Long requesterId, @RequestParam Long receiverId) {
        socialService.rejectFriend(requesterId, receiverId);
    }

    // 내가 받은 대기 중인 요청
    @GetMapping("/api/friends/requests")
    public List<FriendResponse> pending(@RequestParam Long userId) {
        return socialService.listPendingReceived(userId);
    }

    // 내 친구 목록 (수락된 것만)
    @GetMapping("/api/friends")
    public List<FriendResponse> friends(@RequestParam Long userId) {
        return socialService.listFriends(userId);
    }
}
