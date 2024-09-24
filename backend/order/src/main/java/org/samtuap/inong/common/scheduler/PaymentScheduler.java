package org.samtuap.inong.common.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.samtuap.inong.domain.order.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentScheduler {
    private final OrderService orderService;
    @SchedulerLock(name = "shedLock_regular_payment", lockAtLeastFor = "1m", lockAtMostFor = "59m")
    @Scheduled(cron = "0 0 13 * * *")
    public void regularPayment() {
        orderService.regularPay();
    }
}
