package org.samtuap.inong.S3.dto;

public record PresignedRequest(
    String prefix,
    String url
) {
}
