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
    @SchedulerLock(name = "shedLock_launch_view", lockAtLeastFor = "1m", lockAtMostFor = "59m")
//    @Scheduled(cron = "0 0 4 * * *")
    @Scheduled(cron = "0 */2 * * * *")
    public void regularPayment() {
        // 오늘 결제해야하는 내용 가져오기
        log.info("line 22 Scheduler: 결제결제");
        orderService.regularPay();
    }
}
