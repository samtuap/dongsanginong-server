package org.samtuap.inong.common.scheduler;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.samtuap.inong.domain.order.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PaymentScheduler {
    private final OrderRepository orderRepository;
    @SchedulerLock(name = "shedLock_launch_view", lockAtLeastFor = "1m", lockAtMostFor = "59m")
    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void regulerPayment() {

    }

}
