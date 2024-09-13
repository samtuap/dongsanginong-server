package org.samtuap.inong.domain.receipt.repository;

import org.samtuap.inong.domain.receipt.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
