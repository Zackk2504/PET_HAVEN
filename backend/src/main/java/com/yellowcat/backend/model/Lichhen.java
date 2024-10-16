package com.yellowcat.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;


@Entity
@Table(name = "lichhen")
public class Lichhen {
    @Id
    @JsonProperty
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lichhen_id_gen")
    @SequenceGenerator(name = "lichhen_id_gen", sequenceName = "lichhen_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @JsonProperty
    @Column(name = "idkhachhang", nullable = false)
    private String idkhachhang;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idthucung")
    private Thucung thucung;

    @JsonProperty
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "iddichvu")
    private Dichvu dichvu;


    @JsonProperty
    @Column(name = "emailnguoidat", nullable = false)  // ánh xạ chính xác tên cột
    private String emailNguoiDat;

    @NotNull
    @JsonProperty
    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @NotNull
    @JsonProperty
    @Column(name = "trangthai", nullable = false)
    private Integer trangthai ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @Size(max = 255) @NotNull String getIdkhachhang() {
        return idkhachhang;
    }

    public void setIdkhachhang(@Size(max = 255) @NotNull String idkhachhang) {
        this.idkhachhang = idkhachhang;
    }

    public Thucung getThucung() {
        return thucung;
    }

    public void setThucung(Thucung thucung) {
        this.thucung = thucung;
    }

    public Dichvu getDichvu() {
        return dichvu;
    }

    public void setDichvu(Dichvu dichvu) {
        this.dichvu = dichvu;
    }

    public String getEmailNguoiDat() {
        return emailNguoiDat;
    }

    public void setEmailNguoiDat(String emailNguoiDat) {
        this.emailNguoiDat = emailNguoiDat;
    }

    public @NotNull LocalDateTime getDate() {
        return date;
    }

    public void setDate(@NotNull LocalDateTime date) {
        this.date = date;
    }

    public @NotNull Integer getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(@NotNull Integer trangthai) {
        this.trangthai = trangthai;
    }
}