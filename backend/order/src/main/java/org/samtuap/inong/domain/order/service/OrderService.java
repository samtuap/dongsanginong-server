package org.samtuap.inong.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.order.dto.TopPackageResponse;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    public List<Long> getTopPackages() {
        return orderRepository.findTop10PackageIdWithMostOrders();
    }
}
