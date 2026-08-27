package com.acquaintech.demo.polyglot.quantity;

import com.acquaintech.demo.polyglot.exception.QuantityOperationFailedException;
import com.acquaintech.demo.polyglot.quantity.dto.QuantityRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuantityService {

    private final QuantityRepository quantityRepository;

    @Retryable(retryFor = DataAccessException.class, maxAttempts = 3, backoff = @Backoff(delay = 500, multiplier = 2))
    public Quantity create(String productId, QuantityRequest request) {
        Quantity quantity = Quantity.builder()
                .productId(productId)
                .quantity(request.getQuantity())
                .warehouseLocation(request.getWarehouseLocation())
                .build();
        return quantityRepository.save(quantity);
    }

    @Recover
    public Quantity recoverCreate(DataAccessException ex, String productId, QuantityRequest request) {
        log.error("Failed to write quantity for product {} after retries exhausted", productId, ex);
        throw new QuantityOperationFailedException(
                "Could not persist quantity for product " + productId + " in MySQL after retries", ex);
    }

    public List<Quantity> findByProductId(String productId) {
        return quantityRepository.findByProductId(productId);
    }

    @Retryable(retryFor = DataAccessException.class, maxAttempts = 3, backoff = @Backoff(delay = 500, multiplier = 2))
    public void deleteByProductId(String productId) {
        quantityRepository.deleteByProductId(productId);
    }

    @Recover
    public void recoverDelete(DataAccessException ex, String productId) {
        log.error("Failed to delete quantity rows for product {} after retries exhausted", productId, ex);
        throw new QuantityOperationFailedException(
                "Could not delete quantity rows for product " + productId + " in MySQL after retries", ex);
    }
}
