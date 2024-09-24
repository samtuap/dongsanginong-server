package org.samtuap.inong.domain.member.dto;

public record PackageProductSubsResponse(Long packageId,
                                         String packageName,
                                         String imageUrl,
                                         String farmId,
                                         String farmName) {
}
