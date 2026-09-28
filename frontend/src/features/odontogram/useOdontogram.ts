import { useCallback, useEffect, useState } from 'react'
import { getConsultationOdontogram, getCurrentOdontogram, getPieceHistory } from './odontogramApi'
import type { OdontogramEvent, OdontogramMode, OdontogramPiece } from './types'

export function useOdontogram(recordId:number|undefined,mode:OdontogramMode){
 const [pieces,setPieces]=useState<OdontogramPiece[]>([]);const [loading,setLoading]=useState(true);const [error,setError]=useState('');const [history,setHistory]=useState<Record<string,OdontogramEvent[]>>({})
 const [reloadKey, setReloadKey] = useState(0);
 const consultationId=mode.kind==='historical'?mode.consultationId:undefined
 useEffect(()=>{if(!recordId)return;let active=true;Promise.all([getCurrentOdontogram(recordId),consultationId?getConsultationOdontogram(recordId,consultationId):Promise.resolve(null)]).then(([catalog,historical])=>{if(!active)return;const eventsByPiece=new Map(historical?.map(event=>[event.pieceCode,event]));setPieces(catalog.map(piece=>({...piece,currentEvent:historical?eventsByPiece.get(piece.pieceCode)??null:piece.currentEvent}))) }).catch(()=>{if(active)setError('No fue posible cargar el odontograma.')}).finally(()=>{if(active)setLoading(false)});return()=>{active=false}},[recordId,consultationId,reloadKey])
 const loadHistory=useCallback(async(pieceCode:string)=>{if(!recordId)return;try{const events=await getPieceHistory(recordId,pieceCode);setHistory(current=>({...current,[pieceCode]:events}))}catch{setError('No fue posible cargar el historial de la pieza.')}},[recordId,reloadKey])
 const reload = useCallback(() => setReloadKey(k => k + 1), []);
 return{pieces,loading,error,history,loadHistory,reload}
}
