package com.example.calcprofit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ResponseCurrency {
    private Boolean success;
    private Timestamp timestamp;
    private String base;
    private String date;
    private String source;
    private Map<String, BigDecimal> rates;
}
