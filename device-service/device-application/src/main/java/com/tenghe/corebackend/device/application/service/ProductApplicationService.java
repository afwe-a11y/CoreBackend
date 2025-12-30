package com.tenghe.corebackend.device.application.service;

import com.tenghe.corebackend.device.application.command.CreateProductCommand;
import com.tenghe.corebackend.device.application.command.UpdateProductCommand;
import com.tenghe.corebackend.device.application.command.UpdateProductMappingCommand;
import com.tenghe.corebackend.device.application.exception.BusinessException;
import com.tenghe.corebackend.device.application.service.result.CreateProductResult;
import com.tenghe.corebackend.device.application.service.result.PageResult;
import com.tenghe.corebackend.device.application.service.result.ProductListItemResult;
import com.tenghe.corebackend.device.application.validation.ValidationUtils;
import com.tenghe.corebackend.device.interfaces.DeviceModelRepositoryPort;
import com.tenghe.corebackend.device.interfaces.DeviceRepositoryPort;
import com.tenghe.corebackend.device.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.device.interfaces.ProductRepositoryPort;
import com.tenghe.corebackend.device.model.DeviceModel;
import com.tenghe.corebackend.device.model.Product;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class ProductApplicationService {
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String KEY_PREFIX = "PK";
    private static final String SECRET_PREFIX = "PS";
    private static final char[] KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private final ProductRepositoryPort productRepository;
    private final DeviceModelRepositoryPort deviceModelRepository;
    private final DeviceRepositoryPort deviceRepository;
    private final IdGeneratorPort idGenerator;
    private final SecureRandom secureRandom = new SecureRandom();

    public ProductApplicationService(
            ProductRepositoryPort productRepository,
            DeviceModelRepositoryPort deviceModelRepository,
            DeviceRepositoryPort deviceRepository,
            IdGeneratorPort idGenerator) {
        this.productRepository = productRepository;
        this.deviceModelRepository = deviceModelRepository;
        this.deviceRepository = deviceRepository;
        this.idGenerator = idGenerator;
    }

    public PageResult<ProductListItemResult> listProducts(Integer page, Integer size) {
        List<Product> products = productRepository.listAll();
        products.sort(Comparator.comparing(Product::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        long total = products.size();
        List<Product> paged = paginate(products, normalizePage(page), normalizeSize(size));
        List<ProductListItemResult> items = new ArrayList<>();
        for (Product product : paged) {
            ProductListItemResult item = new ProductListItemResult();
            item.setId(product.getId());
            item.setName(product.getName());
            item.setProductKey(product.getProductKey());
            item.setDeviceModelId(product.getDeviceModelId());
            item.setAccessMode(product.getAccessMode());
            item.setDescription(product.getDescription());
            items.add(item);
        }
        return new PageResult<>(items, total, normalizePage(page), normalizeSize(size));
    }

    public CreateProductResult createProduct(CreateProductCommand command) {
        ValidationUtils.requireNonBlank(command.getName(), "Product name is required");
        ValidationUtils.requireMaxLength(command.getName(), 50, "Product name too long");
        ValidationUtils.requireNotNull(command.getDeviceModelId(), "Device model is required");
        ValidationUtils.requireNonBlank(command.getAccessMode(), "Access mode is required");

        DeviceModel model = deviceModelRepository.findById(command.getDeviceModelId());
        if (model == null || model.isDeleted()) {
            throw new BusinessException("Device model not found");
        }

        String productKey = generateUniqueProductKey(KEY_PREFIX, 12);
        String productSecret = generateSecret(SECRET_PREFIX, 16);
        ValidationUtils.validateProductSecret(productSecret, "Product secret invalid");

        Product product = new Product();
        product.setId(idGenerator.nextId());
        product.setName(command.getName());
        product.setProductKey(productKey);
        product.setProductSecret(productSecret);
        product.setDeviceModelId(command.getDeviceModelId());
        product.setAccessMode(command.getAccessMode());
        product.setDescription(command.getDescription());
        product.setProtocolMapping(command.getProtocolMapping());
        product.setCreatedAt(Instant.now());
        product.setDeleted(false);
        productRepository.save(product);

        CreateProductResult result = new CreateProductResult();
        result.setId(product.getId());
        result.setProductKey(productKey);
        result.setProductSecret(productSecret);
        return result;
    }

    public void updateProduct(UpdateProductCommand command) {
        Product product = requireProduct(command.getProductId());
        if (command.getName() != null) {
            ValidationUtils.requireMaxLength(command.getName(), 50, "Product name too long");
            product.setName(command.getName());
        }
        if (command.getDescription() != null) {
            product.setDescription(command.getDescription());
        }
        productRepository.update(product);
    }

    public void updateProtocolMapping(UpdateProductMappingCommand command) {
        Product product = requireProduct(command.getProductId());
        product.setProtocolMapping(command.getProtocolMapping());
        productRepository.update(product);
    }

    public void deleteProduct(Long productId) {
        Product product = requireProduct(productId);
        if (deviceRepository.countByProductId(productId) > 0) {
            throw new BusinessException("Product has devices bound");
        }
        product.setDeleted(true);
        productRepository.update(product);
    }

    private Product requireProduct(Long productId) {
        if (productId == null) {
            throw new BusinessException("Product not found");
        }
        Product product = productRepository.findById(productId);
        if (product == null || product.isDeleted()) {
            throw new BusinessException("Product not found");
        }
        return product;
    }

    private String generateUniqueProductKey(String prefix, int randomLength) {
        String key;
        do {
            key = prefix + randomAlphaNumeric(randomLength);
        } while (productRepository.findByProductKey(key) != null);
        return key.toUpperCase(Locale.ROOT);
    }

    private String generateSecret(String prefix, int randomLength) {
        return (prefix + randomAlphaNumeric(randomLength)).toUpperCase(Locale.ROOT);
    }

    private String randomAlphaNumeric(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(KEY_CHARS[secureRandom.nextInt(KEY_CHARS.length)]);
        }
        return builder.toString();
    }

    private <T> List<T> paginate(List<T> items, int page, int size) {
        if (items.isEmpty()) {
            return new ArrayList<>();
        }
        int fromIndex = Math.max(0, (page - 1) * size);
        if (fromIndex >= items.size()) {
            return new ArrayList<>();
        }
        int toIndex = Math.min(fromIndex + size, items.size());
        return items.subList(fromIndex, toIndex);
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return size;
    }
}
