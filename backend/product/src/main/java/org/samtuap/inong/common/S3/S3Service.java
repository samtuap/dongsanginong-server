package org.samtuap.inong.common.S3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // Presigned URL을 생성하는 로직
    public String generatePreSignedUrl(String objectKey) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(builder -> builder
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(10))
        );

        return presignedRequest.url().toString();
    }

    public void deleteFileFromS3(String imageUrl) {
        // S3 객체 삭제 요청 생성
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(getFileNameFromUrl(imageUrl))
                .build();

        // S3에서 이미지 삭제
        s3Client.deleteObject(deleteObjectRequest);
        System.out.println("Successfully deleted file from S3: " + imageUrl);  // 디버깅용 로그
    }


    // 이미지 URL에서 파일명을 추출하는 메서드
    private String getFileNameFromUrl(String imageUrl) {
        // '?' 문자가 있는 경우, 그 앞까지 파일명으로 취급
        int queryIndex = imageUrl.indexOf('?');
        if (queryIndex != -1) {
            imageUrl = imageUrl.substring(0, queryIndex);
        }
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

}