import pandas as pd
import json

template_file = r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\TestCase.xlsx'
output = {}

def default_converter(o):
    import datetime
    if isinstance(o, (datetime.date, datetime.datetime)):
        return o.isoformat()
    return str(o)

try:
    xls = pd.ExcelFile(template_file)
    for sheet in xls.sheet_names:
        df = pd.read_excel(xls, sheet_name=sheet)
        # Drop completely empty columns and rows
        df = df.dropna(how='all', axis=1).dropna(how='all', axis=0)
        # Fill NaN with empty string
        df = df.fillna("")
        # Get first 15 rows
        output[sheet] = df.head(15).to_dict(orient='records')
except Exception as e:
    output["error"] = str(e)

with open(r'c:\Users\Dell 5540\Downloads\antigravity-testing-kit\scratch_template_rows.json', 'w', encoding='utf-8') as f:
    json.dump(output, f, ensure_ascii=False, indent=2, default=default_converter)

print("Saved template rows to scratch_template_rows.json")
