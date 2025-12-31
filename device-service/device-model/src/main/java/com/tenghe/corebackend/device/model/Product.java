package com.tenghe.corebackend.device.model;

import java.time.Instant;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product {
    private Long id;
    private ProductType productType;
    private String name;
    private String productKey;
    private String productSecret;
    private Long deviceModelId;
    private String accessMode;
    private String description;
    private Map<String, String> protocolMapping;
    private Instant createdAt;
    private boolean deleted;
}
