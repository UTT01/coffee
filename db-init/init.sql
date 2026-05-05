-- =============================================
-- 1. SERVICE USER
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ServiceUser')
CREATE DATABASE [ServiceUser];
GO
USE [ServiceUser]
GO
CREATE TABLE [dbo].[NhanVien](
	[maNhanVien] [varchar](20) NOT NULL PRIMARY KEY,
	[tenNhanVien] [nvarchar](100) NULL,
	[chucVu] [nvarchar](50) NULL,
	[tienLuong] [float] NULL,
	[ngayVaoLam] [date] NULL,
	[ngaySinh] [date] NULL,
	[trangThai] [nvarchar](20) DEFAULT (N'Đang làm')
);
CREATE TABLE [dbo].[TaiKhoan](
	[maTaiKhoan] [varchar](20) NOT NULL PRIMARY KEY,
	[maNhanVien] [varchar](20) NULL,
	[tenDangNhap] [varchar](255) NULL,
	[matKhau] [varchar](255) NULL,
	[loaiTaiKhoan] [nvarchar](20) NULL,
	[OTP] [int] NULL,
    CONSTRAINT [FK_TaiKhoan_NhanVien] FOREIGN KEY([maNhanVien]) REFERENCES [dbo].[NhanVien] ([maNhanVien])
);
GO
INSERT [dbo].[NhanVien] VALUES (N'NV001', N'Nguyễn Văn An', N'Quản lý', 20000000, '2023-01-15', '1990-05-20', N'Đang làm')
INSERT [dbo].[NhanVien] VALUES (N'QL001', N'Bùi Đức Hải', N'Quản lý', 1000000, '2026-04-11', '2005-12-25', N'Đang làm')
INSERT [dbo].[TaiKhoan] VALUES (N'TK0004', N'QL001', N'toilahai05@gmail.com', N'$2a$10$oK1isdURYPkBKB7KeQYnD.EnmG5vJo6t2xeDoxi02WH75fkNXU9ru', N'ADMIN', 228797)
GO

-- =============================================
-- 2. SERVICE POS (CAFE/ORDER)
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ServicePOS')
CREATE DATABASE [ServicePOS];
GO
USE [ServicePOS]
GO
CREATE TABLE [dbo].[HoaDon](
	[maHoaDon] [varchar](20) NOT NULL PRIMARY KEY,
	[maBan] [varchar](20) NULL,
	[thoiGianVao] [datetime] NULL,
	[thoiGianRa] [datetime] NULL,
	[phuongThucThanhToan] [nvarchar](20) NULL,
	[maKhuyenMai] [varchar](20) NULL,
	[tongTien] [float] NULL,
	[trangThaiThanhToan] [nvarchar](20) NULL,
	[maCa] [varchar](20) NULL
);
CREATE TABLE [dbo].[ChiTietHD](
	[maChiTietHD] [varchar](50) NOT NULL PRIMARY KEY,
	[maHoaDon] [varchar](20) NULL,
	[maSanPham] [varchar](20) NULL,
	[soLuong] [int] NULL,
	[donGia] [float] NULL,
	[ghiChu] [nvarchar](100) NULL,
    CONSTRAINT [FK_ChiTietHD_HoaDon] FOREIGN KEY([maHoaDon]) REFERENCES [dbo].[HoaDon] ([maHoaDon])
);
GO

-- =============================================
-- 3. SERVICE DOANH THU
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ServiceDoanhThu')
CREATE DATABASE [ServiceDoanhThu];
GO
USE [ServiceDoanhThu]
GO
CREATE TABLE [dbo].[ca](
	[maCa] [varchar](50) NOT NULL PRIMARY KEY,
	[maNhanVien] [varchar](50) NOT NULL,
	[ngayThang] [date] NOT NULL,
	[soTienKet] [decimal](15, 2) DEFAULT NULL,
	[tenCa] [nvarchar](255) NOT NULL,
	[trangThai] [nvarchar](255) NOT NULL,
	[gioMoCa] [time](7) DEFAULT NULL,
	[gioDongCa] [time](7) DEFAULT NULL
);
CREATE TABLE [dbo].[doanhthu](
	[maDoanhThu] [varchar](50) NOT NULL PRIMARY KEY,
	[maCa] [varchar](50) NOT NULL,
	[tienCK] [decimal](15, 2) DEFAULT NULL,
	[tienChi] [decimal](15, 2) DEFAULT NULL,
	[tienMat] [decimal](15, 2) DEFAULT NULL,
	[tienThu] [decimal](15, 2) DEFAULT NULL
);
GO
INSERT [dbo].[ca] VALUES (N'CA003', N'NV001', '2026-04-12', 1371000.00, N'Chiều 12/04', N'Mở', '16:59:15', NULL)
GO

