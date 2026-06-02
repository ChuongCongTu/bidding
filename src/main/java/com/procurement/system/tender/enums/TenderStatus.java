package com.procurement.system.tender.enums;

public enum TenderStatus {
    NEW, // Mới tạo gói thầu
    INIT_MT, // Đang lập HSMT (Hồ Sơ Mời Thầu)
    PUB_MT, // Đã phát hành HSMT / đăng TBMT
    OPEN_BID, // đã mở thầu
    PUB_KQLCNT, // Công bố kết quả lựa chọn nhà thầu
    CANCEL_BID // Hủy gói thầu
}
