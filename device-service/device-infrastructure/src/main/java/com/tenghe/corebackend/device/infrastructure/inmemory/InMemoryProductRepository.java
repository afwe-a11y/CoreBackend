package com.tenghe.corebackend.device.infrastructure.inmemory;

import com.tenghe.corebackend.device.interfaces.ProductRepositoryPort;
import com.tenghe.corebackend.device.model.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryProductRepository implements ProductRepositoryPort {
    private final Map<Long, Product> store = new ConcurrentHashMap<>();

    @Override
    public Product save(Product product) {
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public Product update(Product product) {
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public Product findById(Long id) {
        return store.get(id);
    }

    @Override
    public Product findByProductKey(String productKey) {
        if (productKey == null) {
            return null;
        }
        for (Product product : store.values()) {
            if (!product.isDeleted() && productKey.equals(product.getProductKey())) {
                return product;
            }
        }
        return null;
    }

    @Override
    public List<Product> listAll() {
        List<Product> results = new ArrayList<>();
        for (Product product : store.values()) {
            if (!product.isDeleted()) {
                results.add(product);
            }
        }
        return results;
    }

    @Override
    public List<Product> listByDeviceModelId(Long deviceModelId) {
        List<Product> results = new ArrayList<>();
        if (deviceModelId == null) {
            return results;
        }
        for (Product product : store.values()) {
            if (!product.isDeleted() && deviceModelId.equals(product.getDeviceModelId())) {
                results.add(product);
            }
        }
        return results;
    }
}
