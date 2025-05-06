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

    /**
     * MultipartFile을 OCI Object Storage에 올리고, 외부에서 접근 가능한 URL을 반환합니다.
     */
    public String upload(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            // 1) 유니크한 오브젝트 이름 생성
            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String objectName = UUID.randomUUID().toString() + ext;

            // 2) PutObject 요청
            PutObjectRequest req = PutObjectRequest.builder()
                    .namespaceName(namespace)
                    .bucketName(bucketName)
                    .objectName(objectName)
                    .putObjectBody(in)
                    .contentLength(file.getSize())
                    .build();

            PutObjectResponse resp = objectStorageClient.putObject(req);

            // 3) 퍼블릭 URL 조합
            //    SDK에 설정된 region을 가져와서 URL을 만들면 됩니다.
            String regionId = objectStorageClient.getRegion().getRegionId();
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
