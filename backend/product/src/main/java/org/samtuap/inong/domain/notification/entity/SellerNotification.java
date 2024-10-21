package org.samtuap.inong.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.seller.entity.Seller;

import javax.annotation.Nonnull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@SQLDelete(sql = "UPDATE notification SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
public class SellerNotification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    private String url;
    private boolean isRead;

    public void updateIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
