package com.ebiz.tableorder.menu.repository;

import com.ebiz.tableorder.menu.entity.Menu;
import com.ebiz.tableorder.table.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
}
