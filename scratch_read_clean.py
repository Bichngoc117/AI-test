import pandas as pd

rezil_file = r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\REZIL - Basic Design Mobile.xlsx'
template_file = r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\TestCase.xlsx'

with open(r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\scratch_clean_output.txt', 'w', encoding='utf-8') as f:
    f.write("=== REZIL EXCEL REQUIREMENTS ===\n")
    xls = pd.ExcelFile(rezil_file)
    for sheet_name in xls.sheet_names:
        f.write(f"\n--- Sheet: {sheet_name} ---\n")
        df = pd.read_excel(xls, sheet_name=sheet_name)
        for index, row in df.iterrows():
            row_vals = [str(val).strip() for val in row if pd.notna(val) and str(val).strip()]
            if row_vals:
                f.write(" | ".join(row_vals) + "\n")

    f.write("\n=== TEST CASE TEMPLATE COLUMNS ===\n")
    xls_temp = pd.ExcelFile(template_file)
    for sheet_name in xls_temp.sheet_names:
        f.write(f"\n--- Sheet: {sheet_name} ---\n")
        df = pd.read_excel(xls_temp, sheet_name=sheet_name)
        f.write("Columns: " + ", ".join(df.columns.tolist()) + "\n")
