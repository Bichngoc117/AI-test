import pandas as pd

test_cases = [
    # A. Truy cập & Khởi tạo
    ["MINSP_005_TC_001", "Access & Init", "Truy cập từ Home - User hợp lệ", "User được assign, SQL >= 1 record", "Click '点検開始' từ MOB-002", "N/A", "Vào màn MOB-010, focus Master Room", "High"],
    ["MINSP_005_TC_002", "Access & Init", "Truy cập - User không hợp lệ", "User không được assign", "Thử truy cập vào plan", "N/A", "Redirect về Home MOB-002", "High"],
    ["MINSP_005_TC_003", "Access & Init", "Tiếp tục kiểm tra (点検再開)", "Đã nhập dở ở phòng A", "Click '点検再開'", "N/A", "Vào đúng phòng A, giữ nguyên dữ liệu đã nhập", "High"],
    
    # B. UI & Navigation
    ["MINSP_005_TC_004", "UI & Nav", "Sidebar - Mở rộng", "Sidebar đang thu gọn", "Click '>'", "N/A", "Mở rộng, hiện tên step, phòng", "Low"],
    ["MINSP_005_TC_005", "UI & Nav", "Sidebar - Thu gọn", "Sidebar đang mở rộng", "Click '<'", "N/A", "Thu gọn, chỉ hiện số thứ tự (①)", "Low"],
    ["MINSP_005_TC_006", "UI & Nav", "Phòng hoàn thành", "Nhập đủ field của tất cả step trong 1 phòng", "Quan sát UI tab phòng", "N/A", "Tab có background xanh nhạt + icon check", "Medium"],
    
    # C. Master Room - Chung
    ["MINSP_005_TC_007", "Master Room", "Thông tin Header", "Đang ở Master Room", "Kiểm tra Plan title, thời gian, tên user, thời tiết", "N/A", "Hiển thị chính xác theo DB", "Medium"],
    ["MINSP_005_TC_008", "Master Room", "Danh sách thiết bị mượn", "User có mượn thiết bị", "Cuộn đến 測定器・試験器一覧表", "N/A", "Hiện đủ: Tên, 型式, Hãng, Ngày SX, Serial", "Low"],
    ["MINSP_005_TC_009", "Master Room", "Không có thiết bị mượn", "User KHÔNG mượn thiết bị", "Cuộn đến 測定器・試験器一覧表", "N/A", "Ẩn toàn bộ phần danh sách thiết bị", "Low"],

    # D. Master Room - Field Validation
    # meter_reading_date
    ["MINSP_005_TC_010", "Master Room - Fields", "検針日 - Tự động tính", "Có last_time = 03/15, tháng hiện tại = 04", "Kiểm tra field 検針日", "N/A", "Tự động điền 04/15", "High"],
    ["MINSP_005_TC_011", "Master Room - Fields", "検針日 - Không có last_time", "Không có last_time", "Kiểm tra field 検針日", "N/A", "Trống, cho phép mở lịch chọn", "High"],
    ["MINSP_005_TC_012", "Master Room - Fields", "検針日 - BVA Chọn ngày > today", "Mở calendar", "Chọn ngày tương lai", "Ngày mai", "Không cho phép chọn / báo lỗi", "High"],
    ["MINSP_005_TC_013", "Master Room - Fields", "検針日 - BVA Chọn ngày < last_time", "Có last_time = 04/15", "Chọn ngày 04/10", "04/10", "Không cho phép chọn / báo lỗi", "Medium"],
    ["MINSP_005_TC_014", "Master Room - Fields", "検針日 - Bỏ trống (Required)", "Đã có dữ liệu", "Xóa trắng và focus out", "Trống", "Viền đỏ báo lỗi required", "Critical"],

    # chk_hv_panel_demand_setpoint_num
    ["MINSP_005_TC_015", "Master Room - Fields", "デマンド指示値 - Bỏ trống (Required)", "N/A", "Bỏ trống, focus out", "Trống", "Viền đỏ báo lỗi required", "Critical"],
    ["MINSP_005_TC_016", "Master Room - Fields", "デマンド指示値 - EP Hợp lệ", "N/A", "Nhập số thập phân hợp lệ", "123.456", "Lưu thành công, background vàng, format (5,3)", "High"],
    ["MINSP_005_TC_017", "Master Room - Fields", "デマンド指示値 - BVA Min/Max", "N/A", "Nhập < 0 hoặc > 99999", "-1, 100000", "Báo lỗi input range, clear giá trị", "High"],

    # Power Factor (力率)
    ["MINSP_005_TC_018", "Master Room - Fields", "力率 - Tự động tính (Happy)", "N/A", "Nhập P1=100, P2=50", "P1=100, P2=50", "Tự tính theo công thức, làm tròn số nguyên", "Critical"],
    ["MINSP_005_TC_019", "Master Room - Fields", "力率 - Chia cho 0", "N/A", "Nhập P1=0, P2=0", "P1=0, P2=0", "Hiển thị 0%", "High"],

    # Energy usage fields (1, P1, P2, 2, 3)
    ["MINSP_005_TC_020", "Master Room - Fields", "使用電力量1 - EP Hợp lệ", "N/A", "Nhập hợp lệ", "1234.567", "Lưu thành công, background vàng, format (6,3)", "High"],
    ["MINSP_005_TC_021", "Master Room - Fields", "使用電力量1 - BVA Out of range", "N/A", "Nhập > 999999", "1000000", "Báo lỗi input range, clear giá trị", "High"],
    ["MINSP_005_TC_022", "Master Room - Fields", "総使用量 - Tự động tính", "Có last time", "Nhập Sử dụng điện lượng 1", "This=100, Last=40", "Tự tính 100 - 40 = 60", "High"],

    # E. Master Room - High Voltage Panel (Voltage & Amp)
    ["MINSP_005_TC_023", "Master Room - Panel", "電圧(V) R-S/S-T/T-R - EP Hợp lệ", "N/A", "Nhập trong khoảng 5000-8000", "6500", "Lưu thành công, không số thập phân", "High"],
    ["MINSP_005_TC_024", "Master Room - Panel", "電圧(V) - BVA Dưới Min", "N/A", "Nhập 4999", "4999", "Báo lỗi range, clear input", "High"],
    ["MINSP_005_TC_025", "Master Room - Panel", "電圧(V) - BVA Trên Max", "N/A", "Nhập 8001", "8001", "Báo lỗi range, clear input", "High"],
    ["MINSP_005_TC_026", "Master Room - Panel", "電流(A) R/S/T - EP Hợp lệ", "N/A", "Nhập 0-600 format (3,2)", "300.55", "Lưu thành công", "High"],
    ["MINSP_005_TC_027", "Master Room - Panel", "電流(A) - BVA Out of range", "N/A", "Nhập -1, 601", "-1, 601", "Báo lỗi range, clear input", "High"],

    # F. Site Location - Modal Điều hướng
    ["MINSP_005_TC_028", "Site Location - Modal", "Mở modal nhập liệu", "Đang ở 1 phòng, tab 受電設備", "Click vào 1 field (vd: 温度)", "N/A", "Mở modal, hiển thị đúng dữ liệu device", "High"],
    ["MINSP_005_TC_029", "Site Location - Modal", "Chuyển Group trong Modal", "Trong modal", "Click nút < hoặc > (Voltage -> Amp -> Temp)", "N/A", "Chuyển đúng màn hình của từng group", "Medium"],
    ["MINSP_005_TC_030", "Site Location - Modal", "Chuyển Equipment (nhiều bản ghi)", "Có > 1 equipment", "Click Lên/Xuống", "N/A", "Chuyển sang thiết bị khác", "Medium"],
    ["MINSP_005_TC_031", "Site Location - Modal", "Đóng modal (Click outside)", "Trong modal", "Click ra vùng tối bên ngoài", "N/A", "Đóng modal, dữ liệu đã nhập truyền ra bảng ngoài", "High"],

    # G. Site Location - Field Validation (Transformer & Other HV)
    ["MINSP_005_TC_032", "Site Location - Fields", "温度 - EP/BVA (0-200)", "Trong modal", "Nhập -1, 100.5, 201", "-1, 100.5, 201", "100.5 hợp lệ; -1 và 201 lỗi range", "High"],
    ["MINSP_005_TC_033", "Site Location - Fields", "負荷率 - EP/BVA (0-100)", "Trong modal", "Nhập -1, 50.25, 101", "-1, 50.25, 101", "50.25 hợp lệ; -1 và 101 lỗi range", "High"],
    ["MINSP_005_TC_034", "Site Location - Fields", "漏れ電流 - EP/BVA (0-9999)", "Trong modal", "Nhập -1, 5000.5, 10000", "-1, 5000.5, 10000", "5000.5 hợp lệ; lỗi với out of range", "High"],
    ["MINSP_005_TC_035", "Site Location - Fields", "電圧(V) Site - EP/BVA (50-3600)", "Trong modal", "Nhập 49, 200, 3601", "49, 200, 3601", "200 hợp lệ; lỗi range", "High"],
    ["MINSP_005_TC_036", "Site Location - Fields", "電流(A) Site - EP/BVA (0-3000)", "Trong modal", "Nhập -1, 1500.5, 3001", "-1, 1500.5, 3001", "1500.5 hợp lệ; lỗi range", "High"],
    ["MINSP_005_TC_037", "Site Location - Fields", "Ẩn/Hiện nhóm thiết bị", "Không có thiết bị Transformer/Condenser", "Xem UI bảng", "N/A", "Nếu không có sẽ hiện dòng 該当なし hoặc ẩn cụm", "Medium"],

    # H. Chức năng Sync (Upload/Download)
    ["MINSP_005_TC_038", "Sync", "Download - Bấm hủy", "Đang ở phòng A", "Click Download -> Chọn NO", "N/A", "Đóng popup, không tải gì", "Low"],
    ["MINSP_005_TC_039", "Sync", "Download - Có mạng, Đồng ý", "Đang ở phòng A, có sửa đổi local", "Click Download -> Chọn YES", "N/A", "Tải DB đè lên local, hiện Toast thành công, mất màu vàng (đã sync)", "Critical"],
    ["MINSP_005_TC_040", "Sync", "Download - Mất mạng", "Tắt Wifi", "Click Download", "N/A", "Hiện Toast báo lỗi offline, không hiện popup hỏi", "High"],
    ["MINSP_005_TC_041", "Sync", "Upload - Bấm hủy", "Có dữ liệu local", "Click Upload -> Chọn NO", "N/A", "Đóng popup, không gửi gì", "Low"],
    ["MINSP_005_TC_042", "Sync", "Upload - Có mạng, Đồng ý", "Có dữ liệu local", "Click Upload -> Chọn YES", "N/A", "Gửi dữ liệu lên DB, Toast thành công, update Last Sync Time", "Critical"],
    ["MINSP_005_TC_043", "Sync", "Upload - Mất mạng", "Tắt Wifi", "Click Upload", "N/A", "Hiện Toast báo lỗi offline", "High"],

    # I. Submit & Complete Inspection
    ["MINSP_005_TC_044", "Complete", "Nút Submit (顧客確認へ) - Enable", "Chưa nhập đủ data", "Kiểm tra nút Submit", "N/A", "Default nút vẫn enable nhưng khi click sẽ báo lỗi", "Medium"],
    ["MINSP_005_TC_045", "Complete", "Submit - Báo lỗi do thiếu Required Fields", "Thiếu 1 field ở phòng phụ", "Click 顧客確認へ", "N/A", "Báo lỗi E-MSG, focus vào field đang thiếu, không chuyển trang", "Critical"],
    ["MINSP_005_TC_046", "Complete", "Submit - Thành công", "Nhập đủ TẤT CẢ các phòng", "Click 顧客確認へ", "N/A", "Update DB, tạo Report Code HJS..., chuyển sang MOB-013", "Critical"],
    ["MINSP_005_TC_047", "Complete", "Submit - Offline Mode", "Tắt mạng", "Click 顧客確認へ", "N/A", "Nút bấm được nhưng hiện Toast báo offline (Chỉ online mới submit được)", "High"],
    ["MINSP_005_TC_048", "Complete", "Submit - Check Report Creation", "Chưa có report cho inspection này", "Submit thành công", "N/A", "Tạo record bảng Report, Code = HJS + id padding 10 số, status = 1-IS_DRAFT", "Critical"],
    ["MINSP_005_TC_049", "Complete", "Submit - Lỗi Insert Report", "Giả lập lỗi DB khi insert", "Submit thành công bước 1", "N/A", "Báo lỗi E-MSG-004, không chuyển trang, chờ thử lại", "High"]
]

columns = ["TC ID", "Module", "Test Scenario", "Pre-Condition", "Test Steps", "Test Data", "Expected Result", "Priority"]

df = pd.DataFrame(test_cases, columns=columns)
csv_file_path = r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\testcases_MINSP_005_Annual_Shutdown_Detailed.csv'

# Save to CSV with utf-8-sig encoding for Excel to read Vietnamese properly
df.to_csv(csv_file_path, index=False, encoding='utf-8-sig')

print(f"Generated detailed test cases at: {csv_file_path}")
