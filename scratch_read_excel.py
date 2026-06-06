import pandas as pd
import json
import sys

sys.stdout.reconfigure(encoding='utf-8')

rezil_file = r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\REZIL - Basic Design Mobile.xlsx'
template_file = r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\TestCase.xlsx'

result = {"rezil": {}, "template": {}}

try:
    rezil_xls = pd.ExcelFile(rezil_file)
    for sheet in rezil_xls.sheet_names:
        df = pd.read_excel(rezil_xls, sheet_name=sheet)
        # Drop empty columns and rows
        df = df.dropna(how='all', axis=1).dropna(how='all', axis=0)
        result["rezil"][sheet] = df.head(100).to_dict(orient='records')
except Exception as e:
    result["rezil_error"] = str(e)

try:
    template_xls = pd.ExcelFile(template_file)
    for sheet in template_xls.sheet_names:
        df = pd.read_excel(template_xls, sheet_name=sheet)
        result["template"][sheet] = df.columns.tolist()
except Exception as e:
    result["template_error"] = str(e)

with open(r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\scratch_output.json', 'w', encoding='utf-8') as f:
    json.dump(result, f, ensure_ascii=False, indent=2)

print("Saved to scratch_output.json")
