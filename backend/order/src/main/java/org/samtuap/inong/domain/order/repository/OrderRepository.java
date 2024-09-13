package org.samtuap.inong.domain.order.repository;

import org.samtuap.inong.domain.order.entity.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Ordering, Long> {
}
