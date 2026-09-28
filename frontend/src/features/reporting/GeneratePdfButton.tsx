import { lazy, Suspense, useRef, useState } from 'react'
import { getCurrentOdontogram } from '../odontogram/odontogramApi'
import type { OdontogramPiece } from '../odontogram/types'
import { generateClinicalPdf } from './clinicalPdfApi'
import './reporting.css'

const PdfOdontogramCapture=lazy(()=>import('./PdfOdontogramCapture').then(module=>({default:module.PdfOdontogramCapture})))

export function GeneratePdfButton({recordId,recordNumber}:{recordId:number;recordNumber:string}){
 const [pieces,setPieces]=useState<OdontogramPiece[]>([]);const [working,setWorking]=useState(false);const [error,setError]=useState('');const canvas=useRef<HTMLCanvasElement|null>(null)
 const generate=async()=>{setWorking(true);setError('');try{const data=await getCurrentOdontogram(recordId);setPieces(data);await new Promise(resolve=>setTimeout(resolve,1200));if(!canvas.current)throw new Error('Canvas no disponible');const image=await new Promise<Blob>((resolve,reject)=>canvas.current?.toBlob(blob=>blob?resolve(blob):reject(new Error('Captura inválida')),'image/png'));const pdf=await generateClinicalPdf(recordId,image);const url=URL.createObjectURL(pdf);const link=document.createElement('a');link.href=url;link.download=`historia-clinica-${recordNumber}.pdf`;link.click();setTimeout(()=>URL.revokeObjectURL(url),1000)}catch{setError('No fue posible generar el PDF clínico.')}finally{setWorking(false)}}
 return <><button className="btn btn-outline-primary" onClick={generate} disabled={working}>{working?'Generando PDF…':'Generar PDF / Imprimir'}</button>{error&&<span className="pdf-error" role="alert">{error}</span>}{pieces.length>0&&<Suspense fallback={null}><PdfOdontogramCapture pieces={pieces} onReady={value=>{canvas.current=value}}/></Suspense>}</>
}
