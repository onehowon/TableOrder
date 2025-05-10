package com.ebiz.tableorder.order.repository;

import com.ebiz.tableorder.order.entity.CustomerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRequestRepository extends JpaRepository<CustomerRequest, Long> {
    // 필요시 findByTableNumber(), findRecent() 등 추가
}