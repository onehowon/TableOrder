package com.ebiz.tableorder.order.repository;

import com.ebiz.tableorder.order.entity.CustomerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {
    List<CustomerRequest> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}