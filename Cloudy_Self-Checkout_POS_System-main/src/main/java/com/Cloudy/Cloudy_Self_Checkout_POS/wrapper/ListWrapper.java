package com.Cloudy.Cloudy_Self_Checkout_POS.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListWrapper<T> {
    private final List<T> content;
    private final int total;
}
