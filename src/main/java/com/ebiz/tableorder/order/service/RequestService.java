package com.ebiz.tableorder.order.service;

import com.ebiz.tableorder.order.dto.RequestDTO;
import com.ebiz.tableorder.order.entity.CustomerRequest;
import com.ebiz.tableorder.order.repository.CustomerRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// test
@Service
public class RequestService {
    private final CustomerRequestRepository repo;

    public RequestService(CustomerRequestRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void callStaff(Integer tableNumber) {
        CustomerRequest entity = CustomerRequest.builder()
                .tableNumber(tableNumber)
                .build();

        repo.save(entity);
    }
}