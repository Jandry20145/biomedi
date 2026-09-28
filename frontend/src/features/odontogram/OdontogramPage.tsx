import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link, useParams, useSearchParams } from 'react-router-dom'
import { listConsultations } from '../consultations/consultationApi'
import type { ConsultationSummary } from '../consultations/types'
import { ClinicHeader } from '../patients/ClinicHeader'
import { getClinicalRecord } from '../patients/patientApi'
import type { Patient } from '../patients/types'
import { useClinicalCare } from '../treatment/useClinicalCare'
import { OdontogramAccessibilityList } from './OdontogramAccessibilityList'
import { OdontogramClinicalOverview } from './OdontogramClinicalOverview'
import { OdontogramFilters } from './OdontogramFilters'
import { OdontogramLegend } from './OdontogramLegend'
import { OdontogramScene } from './OdontogramScene'
import { OdontogramToolbar } from './OdontogramToolbar'
import { ToothDetailPanel } from './ToothDetailPanel'
import type { CameraView, OdontogramFilter, OdontogramMode } from './types'
import { useOdontogram } from './useOdontogram'
import { isAcademicExtra } from './toothLayout'
import './odontogram.css'

export function OdontogramPage(){
 const {id}=useParams();const [search,setSearch]=useSearchParams();const historicalId=Number(search.get('consultation'))||undefined
 const mode:OdontogramMode=historicalId?{kind:'historical',consultationId:historicalId}:{kind:'current'}
 const [patient,setPatient]=useState<Patient>();const [consultations,setConsultations]=useState<ConsultationSummary[]>([]);const [pageError,setPageError]=useState('');const [selectedCode,setSelectedCode]=useState<string>();const [filter,setFilter]=useState<OdontogramFilter>('ALL');const [cameraView,setCameraView]=useState<CameraView>('RESET');const [resetKey,setResetKey]=useState(0)
 const [displayMode,setDisplayMode]=useState<'clinical'|'exploration'>('clinical')
 const [layers,setLayers]=useState({roots:false,numbers:true,quadrants:false,isolate:false})
 useEffect(()=>{if(!id)return;getClinicalRecord(id).then(async value=>{setPatient(value);setConsultations(await listConsultations(value.clinicalRecordId))}).catch(()=>setPageError('No fue posible cargar el contexto de la historia clínica.'))},[id])
 const {pieces,loading,error,history,loadHistory,reload}=useOdontogram(patient?.clinicalRecordId,mode);const care=useClinicalCare(patient?.clinicalRecordId)
 const activeCode=selectedCode??pieces.find(piece=>!isAcademicExtra(piece.visualOrder))?.pieceCode;const selectedPiece=pieces.find(piece=>piece.pieceCode===activeCode)
 useEffect(()=>{if(activeCode)void loadHistory(activeCode)},[activeCode,loadHistory])
 const select=useCallback((code:string)=>setSelectedCode(code),[])
 const pendingCodes=useMemo(()=>new Set(care.plans.flatMap(plan=>plan.items).filter(item=>item.status==='PENDING'||item.status==='IN_PROGRESS').map(item=>item.pieceCode)),[care.plans])
 const visibleCodes=useMemo(()=>new Set(pieces.filter(piece=>filter==='ALL'||filter==='AFFECTED'&&!!piece.currentEvent&&piece.currentEvent.status!=='SANA'||filter==='HISTORY'&&!!piece.currentEvent||filter==='PENDING'&&pendingCodes.has(piece.pieceCode)).map(piece=>piece.pieceCode)),[filter,pieces,pendingCodes])
 const reset=useCallback(()=>{setCameraView('RESET');setResetKey(v=>v+1);setLayers(v=>({...v,isolate:false}))},[])
 const resetFilters=useCallback(()=>{setFilter('ALL');setLayers(v=>({...v,isolate:false}))},[])
 const toggle=useCallback((key:'roots'|'numbers'|'quadrants'|'isolate')=>setLayers(v=>({...v,[key]:!v[key]})),[])
 if(!patient&&!pageError)return <><ClinicHeader/><main className="odontogram-loading">Preparando odontograma 3D…</main></>
 return <><ClinicHeader/><main className="odontogram-page">{(pageError||error)&&<div className="alert alert-danger" role="alert">{pageError||error}</div>}{patient&&<>
  <header className="odontogram-header"><div><p className="kicker">Historia clínica {patient.clinicalRecordNumber}</p><h1>Odontograma clínico</h1><p>{patient.firstNames} {patient.lastNames} <span>·</span> Exploración tridimensional de 36 piezas</p></div><div className="header-actions"><Link className="btn btn-outline-secondary" to={`/pacientes/${patient.id}/historia-clinica`}>← Volver a la HCE</Link><label><span>Vista temporal</span><select value={historicalId??'current'} onChange={e=>e.target.value==='current'?setSearch({}):setSearch({consultation:e.target.value})}><option value="current">Estado actual</option>{consultations.map(item=><option key={item.id} value={item.id}>Consulta #{item.id} · {new Date(item.consultationDate).toLocaleDateString('es-EC')}</option>)}</select></label></div></header>
  {mode.kind==='historical'&&<div className="historical-banner" role="status"><span>◷</span><div><strong>Contexto de Consulta #{mode.consultationId}</strong><p>Puedes registrar nuevos hallazgos clínicos asociados a esta consulta.</p></div></div>}
  <section className="odontogram-workspace">
   <aside className="odontogram-sidebar"><OdontogramFilters value={filter} onChange={setFilter}/><OdontogramLegend/><div className="mode-card"><span>Modo activo</span><strong>{mode.kind==='current'?'Estado actual':`Consulta #${mode.consultationId}`}</strong><p>{visibleCodes.size} de 36 piezas visibles</p></div></aside>
   <div className="scene-column"><div className="visualization-mode" role="group" aria-label="Modo de visualización"><button type="button" aria-pressed={displayMode==='clinical'} onClick={()=>setDisplayMode('clinical')}><span>Resumen clínico</span><small>Cuatro vistas comparables</small></button><button type="button" aria-pressed={displayMode==='exploration'} onClick={()=>setDisplayMode('exploration')}><span>Exploración 3D</span><small>Rotar, acercar y aislar</small></button></div>{displayMode==='exploration'&&<OdontogramToolbar showRoots={layers.roots} showNumbers={layers.numbers} showQuadrants={layers.quadrants} isolate={layers.isolate} onToggle={toggle} onView={setCameraView} onReset={reset} onResetFilters={resetFilters}/>} {loading?<div className="scene-state">Cargando las 36 piezas…</div>:displayMode==='clinical'?<OdontogramClinicalOverview pieces={pieces} visibleCodes={visibleCodes} selectedCode={activeCode} onSelect={select}/>:<OdontogramScene pieces={pieces} visibleCodes={visibleCodes} selectedCode={activeCode} onSelect={select} cameraView={cameraView} resetKey={resetKey} showRoots={layers.roots} showNumbers={layers.numbers} showQuadrants={layers.quadrants} isolate={layers.isolate}/>}<div className="scene-footer"><span className="live-dot"/> <strong data-testid="piece-count">{pieces.length} piezas 3D independientes</strong><span>Seleccionada: {activeCode?`Pieza ${Number(activeCode.slice(-2))}`:'ninguna'}</span></div></div>
   <ToothDetailPanel piece={selectedPiece} allPieces={pieces} history={activeCode?history[activeCode]??[]:[]} mode={mode} patientId={patient.id} clinicalRecordId={patient.clinicalRecordId} procedures={care.procedures} plans={care.plans} evolutions={care.evolutions} onEventCreated={reload}/>
  </section><OdontogramAccessibilityList pieces={pieces} selectedCode={activeCode} onSelect={select}/>
 </>}</main></>}
