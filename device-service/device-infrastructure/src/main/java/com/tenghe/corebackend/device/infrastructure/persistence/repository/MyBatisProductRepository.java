package com.tenghe.corebackend.device.infrastructure.persistence.repository;

import com.tenghe.corebackend.device.infrastructure.persistence.json.JsonCodec;
import com.tenghe.corebackend.device.infrastructure.persistence.mapper.ProductMapper;
import com.tenghe.corebackend.device.infrastructure.persistence.po.ProductPo;
import com.tenghe.corebackend.device.interfaces.ProductRepositoryPort;
import com.tenghe.corebackend.device.model.Product;
import com.tenghe.corebackend.device.model.ProductType;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisProductRepository implements ProductRepositoryPort {
    private static final String DEFAULT_STATUS = "NORMAL";

    private final ProductMapper productMapper;
    private final JsonCodec jsonCodec;

    public MyBatisProductRepository(ProductMapper productMapper, JsonCodec jsonCodec) {
        this.productMapper = productMapper;
        this.jsonCodec = jsonCodec;
    }

    @Override
    public Product save(Product product) {
        ProductPo po = toPo(product);
        productMapper.insert(po);
        return product;
    }

    @Override
    public Product update(Product product) {
        ProductPo po = toPo(product);
        productMapper.update(po);
        return product;
    }

    @Override
    public Product findById(Long id) {
        return toModel(productMapper.findById(id));
    }

    @Override
    public Product findByProductKey(String productKey) {
        return toModel(productMapper.findByProductKey(productKey));
    }

    @Override
    public List<Product> listAll() {
        return productMapper.listAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> listByDeviceModelId(Long deviceModelId) {
        return productMapper.listByDeviceModelId(deviceModelId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private Product toModel(ProductPo po) {
        if (po == null) {
            return null;
        }
        Product product = new Product();
        product.setId(po.getId());
        product.setProductType(ProductType.fromValue(po.getProductType()));
        product.setName(po.getName());
        product.setProductKey(po.getProductKey());
        product.setProductSecret(po.getProductSecret());
        product.setDeviceModelId(po.getDeviceModelId());
        product.setAccessMode(po.getAccessMode());
        product.setDescription(po.getDescription());
        product.setProtocolMapping(readStringMap(po.getProtocolMapping()));
        product.setCreatedAt(po.getCreateTime());
        product.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
        return product;
    }

    private ProductPo toPo(Product product) {
        ProductPo po = new ProductPo();
        po.setId(product.getId());
        ProductType type = product.getProductType() == null ? ProductType.DEVICE : product.getProductType();
        po.setProductType(type.name());
        po.setName(product.getName());
        po.setDescription(product.getDescription());
        po.setProductKey(product.getProductKey());
        po.setProductSecret(product.getProductSecret());
        po.setDeviceModelId(product.getDeviceModelId());
        po.setAccessMode(product.getAccessMode());
        po.setProtocolMapping(jsonCodec.writeValue(product.getProtocolMapping()));
        po.setStatus(DEFAULT_STATUS);
        po.setCreateTime(product.getCreatedAt() == null ? Instant.now() : product.getCreatedAt());
        po.setDeleted(product.isDeleted() ? 1 : 0);
        return po;
    }

    private Map<String, String> readStringMap(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        Map<String, Object> raw = jsonCodec.readMap(json);
        if (raw.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : raw.entrySet()) {
            result.put(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString());
        }
        return result;
    }
}
