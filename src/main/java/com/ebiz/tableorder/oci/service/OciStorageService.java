package com.ebiz.tableorder.oci.service;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OciStorageService {
    private final ObjectStorage objectStorageClient;

    @Value("${oci.namespace}")
    private String namespace;

    @Value("${oci.bucket-name}")
    private String bucketName;

    @Value("${oci.auth.region}")
    private String regionId;

    /**
     * MultipartFile을 OCI Object Storage에 올리고, 외부에서 접근 가능한 URL을 반환합니다.
     */
    public String upload(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            // 1) 원본 파일명에서 확장자 추출
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.lastIndexOf('.') != -1) {
                ext = original.substring(original.lastIndexOf('.'));
            }

            // 2) UUID 기반의 objectName 생성
            String objectName = UUID.randomUUID().toString() + ext;

            // 3) PutObjectRequest 빌드
            PutObjectRequest req = PutObjectRequest.builder()
                    .namespaceName(namespace)
                    .bucketName(bucketName)
                    .objectName(objectName)
                    .putObjectBody(in)
                    .contentLength(file.getSize())
                    .build();

            // 4) 업로드 실행
            PutObjectResponse resp = objectStorageClient.putObject(req);

            // 5) URL 인코딩 및 리턴
            String encodedName = URLEncoder.encode(objectName, StandardCharsets.UTF_8);
            return String.format(
                    "https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
                    regionId, namespace, bucketName, encodedName
            );
        } catch (IOException e) {
            throw new RuntimeException("OCI 파일 업로드 실패", e);
        }
    }
}
