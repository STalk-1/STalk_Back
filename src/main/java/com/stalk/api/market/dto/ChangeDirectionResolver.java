package com.stalk.api.market.dto;

import com.stalk.api.market.Domain.ChangeDirection;

import java.math.BigDecimal;

public final class ChangeDirectionResolver {

    private ChangeDirectionResolver() {}

    public static ChangeDirection resolve(BigDecimal changeValue) {
        if (changeValue == null) return ChangeDirection.FLAT;

        int c = changeValue.compareTo(BigDecimal.ZERO);
        if (c > 0) return ChangeDirection.UP;
        if (c < 0) return ChangeDirection.DOWN;
        return ChangeDirection.FLAT;
    }
}