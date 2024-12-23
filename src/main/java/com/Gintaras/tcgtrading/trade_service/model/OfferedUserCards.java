package com.Gintaras.tcgtrading.trade_service.model;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferedUserCards {

    @Min(value = 1, message = "Employee Id must be greater than 0")
    @ApiModelProperty(notes = "Unique id of the offered card")
    private Long id;

    @Min(value = 1)
    @ApiModelProperty(notes = "Unique id of Trade")
    private Long tradeId;

    @ApiModelProperty(notes = "Unique id of offered Card")
    private String offeredCardId;

    @Min(value = 1)
    @ApiModelProperty(notes = "Amount of the card offered")
    private Integer amount;
}
