import openpyxl

def read_excel_to_file(file_path, out_file):
    wb = openpyxl.load_workbook(file_path, data_only=True)
    with open(out_file, 'w', encoding='utf-8') as f:
        for sheet in wb.worksheets:
            f.write(f"--- Sheet: {sheet.title} ---\n")
            for row in sheet.iter_rows(values_only=True):
                # filter out rows that are completely None
                if all(cell is None for cell in row):
                    continue
                # convert cell to string and handle None
                row_str = " | ".join([str(cell).strip().replace('\n', ' ') if cell is not None else "" for cell in row])
                f.write(row_str + "\n")
            f.write("\n")

req_path = r"c:\Users\Dell 5540\Downloads\antigravity-testing-kit\REZIL - Basic Design Mobile.xlsx"
req_out = r"c:\Users\Dell 5540\Downloads\antigravity-testing-kit\REZIL_req.txt"
tc_path = r"c:\Users\Dell 5540\Downloads\antigravity-testing-kit\TestCase.xlsx"
tc_out = r"c:\Users\Dell 5540\Downloads\antigravity-testing-kit\TestCase_template.txt"

read_excel_to_file(req_path, req_out)
read_excel_to_file(tc_path, tc_out)
print("Done extracting to txt")
