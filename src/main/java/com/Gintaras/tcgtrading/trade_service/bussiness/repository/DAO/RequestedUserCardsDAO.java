package com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Requested_User_Card")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestedUserCardsDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id")
    private TradeDAO tradeDAO;

    @Column(name = "requested_card_id")
    private String requestedCardId;

    @Column(name = "amount")
    private Integer amount;
}
