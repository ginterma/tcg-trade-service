package com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO;

import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="Trade")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "requester_id")
    private String requesterId;

    @Column(name = "offereeId")
    private String offereeId;

    @Column(name = "trade_creation_date")
    private Date tradeCreationDate;

    @Column(name = "trade_completion_date")
    private Date tradeCompletionDate;

    @Column(name = "trade_status")
    private String tradeStatus;

    @Column(name = "offered_cards_value")
    private Double offeredCardsValue;

    @Column(name = "requested_cards_value")
    private Double requestedCardsValue;

    @OneToMany(mappedBy = "tradeDAO", fetch = FetchType.LAZY)
    private List<RatingDAO> ratingList;
}
