package com.procurement.system.common.storage;

import com.procurement.system.common.config.MinioConfig;
import com.procurement.system.common.exception.BusinessException;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public void uploadFile(String objectName, InputStream inputStream, long size, String contentType) {
        try {
            ensureBucketExists();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("Failed to upload file: " + e.getMessage());
        }
    }

    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .method(Method.GET)
                    .expiry(1, TimeUnit.HOURS)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("Failed to generate download URL: " + e.getMessage());
        }
    }

    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("Failed to delete file: " + e.getMessage());
        }
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucket())
                    .build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .build());
            }
        } catch (Exception e) {
            throw new BusinessException("Failed to ensure bucket exists: " + e.getMessage());
        }
    }
}
