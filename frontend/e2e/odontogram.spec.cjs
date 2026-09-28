const { test, expect } = require('@playwright/test')
const path = require('path')
const fs = require('fs')

const evidence = path.resolve(__dirname, '../../docs/evidencias/odontogram-final')
fs.mkdirSync(evidence, { recursive: true })
const sizes=[{name:'1366x768',width:1366,height:768},{name:'1440x900',width:1440,height:900},{name:'1920x1080',width:1920,height:1080}]

for(const size of sizes)test(`odontograma clínico ${size.name}`,async({page})=>{
 test.setTimeout(60000)
 await page.setViewportSize(size);const consoleErrors=[];page.on('console',message=>{if(message.type()==='error')consoleErrors.push(message.text())})
 await page.goto('http://localhost:5173/login');await page.locator('#username').fill('odontologo');await page.locator('#password').fill('OdontologoDemo2026!');await page.getByRole('button',{name:/Ingresar al sistema/i}).click();await page.waitForURL(/\/historia-clinica/);await page.locator('a[href="/pacientes"]').click();await page.waitForURL(/\/pacientes/)
 await page.getByRole('link',{name:/Ver HCE/i}).first().click();await page.getByRole('link',{name:/Abrir odontograma 3D/i}).click();await expect(page.getByTestId('piece-count')).toContainText('36 piezas');await expect(page.getByTestId('clinical-overview-canvas')).toBeVisible();await page.locator('.academic-extra-strip button').filter({hasText:'TOOTH_36'}).click();await expect(page.getByRole('heading',{name:'Pieza 36'})).toBeVisible();await expect(page.getByText('Restauración').first()).toBeVisible();await page.waitForTimeout(2500);await page.screenshot({path:path.join(evidence,`odontogram-clinical-${size.name}.png`),fullPage:true})
 await page.getByRole('button',{name:/Exploración 3D/i}).click();await expect(page.getByTestId('odontogram-canvas')).toBeVisible();await page.waitForTimeout(1800);await page.screenshot({path:path.join(evidence,`odontogram-exploration-${size.name}.png`),fullPage:true});expect(consoleErrors).toEqual([])
})

test('PDF clínico incorpora captura del odontograma actualizado',async({page})=>{
 test.setTimeout(90000)
 await page.goto('http://localhost:5173/login');await page.locator('#username').fill('odontologo');await page.locator('#password').fill('OdontologoDemo2026!');await page.getByRole('button',{name:/Ingresar al sistema/i}).click();await page.waitForURL(/\/historia-clinica/);await page.locator('a[href="/pacientes"]').click();await page.getByRole('link',{name:/Ver HCE/i}).first().click()
 const downloadPromise=page.waitForEvent('download');await page.getByRole('button',{name:/Generar PDF/i}).click();const download=await downloadPromise;await download.saveAs(path.join(evidence,'historia-clinica-odontogram-final.pdf'));expect(fs.statSync(path.join(evidence,'historia-clinica-odontogram-final.pdf')).size).toBeGreaterThan(10000)
})
