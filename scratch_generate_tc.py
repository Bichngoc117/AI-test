import pandas as pd
import csv

# Define the data for the test cases
test_cases = [
    ["MINSP_005_TC_001", "MINSP-005 Annual Shutdown", "Khởi tạo - Truy cập màn hình thực thi kiểm tra hợp lệ", "User đăng nhập thuộc list assign, query SQL trả về >= 1 record", "1. Từ MOB-002 HOME, click '点検開始' tại plan MINSP-005\n2. Kiểm tra màn hình được hiển thị", "N/A", "1. Chuyển hướng thành công đến màn hình thực thi (MOB-010)\n2. Hiển thị đúng phòng Master (取引電力計) làm tab đầu tiên", "High"],
    ["MINSP_005_TC_002", "MINSP-005 Annual Shutdown", "Khởi tạo - Truy cập màn hình thất bại do không có quyền", "User đăng nhập KHÔNG thuộc list assign, hoặc SQL trả về 0 record", "1. Truy cập trực tiếp url hoặc giả lập query = 0\n2. Kiểm tra phản hồi của hệ thống", "N/A", "1. Hệ thống không cho phép truy cập\n2. Redirect về màn hình MOB-002 HOME", "High"],
    ["MINSP_005_TC_003", "MINSP-005 Annual Shutdown", "Khởi tạo - Tiếp tục kiểm tra (点検再開)", "Đã có dữ liệu nhập dở ở một phòng trước đó", "1. Từ MOB-002 HOME, click '点検再開'\n2. Kiểm tra phòng và dữ liệu hiển thị", "N/A", "1. Tự động chuyển đến đúng phòng (tab) trước đó\n2. Dữ liệu đã nhập được giữ nguyên", "High"],
    
    ["MINSP_005_TC_004", "MINSP-005 Annual Shutdown", "Sidebar - Thu gọn và mở rộng (State Transition)", "Đang ở màn hình thực thi kiểm tra", "1. Click nút 閉じる (<)\n2. Kiểm tra sidebar\n3. Click nút 開く (>)\n4. Kiểm tra sidebar", "N/A", "1. Sidebar thu gọn, chỉ hiện số thứ tự step\n2. Sidebar mở rộng, hiện đầy đủ tên step", "Low"],
    
    ["MINSP_005_TC_005", "MINSP-005 Annual Shutdown", "Master Room - Kiểm tra thông tin cố định", "Đang ở tab 取引電力計", "1. Kiểm tra các thông tin: plan_title, work_start, execute_incharge, thời tiết\n2. Kiểm tra list borrowed_equipment", "N/A", "1. Hiển thị đúng thông tin của plan, user và thời tiết site\n2. Hiển thị đúng danh sách thiết bị mượn của engineer", "Medium"],
    
    ["MINSP_005_TC_006", "MINSP-005 Annual Shutdown", "Master Room - Field Validation (meter_reading_date)", "Đang ở màn hình Master Room", "1. Focus vào trường 検針日\n2. Để trống và focus_out\n3. Chọn ngày hợp lệ (<= today)", "Date: today", "1. Hiện error required (viền đỏ)\n2. Dữ liệu được lưu, background đổi màu vàng", "High"],
    ["MINSP_005_TC_007", "MINSP-005 Annual Shutdown", "Master Room - Field Validation (chk_hv_panel_demand_setpoint_num) - EP/BVA", "Đang ở màn hình Master Room", "1. Nhập giá trị < 0\n2. Nhập giá trị > 99999\n3. Nhập giá trị hợp lệ (vd: 100.123)", "-1, 100000, 100.123", "1&2. Clear giá trị, hiện error input range\n3. Giá trị lưu thành công, format (5,3)", "High"],
    ["MINSP_005_TC_008", "MINSP-005 Annual Shutdown", "Master Room - Auto Calculate (chk_hv_panel_power_factor_rate)", "Đang ở màn hình Master Room", "1. Nhập P1 = 100, P2 = 0\n2. Nhập P1 = 0, P2 = 0\n3. Kiểm tra 力率", "P1=100, P2=0; P1=0, P2=0", "1. Tính toán ra 100%\n2. Tính toán ra 0% (xử lý chia cho 0)\n3. Kết quả làm tròn đến hàng đơn vị", "High"],

    ["MINSP_005_TC_009", "MINSP-005 Annual Shutdown", "Site Location - Nhập dữ liệu (Modal) - Happy Path", "Đang ở tab một phòng cụ thể (site_location)", "1. Click vào field nhập số (vd: 温度)\n2. Nhập dữ liệu hợp lệ trên bàn phím số\n3. Điều hướng group (</>)\n4. Đóng modal", "Nhiệt độ: 30", "1. Hiển thị Modal nhập liệu với 3 group\n2. Nhập thành công\n3. Chuyển group đúng vòng lặp\n4. Dữ liệu map xuống bảng ngoài", "High"],
    ["MINSP_005_TC_010", "MINSP-005 Annual Shutdown", "Site Location - Field Validation (chk_temperature_num) - EP/BVA", "Mở modal nhập của 受電設備", "1. Nhập giá trị < 0\n2. Nhập giá trị > 200\n3. Nhập giá trị 100.5", "-1, 201, 100.5", "1&2. Clear giá trị, hiện error input range\n3. Lưu thành công, format (3,1)", "High"],
    ["MINSP_005_TC_011", "MINSP-005 Annual Shutdown", "Site Location - Field Validation (chk_voltage_rs_num) - EP/BVA", "Mở modal nhập của 受電設備", "1. Nhập giá trị < 50\n2. Nhập giá trị > 3600\n3. Nhập giá trị 200", "49, 3601, 200", "1&2. Clear giá trị, hiện error input range\n3. Lưu thành công", "High"],
    ["MINSP_005_TC_012", "MINSP-005 Annual Shutdown", "Site Location - Hoàn thành Step và Room", "Đang ở tab site_location", "1. Nhập toàn bộ required field của step 1\n2. Nhập toàn bộ required field của step 2 (nếu có)", "Valid data cho tất cả required fields", "1. Sidebar chuyển trạng thái step DONE (hiện icon checked)\n2. Toàn bộ step DONE -> Tab phòng chuyển background xanh nhạt + checked", "Critical"],

    ["MINSP_005_TC_013", "MINSP-005 Annual Shutdown", "Sync Data - Download (Offline & Online)", "Hoàn thành một phần dữ liệu", "1. Tắt mạng (Offline) -> click Download\n2. Bật mạng (Online) -> click Download -> chọn YES\n3. Kiểm tra dữ liệu", "N/A", "1. Nút clickable nhưng hiện toast báo lỗi offline\n2. Hiện Toast 'データのダウンロードが完了しました'\n3. Dữ liệu local bị ghi đè, sync_datetime cập nhật", "High"],
    ["MINSP_005_TC_014", "MINSP-005 Annual Shutdown", "Sync Data - Upload (Offline & Online)", "Hoàn thành một phần dữ liệu", "1. Tắt mạng (Offline) -> click Upload\n2. Bật mạng (Online) -> click Upload -> chọn YES\n3. Kiểm tra dữ liệu trên server", "N/A", "1. Hiện toast báo lỗi offline\n2. Hiện Toast 'データのアップロードが完了しました'\n3. Dữ liệu local đẩy lên server, sync_datetime cập nhật", "High"],
    
    ["MINSP_005_TC_015", "MINSP-005 Annual Shutdown", "Complete Inspection - Submit thành công", "Đã nhập đủ required data tất cả các phòng", "1. Kiểm tra trạng thái btn '顧客確認へ'\n2. Click '顧客確認へ'\n3. Chờ xử lý", "N/A", "1. Nút enable\n2. Hệ thống tạo record report (HJS...)\n3. Chuyển sang màn hình MOB-013 thành công", "Critical"],
    ["MINSP_005_TC_016", "MINSP-005 Annual Shutdown", "Complete Inspection - Submit thất bại (Thiếu field)", "Có 1 phòng chưa nhập đủ required data", "1. Click '顧客確認へ'\n2. Kiểm tra focus và error", "N/A", "1. Validation failed\n2. Không chuyển màn hình, hiện popup/toast lỗi, focus vào field thiếu", "High"]
]

columns = ["TC ID", "Module", "Test Scenario", "Pre-Condition", "Test Steps", "Test Data", "Expected Result", "Priority"]

df = pd.DataFrame(test_cases, columns=columns)
csv_file_path = r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\testcases_MINSP_005_Annual_Shutdown.csv'

# Save to CSV with utf-8-sig encoding for Excel to read Vietnamese properly
df.to_csv(csv_file_path, index=False, encoding='utf-8-sig')

print(f"Generated test cases at: {csv_file_path}")
