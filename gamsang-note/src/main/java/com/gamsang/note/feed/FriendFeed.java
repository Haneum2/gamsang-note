package com.gamsang.note.feed;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Immutable;
import org.hibernate.type.YesNoConverter;

import com.gamsang.note.viewing.VisibleReviewId;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * 뷰 v_friend_feed 를 읽기 전용으로 매핑했다. 한 행 = (보는 사람, 친구의 후기) 한 쌍.
 * 기본키 클래스는 이름만 다를 뿐 구조가 같아서(viewerId, reviewId) VisibleReviewId 를 재사용한다.
 */
@Entity
@Immutable
@Table(name = "v_friend_feed")
@IdClass(VisibleReviewId.class)
public class FriendFeed {

    @Id
    @Column(name = "viewer_id")
    private Long viewerId;

    @Id
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "friend_id")
    private Long friendId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "work_id")
    private Long workId;

    @Column(name = "work_type")
    private String workType;

    @Column(name = "title")
    private String title;

    @Column(name = "rating")
    private BigDecimal rating;

    @Lob
    @Column(name = "content")
    private String content;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "spoiler_yn")
    private boolean spoiler;

    @Column(name = "visibility")
    private String visibility;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected FriendFeed() {
    }

    public Long getViewerId() { return viewerId; }
    public Long getReviewId() { return reviewId; }
    public Long getFriendId() { return friendId; }
    public String getNickname() { return nickname; }
    public Long getWorkId() { return workId; }
    public String getWorkType() { return workType; }
    public String getTitle() { return title; }
    public BigDecimal getRating() { return rating; }
    public String getContent() { return content; }
    public boolean isSpoiler() { return spoiler; }
    public String getVisibility() { return visibility; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
