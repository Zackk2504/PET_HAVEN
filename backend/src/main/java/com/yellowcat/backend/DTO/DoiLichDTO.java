package com.yellowcat.backend.DTO;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class DoiLichDTO {
    @NotNull
    private String date;
    @NotNull
    private Integer idcalichhen;

    public @NotNull String getDate() {
        return date;
    }

    public void setDate(@NotNull String date) {
        this.date = date;
    }

    public @NotNull Integer getIdcalichhen() {
        return idcalichhen;
    }

    public void setIdcalichhen(@NotNull Integer idcalichhen) {
        this.idcalichhen = idcalichhen;
    }
}
