package com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO;

import jakarta.persistence.*;
import lombok.*;

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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TradeDAO tradeDAO;

    @Column(name = "requested_card_id")
    private String requestedCardId;

    @Column(name = "amount")
    private Integer amount;

    public RequestedUserCardsDAO(Long cardId) {
    }
}
