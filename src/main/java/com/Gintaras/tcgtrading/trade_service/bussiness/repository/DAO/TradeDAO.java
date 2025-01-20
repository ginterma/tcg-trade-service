package com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO;

import com.Gintaras.tcgtrading.trade_service.model.TradeStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "offeree_id")
    private String offereeId;

    @Column(name = "trade_creation_date")
    private Date tradeCreationDate;

    @Column(name = "trade_completion_date")
    private Date tradeCompletionDate;

    @Enumerated(EnumType.STRING) // Persist the enum as a String
    @Column(name = "trade_status")
    private TradeStatus tradeStatus;

    @Column(name = "offered_cards_value")
    private Double offeredCardsValue;

    @Column(name = "requested_cards_value")
    private Double requestedCardsValue;

    @OneToMany(mappedBy = "tradeDAO", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<OfferedUserCardsDAO> offeredCardList;

    @OneToMany(mappedBy = "tradeDAO", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<RatingDAO> ratingList;

    @OneToMany(mappedBy = "tradeDAO", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<RequestedUserCardsDAO> requestedCardList;

    @Override
    public String toString() {
        return "TradeDAO{" +
                "id=" + id +
                ", requesterId='" + requesterId + '\'' +
                ", offereeId='" + offereeId + '\'' +
                ", tradeStatus=" + tradeStatus +
                ", offeredCardsValue=" + offeredCardsValue +
                ", requestedCardsValue=" + requestedCardsValue +
                '}';
    }
}
