CREATE TABLE TaiKhoan
(
    ID        SERIAL PRIMARY KEY,
    Username  VARCHAR(100) NOT NULL,
    Pass      VARCHAR(255) NOT NULL,
    TrangThai BOOLEAN      NOT NULL,
    Gmail     VARCHAR(255),
    Image     TEXT,
    Role      VARCHAR(50)  NOT NULL
);

CREATE TABLE ThongTinCaNhan
(
    ID         SERIAL PRIMARY KEY,
    HoTen      VARCHAR(100) NOT NULL,
    Tuoi       INT,
    GioiTinh   VARCHAR(10),
    idTaiKhoan INT REFERENCES TaiKhoan (ID),
    SDT        VARCHAR(20)
);

CREATE TABLE DichVu
(
    ID        SERIAL PRIMARY KEY,
    TenDichVu VARCHAR(100) NOT NULL,
    MoTa      TEXT         NOT NULL,
    Anh       TEXT,
    GiaTien   INT          NOT NULL
);

CREATE TABLE ThuCung
(
    ID         SERIAL PRIMARY KEY,
    TenCho     VARCHAR(100) NOT NULL,
    CanNang    FLOAT,
    Tuoi       INT,
    GiongCho   VARCHAR(100),
    IDTaiKhoan VARCHAR(255) not null ,
    Image      TEXT
);

CREATE TABLE LichHen
(
    ID          SERIAL PRIMARY KEY,
    idKhachHang VARCHAR(255) NOT NULL ,
    idThuCung   INT REFERENCES ThuCung (ID),
    idDichVu    INT REFERENCES DichVu (ID),
    Date        TIMESTAMP NOT NULL,
    TrangThai   INT   NOT NULL,
    EmailNguoiDat VARCHAR(255) NOT NULL default '123@gmail.com'
);
-- quy ước:
-- 0 : thành công
-- 1 : thất bại
-- 2 : đã hủy
-- 3 : chờ thanh toán
-- 4 : chờ xác nhận

CREATE TABLE NhacLichHen
(
    ID          SERIAL PRIMARY KEY,
    TenThongBao VARCHAR(100) NOT NULL,
    MoTa        TEXT         NOT NULL,
    idTaiKhoan  VARCHAR(255) NOT NULL ,
    ISRead      BOOLEAN,
    TrangThai   BOOLEAN
);

CREATE TABLE DanhGia
(
    ID         SERIAL PRIMARY KEY,
    idTaiKhoan VARCHAR(255) not null ,
    SoSao      INT,
    MoTa       TEXT,
    Date       TIMESTAMP,
    TrangThai  BOOLEAN
);

CREATE TABLE GiamGia
(
    ID           SERIAL PRIMARY KEY,
    PhanTramGiam INT,
    NgayBatDau   TIMESTAMP,
    NgayKetThuc  TIMESTAMP,
    MoTa         TEXT,
    TrangThai    BOOLEAN
);

CREATE TABLE HoaDon
(
    ID                  SERIAL PRIMARY KEY,
    idLichHen           INT REFERENCES LichHen (ID),
    Date                TIMESTAMP,
    SoTien              INT NOT NULL,
    NgayThanhToan       TIMESTAMP,
    PhuongThucThanhToan VARCHAR(50),
    TrangThai           BOOLEAN,
    idGiamGia           INT REFERENCES GiamGia (ID)
);


-- Thêm dữ liệu vào bảng TaiKhoan
INSERT INTO TaiKhoan (Username, Pass, TrangThai, Gmail, Image, Role)
VALUES ('user1', 'pass1', TRUE, 'user1@gmail.com', NULL, 'USER'),
       ('user2', 'pass2', FALSE, 'user2@gmail.com', NULL, 'ADMIN'),
       ('user3', 'pass3', TRUE, 'user3@gmail.com', NULL, 'USER');

-- Thêm dữ liệu vào bảng ThongTinCaNhan
INSERT INTO ThongTinCaNhan (HoTen, Tuoi, GioiTinh, idTaiKhoan, SDT)
VALUES ('Nguyen Van A', 25, 'Nam', 1, '0901234567'),
       ('Tran Thi B', 30, 'Nu', 2, '0907654321'),
       ('Le Van C', 22, 'Nam', 3, '0901122334');

-- Thêm dữ liệu vào bảng DichVu
INSERT INTO DichVu (TenDichVu, MoTa, Anh, GiaTien)
VALUES ('Dịch vụ 1', 'Mô tả dịch vụ 1', NULL, 100000),
       ('Dịch vụ 2', 'Mô tả dịch vụ 2', NULL, 150000),
       ('Dịch vụ 3', 'Mô tả dịch vụ 3', NULL, 200000);

-- Thêm dữ liệu vào bảng ThuCung
INSERT INTO ThuCung (TenCho, CanNang, Tuoi, GiongCho, IDTaiKhoan, Image)
VALUES ('Dog 1', 10.5, 2, 'Golden', 1, NULL),
       ('Dog 2', 8.3, 3, 'Bulldog', 2, NULL),
       ('Dog 3', 12.7, 4, 'Labrador', 3, NULL);

-- Thêm dữ liệu vào bảng LichHen
INSERT INTO LichHen (idKhachHang, idThuCung, idDichVu, Date, TrangThai)
VALUES (1, 1, 1, '2024-10-02 10:00:00', 1),
       (2, 2, 2, '2024-10-03 14:00:00', 2),
       (3, 3, 3, '2024-10-04 09:00:00', 3);

-- Thêm dữ liệu vào bảng NhacLichHen
INSERT INTO NhacLichHen (TenThongBao, MoTa, idTaiKhoan, ISRead, TrangThai)
VALUES ('Nhắc lịch 1', 'Thông báo lịch hẹn 1', 1, FALSE, TRUE),
       ('Nhắc lịch 2', 'Thông báo lịch hẹn 2', 2, TRUE, FALSE),
       ('Nhắc lịch 3', 'Thông báo lịch hẹn 3', 3, FALSE, TRUE);

-- Thêm dữ liệu vào bảng DanhGia
INSERT INTO DanhGia (idTaiKhoan, SoSao, MoTa, Date, TrangThai)
VALUES (1, 5, 'Rất tốt', '2024-10-01 12:00:00', TRUE),
       (2, 4, 'Tốt', '2024-09-30 11:00:00', TRUE),
       (3, 3, 'Bình thường', '2024-09-29 10:00:00', FALSE);

-- Thêm dữ liệu vào bảng GiamGia
INSERT INTO GiamGia (PhanTramGiam, NgayBatDau, NgayKetThuc, MoTa, TrangThai)
VALUES (10, '2024-10-01 00:00:00', '2024-10-31 23:59:59', 'Giảm giá tháng 10', TRUE),
       (20, '2024-11-01 00:00:00', '2024-11-30 23:59:59', 'Giảm giá tháng 11', TRUE),
       (15, '2024-12-01 00:00:00', '2024-12-31 23:59:59', 'Giảm giá tháng 12', TRUE);

-- Thêm dữ liệu vào bảng HoaDon
INSERT INTO HoaDon (idLichHen, Date, SoTien, NgayThanhToan, PhuongThucThanhToan, TrangThai, idGiamGia)
VALUES (1, '2024-10-02 10:00:00', 90000, '2024-10-02 11:00:00', 'Tiền mặt', TRUE, 1),
       (2, '2024-10-03 14:00:00', 120000, '2024-10-03 15:00:00', 'Thẻ tín dụng', FALSE, 2),
       (3, '2024-10-04 09:00:00', 170000, '2024-10-04 10:00:00', 'Chuyển khoản', TRUE, 3);
