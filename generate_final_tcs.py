import pandas as pd

columns = [
    "NO", "TC No.", "Check Object 1", "Check Object 2", "Check content", 
    "Pre-condition", "Test Data", "Steps", "Expected Result", 
    "Test IT Result", "Executed Date", "SQA", "Core IT", "Detail IT", 
    "Evidence", "Test UT Result", "Status fix bug", "Executed Date DEV", "DEV", "Note (DefectID, Actual result)"
]

tcs = []
tc_count = 1

def add_tc(obj1, obj2, content, precond, test_data, steps, expected):
    global tc_count
    row = [
        "", tc_count, obj1, obj2, content, 
        precond, test_data, steps, expected,
        "", "", "SQA_Name", "", "", "", "", "", "", "", ""
    ]
    tcs.append(row)
    tc_count += 1


# TC 1: SQL Data Init for Master Room
sql_query = """SELECT p.work_start, p.weather_information,
i.chk_hv_panel_demand_setpoint_num, 
i.chk_hv_panel_power_factor_rate, 
i.chk_meter_replaced_flag,                                                                        
i.chk_energy_usage1_num,                                                                        
i.chk_active_energy_p1_num,                                                                        
i.chk_reactive_energy_p2_num,                                                                        
i.chk_energy_usage2_num,                                                                        
i.chk_energy_usage3_num,                                                                                                                                                                                                        
i.chk_voltage_rs_num,                                                                        
i.chk_voltage_st_num,                                                                        
i.chk_voltage_tr_num,                                                                        
i.chk_amp_r_num,                                                                        
i.chk_amp_s_num,                                                                        
i.chk_amp_t_num                                                                                                                                                                

FROM inspection i                                                                                                                                                                                
        INNER JOIN plan p ON i.id = p.inspection_id                                                                                                                                                                        
WHERE i.id = :current_inspection_id                                                                                                                                                                                
        AND p.state = 11-IS_REPORT_IN_PROGRESS;"""

add_tc(
    "取引電力計 Tab", "Default value", "Kiểm tra default khi open MH MOB-010", 
    "1. Có data trong database cho current_inspection_id", 
    "N/A", 
    "1. Open app\n2. Thực hiện login với role Engineer\n3. Tại màn hình MOB-002 Home, click button 点検開始 ở Inspection Plan", 
    f"1. Hiển thị overlay loading data. Tab 取引電力計 được hiển thị data\n2. Thông tin this time hiển thị data từ câu SQL sau:\n{sql_query}"
)

# TC 2: Click button sidebar step 2 ② 絶縁監視装置
add_tc(
    "sidebar_step_2", "Item type", "Kiểm tra thao tác click", 
    "Đang ở vùng step ① 受電状況", "N/A", 
    "1. Click button sidebar step 2 ② 絶縁監視装置", 
    "1. Scroll về vùng step ② 絶縁監視装置. Item sidebar được highlight trạng thái active."
)

# TC 3: Check UI and data for chk_low_voltage_wiring_devices
add_tc(
    "chk_low_voltage_wiring_devices", "Default value", "Kiểm tra UI/Activity/Data từng item", 
    "Có data trong BD", "N/A", 
    "1. Kiểm tra loại item\n2. Confirm hiển thị", 
    "1. Item được hiển thị đúng kiểu là Label/Text\nText là 低圧配線・配線器具\n2. Data get từ field: \ninspection_location.chk_low_voltage_wiring_devices\nHiện thị 4 lựa chọn: \ncase IS_BAD  (-1, \"✕\")\ncase IS_NOT_APPLICABLE ( 0, \"-\")\ncase IS_WARNING  ( 1, \"△\")\ncase IS_GOOD ( 2, \"◯\")\n\nDefault chọn \"ー\" IS_NOT_APPLICABLE"
)

# TC 4: Add the issue state logic
add_tc(
    "issue_btn", "Item type", "Kiểm tra loại item\nConfirm hiển thị số lượng issue và state background", 
    "Có data trong BD", "N/A", 
    "1. Kiểm tra loại item\n2. Confirm hiển thị số lượng issue\n3. Kiểm tra issue.state", 
    "1. Hiển thị dạng button 指摘事項 (<số lượng>)\n2. Hiển thị tổng số lượng inspection_issue hiện tại trong lần inspection này\n3. Nếu issue.state IN (99-IS_INVALID, 4-IS_COMPLETION_CONFIRMED) thì issue_item hiển thị background xám. Ngược lại hiển thị background đỏ/vàng tuỳ severity."
)

# Convert to DataFrame
df = pd.DataFrame(tcs, columns=columns)
output_path = r"c:\Users\Dell 5540\Downloads\antigravity-testing-kit\testcases_MINSP_005_Annual_Shutdown_Updated.csv"
df.to_csv(output_path, index=False, encoding='utf-8-sig')
print(f"Generated {len(df)} exact requested test cases.")
