package com.ebiz.tableorder.table.repository;

import com.ebiz.tableorder.table.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {
    Optional<Table> findByTableNumber(Integer tableNumber);
}
