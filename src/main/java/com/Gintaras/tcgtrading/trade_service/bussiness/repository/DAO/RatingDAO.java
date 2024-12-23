package com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_id")
    private TradeDAO tradeDAO;

    @Column(name = "user_id")
    private String userId;


    @Column(name = "rating")
    private Integer rating;


    public RatingDAO(Long ratingId) {
    }
}

