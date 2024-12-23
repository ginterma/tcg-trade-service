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
public class Rating {

    @Min(value = 1)
    @ApiModelProperty(notes = "Unique id of the Rating")
    private Long id;

    @ApiModelProperty(notes = "Unique id of the Trading that is being rated")
    @Min(value = 1)
    private Long tradeId;

    @ApiModelProperty(notes = "Unique id of the User that is being rated")
    @Min(value = 1)
    private String userId;

    @ApiModelProperty(notes = "Rating of the user's trade 1-10")
    @Min(value = 1)
    @Max(value = 10)
    private Integer rating;
}
