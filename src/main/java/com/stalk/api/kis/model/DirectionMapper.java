package com.stalk.api.kis.model;

public final class DirectionMapper {

    private DirectionMapper() {
    }

    public static Direction fromKisSign(String sign) {
        return switch (sign) {
            case "1", "2" -> Direction.UP;
            case "4", "5" -> Direction.DOWN;
            case "3" -> Direction.FLAT;
            default -> throw new IllegalArgumentException("Unknown KIS sign: " + sign);
        };
    }
}
