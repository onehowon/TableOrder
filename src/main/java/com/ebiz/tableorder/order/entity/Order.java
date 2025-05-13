package com.ebiz.tableorder.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@Builder
@Getter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private com.ebiz.tableorder.table.entity.Table table;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name="estimated_time")
    private Integer estimatedTime;

    @Column(nullable = false, updatable = true)
    private boolean cleared = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public Order(Long id,
                 com.ebiz.tableorder.table.entity.Table table,
                 OrderStatus status,
                 Integer estimatedTime,
                 boolean cleared,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt,
                 List<OrderItem> items) {
        this.id            = id;
        this.table         = table;
        this.status        = status;
        this.estimatedTime = estimatedTime;
        this.cleared       = cleared;
        this.createdAt     = createdAt;
        this.updatedAt     = updatedAt;
        this.items         = items;
    }
}