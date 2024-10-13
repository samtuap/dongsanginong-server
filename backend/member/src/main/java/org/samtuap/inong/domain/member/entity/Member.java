package org.samtuap.inong.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.samtuap.inong.domain.common.BaseEntity;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@SQLDelete(sql = "UPDATE member SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@Getter
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String phone;

    @NotNull
    private String address;

    @NotNull
    private String addressDetail;

    @NotNull
    private String zipcode;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    @NotNull
    private String socialId;

    private PaymentMethod paymentMethod;

    private String billingKey;

    public void updatePhone(String phone){
        this.phone = phone;
    }

    public void updateAddress(String address, String addressDetail, String zipcode){
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipcode = zipcode;
    }


    public void updateBillingKey(String billingKey) {
        this.billingKey = billingKey;
    }

    public void updatePaymentMethod(PaymentMethod paymentMethod, String billingKey) {
        updateBillingKey(billingKey);
        this.paymentMethod = paymentMethod;
    }
}