-- =============================================
-- 4. SERVICE NOTIFICATION
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ServiceNotification')
CREATE DATABASE [ServiceNotification];
GO
USE [ServiceNotification]
GO
CREATE TABLE [dbo].[ThongBao](
	[maThongBao] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY,
	[maNhanVien] [varchar](20) NOT NULL,
	[tieuDe] [nvarchar](255) NOT NULL,
	[noiDung] [nvarchar](max) NULL,
	[loaiThongBao] [varchar](50) NOT NULL,
	[idThamChieu] [varchar](50) NULL,
	[daDoc] [bit] DEFAULT (0),
	[ngayTao] [datetime] DEFAULT (getdate())
);
GO

-- =============================================
-- 5. SERVICE PRODUCT
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ServiceProduct')
CREATE DATABASE [ServiceProduct];
GO
USE [ServiceProduct]
GO
CREATE TABLE [dbo].[LoaiSanPham](
	[maLoaiSanPham] [varchar](20) NOT NULL PRIMARY KEY,
	[tenLoaiSanPham] [nvarchar](100) NULL,
	[duongDanHinh] [nvarchar](max) NULL
);
CREATE TABLE [dbo].[SanPham](
	[maSanPham] [varchar](20) NOT NULL PRIMARY KEY,
	[tenSanPham] [nvarchar](100) NULL,
	[donGia] [float] NULL,
	[duongDanHinh] [nvarchar](max) NULL,
	[maLoaiSanPham] [varchar](20) REFERENCES [LoaiSanPham]([maLoaiSanPham]),
	[trangThai] [nvarchar](20) NULL
);
GO
CREATE TABLE [dbo].[CongThuc](
    [maSanPham] [varchar](20) NOT NULL,
    [maNguyenLieu] [varchar](20) NOT NULL,
    [soLuong] [float] NULL,
    CONSTRAINT [PK_CongThuc] PRIMARY KEY ([maSanPham], [maNguyenLieu]),
    CONSTRAINT [FK_CongThuc_SanPham] FOREIGN KEY([maSanPham]) REFERENCES [dbo].[SanPham] ([maSanPham]) ON DELETE CASCADE
);
-- =============================================
-- 6. SERVICE PROMOTION
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'SerivcePromotion')
CREATE DATABASE [SerivcePromotion];
GO
USE [SerivcePromotion]
GO
CREATE TABLE [dbo].[KhuyenMai](
	[ma_khuyen_mai] [varchar](20) NOT NULL PRIMARY KEY,
	[ten_khuyen_mai] [nvarchar](100) NULL,
	[mo_ta] [nvarchar](255) NULL,
	[loai_khuyen_mai] [varchar](255) NULL,
	[gia_tri] [float] NULL,
	[trang_thai] [bit] DEFAULT (1),
	[mau_sac] [varchar](255) NULL,
	[ngay_tao] [datetime2](7) DEFAULT (getdate())
);
GO

-- =============================================
-- 7. SERVICE SALARY
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ServiceSalary')
CREATE DATABASE [ServiceSalary];
GO
USE [ServiceSalary]
GO
CREATE TABLE [dbo].[ChamCong](
	[maChamCong] [varchar](20) NOT NULL PRIMARY KEY,
	[maNhanVien] [varchar](20) NULL,
	[maCa] [varchar](20) NULL,
	[thoiGianVao] [datetime] NULL,
	[thoiGianRa] [datetime] NULL,
	[soGioLam] [float] NULL,
	[trangThai] [nvarchar](20) NULL
);
GO

-- =============================================
-- 8. SERVICE STORE
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ServiceStore')
CREATE DATABASE [ServiceStore];
GO
USE [ServiceStore]
GO
CREATE TABLE [dbo].[NguyenLieu](
	[maNguyenLieu] [varchar](20) NOT NULL PRIMARY KEY,
	[donViTinh] [nvarchar](20) NULL,
	[soLuong] [float] NULL,
	[tenNguyenLieu] [nvarchar](100) NULL
);
GO

-- =============================================
-- 9. SERVICE BAN (TABLE)
-- =============================================
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'ServiceBan')
CREATE DATABASE [ServiceBan];
GO
USE [ServiceBan]
GO
CREATE TABLE [dbo].[khuvuc](
	[maKhuVuc] [varchar](50) NOT NULL PRIMARY KEY,
	[tenKhuVuc] [nvarchar](255) NOT NULL,
	[trangThai] [nvarchar](255) NOT NULL
);
CREATE TABLE [dbo].[ban](
	[maBan] [varchar](50) NOT NULL PRIMARY KEY,
	[maKhuVuc] [varchar](50) REFERENCES [khuvuc]([maKhuVuc]),
	[trangThaiBan] [nvarchar](255) DEFAULT (N'Hoạt động'),
	[tenBan] [nvarchar](255) NOT NULL,
	[trangThaiThanhToan] [nvarchar](50) DEFAULT (N'PAID')
);
GO
INSERT [dbo].[khuvuc] VALUES (N'TANG1', N'Tầng 1', N'Sẵn sàng')
INSERT [dbo].[ban] VALUES (N'BAN101', N'TANG1', N'Hoạt động', N'Bàn 101', N'PENDING')
GO