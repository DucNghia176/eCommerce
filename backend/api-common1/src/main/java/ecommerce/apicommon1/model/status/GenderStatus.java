package ecommerce.apicommon1.model.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GenderStatus {
    Nam("Nam"), Nữ("Nữ");
    private final String display;

    GenderStatus(String display) {
        this.display = display;
    }

    @JsonCreator
    public static GenderStatus fromValue(String value) {
        if (value == null) return null;
        value = value.trim().toLowerCase();
        switch (value) {
            case "nam":
                return Nam;
            case "nữ":
            case "nu":
                return Nữ;
            default:
                throw new IllegalArgumentException("Giới tính không hợp lệ: " + value);
        }
    }

    @JsonValue
    public String getDisplay() {
        return display;
    }
}
