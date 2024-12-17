package com.Gintaras.tcgtrading.trade_service.model;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestedUserCards {

    @ApiModelProperty(notes = "Unique id of the requested card")
    @Min(value = 1)
    private Long id;

    @ApiModelProperty(notes = "Unique id of Trade")
    @Min(value = 1)
    private Long tradeId;

    @ApiModelProperty(notes = "Unique id of requested Card")
    private String requestedCardId;

    @ApiModelProperty(notes = "Amount of the card requested")
    @Min(value = 1)
    private int amount;
}
