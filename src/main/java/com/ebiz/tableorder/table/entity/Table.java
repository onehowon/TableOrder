package com.ebiz.tableorder.table.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@jakarta.persistence.Table(name = "table")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Table {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", nullable = false, unique = true)
    private Integer tableNumber;
}
