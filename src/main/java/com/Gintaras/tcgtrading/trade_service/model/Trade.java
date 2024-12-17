package com.Gintaras.tcgtrading.trade_service.model;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    @Min(value = 1)
    @ApiModelProperty(notes = "Unique id of Trade")
    private Long id;
// funny comment.jpeg
    @ApiModelProperty(notes = "Unique id of requester")
    @Min(value = 1)
    private String requesterId;

    @ApiModelProperty(notes = "Unique id of offeree")
    @Min(value = 1)
    @NotNull
    private String offereeId;

    @ApiModelProperty(notes = "Date when trade was initiated")
    private Date tradeCreationDate;

    @ApiModelProperty(notes = "Date when trade was completed")
    private Date tradeCompletionDate;

    @ApiModelProperty(notes = "Status of the trade")
    private String tradeStatus;

    @ApiModelProperty(notes = "Values of cards that are offered")
    private Double offeredCardsValue;

    @ApiModelProperty(notes = "Value of card that are requested")
    private Double requestedCardsValue;
}
