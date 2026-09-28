const fs = require('fs');
const xlsx = require('xlsx');
const workbook = xlsx.readFile('../CIE10.xls');
const sheet = workbook.Sheets[workbook.SheetNames[0]];
const rows = xlsx.utils.sheet_to_json(sheet, {header: 1}).slice(1);
let sql = 'DELETE FROM icd10_codes;\nINSERT INTO icd10_codes (code, description) VALUES\n';
const values = rows.filter(r => r[1] && r[2]).map(r => {
    const code = r[1].toString().replace(/'/g, "''");
    const desc = r[2].toString().replace(/'/g, "''").substring(0, 255);
    return `('${code}', '${desc}')`;
});
sql += values.join(',\n') + ';\n';
fs.writeFileSync('../database/migrations/V11__fix_icd10_codes.sql', sql);
console.log("Migration generated.");
