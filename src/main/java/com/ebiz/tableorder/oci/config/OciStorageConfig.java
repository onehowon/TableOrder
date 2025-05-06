package com.ebiz.tableorder.oci.config;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.StringPrivateKeySupplier;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.common.base.Supplier;

@Configuration
public class OciStorageConfig {

    @Value("${oci.auth.tenancy}")
    private String tenancy;

    @Value("${oci.auth.user}")
    private String user;

    @Value("${oci.auth.fingerprint}")
    private String fingerprint;

    @Value("${oci.auth.region}")
    private String regionId;

    @Value("${oci.auth.key-file}")
    private String keyFilePath;

    @Bean
    public ObjectStorage objectStorageClient() throws IOException {
        String privateKeyPem = Files.readString(Paths.get(keyFilePath));

        Supplier<InputStream> privateKeySupplier =
                new StringPrivateKeySupplier(privateKeyPem);

        SimpleAuthenticationDetailsProvider provider =
                SimpleAuthenticationDetailsProvider.builder()
                        .tenantId(tenancy)
                        .userId(user)
                        .fingerprint(fingerprint)
                        .region(Region.fromRegionId(regionId))
                        .privateKeySupplier(privateKeySupplier)
                        .build();

        return ObjectStorageClient.builder()
                .build(provider);
    }
}