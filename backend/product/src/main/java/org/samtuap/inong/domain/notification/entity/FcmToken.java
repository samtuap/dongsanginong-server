package org.samtuap.inong.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.samtuap.inong.domain.common.BaseEntity;
import org.samtuap.inong.domain.seller.entity.Seller;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class FcmToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar (255) unique not null")
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

}