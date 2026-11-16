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
import org.springframework.web.server.ResponseStatusException;

import com.gamsang.note.social.SocialDtos.CommentRequest;
import com.gamsang.note.social.SocialDtos.CommentResponse;
import com.gamsang.note.social.SocialDtos.FriendRequest;
import com.gamsang.note.social.SocialDtos.FriendResponse;
import com.gamsang.note.social.SocialDtos.FriendshipResponse;
import com.gamsang.note.social.SocialDtos.LikeResponse;
import com.gamsang.note.social.SocialDtos.SocialStats;
import com.gamsang.note.user.LoginUser;

@RestController
public class SocialController {

    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    // ---- 좋아요 ----
    @PostMapping("/api/reviews/{id}/likes")
    public LikeResponse like(@LoginUser Long me, @PathVariable Long id) {
        return socialService.like(id, me);
    }

    @DeleteMapping("/api/reviews/{id}/likes")
    public LikeResponse unlike(@LoginUser Long me, @PathVariable Long id) {
        return socialService.unlike(id, me);
    }

    // ---- 댓글 ----
    @PostMapping("/api/reviews/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(@LoginUser Long me, @PathVariable Long id, @RequestBody CommentRequest request) {
        return socialService.addComment(id, me, request.content());
    }

    @GetMapping("/api/reviews/{id}/comments")
    public List<CommentResponse> comments(@LoginUser Long me, @PathVariable Long id) {
        return socialService.listComments(id, me);
    }

    // 좋아요 수 · 댓글 수 · 내가 눌렀는지
    @GetMapping("/api/reviews/{id}/social")
    public SocialStats stats(@LoginUser Long me, @PathVariable Long id) {
        return socialService.stats(id, me);
    }

    // ---- 친구 ----
    @PostMapping("/api/friends/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendshipResponse request(@LoginUser Long me, @RequestBody FriendRequest request) {
        return socialService.requestFriend(me, request.receiverId());
    }

    @PostMapping("/api/friends/requests/accept")
    public FriendshipResponse accept(@LoginUser Long me, @RequestBody FriendRequest request) {
        return socialService.acceptFriend(request.requesterId(), me); // 나에게 온 요청만 수락할 수 있다
    }

    @DeleteMapping("/api/friends/requests")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@LoginUser Long me, @RequestParam Long requesterId, @RequestParam Long receiverId) {
        // 받은 요청 거절(나=receiver) 또는 보낸 요청 취소(나=requester)만 허용
        if (!me.equals(requesterId) && !me.equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "내 친구 요청만 거절하거나 취소할 수 있습니다.");
        }
        socialService.rejectFriend(requesterId, receiverId);
    }

    // 내가 받은 대기 중인 요청
    @GetMapping("/api/friends/requests")
    public List<FriendResponse> pending(@LoginUser Long me) {
        return socialService.listPendingReceived(me);
    }

    // 내가 보낸 대기 중인 요청 (취소는 DELETE /api/friends/requests?requesterId=나&receiverId=상대)
    @GetMapping("/api/friends/requests/sent")
    public List<FriendResponse> sent(@LoginUser Long me) {
        return socialService.listPendingSent(me);
    }

    // 내 친구 목록 (수락된 것만)
    @GetMapping("/api/friends")
    public List<FriendResponse> friends(@LoginUser Long me) {
        return socialService.listFriends(me);
    }
}
