package com.ebiz.tableorder.order.service;

import com.ebiz.tableorder.order.dto.RequestDTO;
import com.ebiz.tableorder.order.entity.CustomerRequest;
import com.ebiz.tableorder.order.repository.CustomerRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// test
@Service
public class RequestService {
    private final CustomerRequestRepository repo;

    public RequestService(CustomerRequestRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void postRequest(RequestDTO req) {
        CustomerRequest entity = CustomerRequest.builder()
                .tableNumber(req.getTableNumber())
                .type(req.getType())
                .build();

        repo.save(entity);
        // TODO: WebSocket 알림이나 FCM 푸시 로직 추가
    }

    @Transactional(readOnly = true)
    public List<CustomerRequest> getTodayRequests() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end   = start.plusDays(1);
        return repo.findAllByCreatedAtBetween(start, end);
    }
}