package org.samtuap.inong.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.samtuap.inong.domain.common.BaseEntity;

@Entity
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
}
