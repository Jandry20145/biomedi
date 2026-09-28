import { Link } from 'react-router-dom'
import { type CSSProperties, type ReactNode, useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import type { ClinicalEvolution,Procedure,TreatmentPlan } from '../treatment/types'
import { STATUS_VISUALS } from './statusVisuals'
import type { OdontogramEvent, OdontogramMode, OdontogramPiece, OdontogramStatus, DentalSurface } from './types'
import { createOdontogramEvent, type CreateOdontogramEventRequest } from './odontogramApi'

interface Props {piece?:OdontogramPiece;allPieces?:OdontogramPiece[];history:OdontogramEvent[];mode:OdontogramMode;patientId:number;clinicalRecordId?:number;procedures?:Procedure[];plans?:TreatmentPlan[];evolutions?:ClinicalEvolution[];onEventCreated?:()=>void}
function friendly(code: string, allPieces?: OdontogramPiece[]) {
  if (allPieces && allPieces.length > 0) {
    const p = allPieces.find(x => x.pieceCode === code)
    if (p && p.fdiCode) return `Pieza ${p.fdiCode}`
  }
  return `Pieza ${Number(code.slice(-2))}`
}

const STATUS_OPTIONS: {value: OdontogramStatus; label: string}[] = [
  {value: 'SANA', label: 'Sana'},
  {value: 'CARIES', label: 'Caries'},
  {value: 'RESTAURACION', label: 'Restauración'},
  {value: 'OBTURACION_TEMPORAL', label: 'Obturación Temporal'},
  {value: 'OBTURACION_DEFINITIVA', label: 'Obturación Definitiva'},
  {value: 'FRACTURA', label: 'Fractura'},
  {value: 'AUSENTE', label: 'Ausente'},
  {value: 'EXTRACCION_INDICADA', label: 'Extracción Indicada'},
  {value: 'EXTRAIDA', label: 'Extraída'},
  {value: 'ENDODONCIA', label: 'Endodoncia'},
  {value: 'CORONA', label: 'Corona'},
  {value: 'PROTESIS', label: 'Prótesis'},
  {value: 'IMPLANTE', label: 'Implante'},
  {value: 'SELLANTE', label: 'Sellante'},
  {value: 'MOVILIDAD', label: 'Movilidad'},
  {value: 'PLACA_BACTERIANA', label: 'Placa Bacteriana'},
  {value: 'CALCULO_DENTAL', label: 'Cálculo Dental'},
  {value: 'LESION_PERIODONTAL', label: 'Lesión Periodontal'},
  {value: 'OBSERVACION', label: 'Observación'},
]

const SURFACE_OPTIONS: {value: DentalSurface; label: string}[] = [
  {value: 'PIEZA_COMPLETA', label: 'Pieza Completa'},
  {value: 'OCLUSAL', label: 'Oclusal'},
  {value: 'VESTIBULAR', label: 'Vestibular'},
  {value: 'LINGUAL', label: 'Lingual'},
  {value: 'PALATINA', label: 'Palatina'},
  {value: 'MESIAL', label: 'Mesial'},
  {value: 'DISTAL', label: 'Distal'},
  {value: 'CERVICAL', label: 'Cervical'},
  {value: 'RAIZ', label: 'Raíz'},
]

function OdontogramEventForm({ pieceCode, allPieces = [], clinicalRecordId, consultationId, onCreated, onCancel }: { pieceCode: string, allPieces: OdontogramPiece[], clinicalRecordId: number, consultationId: number, onCreated: () => void, onCancel: () => void }) {
  const { register, handleSubmit, formState: { isSubmitting }, reset } = useForm<CreateOdontogramEventRequest>({
    defaultValues: { pieceCode, status: 'CARIES', surfaces: ['PIEZA_COMPLETA'], observations: '' }
  })
  const [error, setError] = useState('')

  useEffect(() => {
    reset({ pieceCode, status: 'CARIES', surfaces: ['PIEZA_COMPLETA'], observations: '' })
  }, [pieceCode, reset])

  const onSubmit = handleSubmit(async (data) => {
    try {
      if (!data.surfaces || data.surfaces.length === 0) {
        setError('Debe seleccionar al menos una superficie.')
        return
      }
      setError('')
      await createOdontogramEvent(clinicalRecordId, consultationId, data)
      onCreated()
    } catch (e: any) {
      setError(e.response?.data?.message || 'Error al guardar el hallazgo')
    }
  })

  // Sort pieces so they appear ordered by number in the select dropdown
  const sortedPieces = [...allPieces].sort((a, b) => Number(a.pieceCode.slice(-2)) - Number(b.pieceCode.slice(-2)))

  return (
    <form onSubmit={onSubmit} className="panel-section" style={{ background: '#f8f9fa', padding: '1rem', borderRadius: '8px' }}>
      <h3 style={{ marginBottom: '1rem' }}>Registrar Hallazgo</h3>
      {error && <div className="alert alert-danger" style={{ padding: '0.5rem', fontSize: '0.875rem' }}>{error}</div>}
      
      <div style={{ marginBottom: '0.75rem' }}>
        <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, marginBottom: '0.25rem' }}>Pieza Dental</label>
        <select className="form-select form-select-sm" {...register('pieceCode')} required>
          {sortedPieces.length > 0 ? (
            sortedPieces.map(p => <option key={p.pieceCode} value={p.pieceCode}>{friendly(p.pieceCode, allPieces)}</option>)
          ) : (
            <option value={pieceCode}>{friendly(pieceCode, allPieces)}</option>
          )}
        </select>
      </div>

      <div style={{ marginBottom: '0.75rem' }}>
        <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, marginBottom: '0.25rem' }}>Estado Clínico</label>
        <select className="form-select form-select-sm" {...register('status')} required>
          {STATUS_OPTIONS.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
        </select>
      </div>
      
      <div style={{ marginBottom: '0.75rem' }}>
        <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, marginBottom: '0.25rem' }}>Superficies Afectadas</label>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.25rem' }}>
          {SURFACE_OPTIONS.map(o => (
            <div key={o.value} className="form-check">
              <input 
                className="form-check-input" 
                type="checkbox" 
                value={o.value} 
                id={`surface-${o.value}`}
                {...register('surfaces')} 
              />
              <label className="form-check-label" htmlFor={`surface-${o.value}`} style={{ fontSize: '0.875rem' }}>
                {o.label}
              </label>
            </div>
          ))}
        </div>
      </div>
      
      <div style={{ marginBottom: '1rem' }}>
        <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, marginBottom: '0.25rem' }}>Observaciones</label>
        <textarea className="form-control form-control-sm" {...register('observations')} rows={2} />
      </div>
      
      <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
        <button type="button" className="btn btn-sm btn-outline-secondary" onClick={onCancel} disabled={isSubmitting}>Cancelar</button>
        <button type="submit" className="btn btn-sm btn-primary" disabled={isSubmitting}>Guardar</button>
      </div>
    </form>
  )
}

export function ToothDetailPanel({piece,allPieces=[],history,mode,patientId,clinicalRecordId,procedures=[],plans=[],evolutions=[],onEventCreated}:Props){
 const [showForm, setShowForm] = useState(false)

 if(!piece)return <aside className="tooth-panel empty-panel"><span className="panel-icon">＋</span><p className="kicker">Detalle clínico</p><h2>Seleccione una pieza</h2><p>Use el modelo 3D o la lista accesible para consultar su historia.</p></aside>
 const event=piece.currentEvent;const visual=event?STATUS_VISUALS[event.status]:null
 const relatedProcedures=procedures.filter(item=>item.pieceCodes.includes(piece.pieceCode))
 const pending=plans.flatMap(plan=>plan.items).filter(item=>item.pieceCode===piece.pieceCode&&item.status!=='COMPLETED'&&item.status!=='CANCELLED')
 const relatedEvolutions=evolutions.filter(item=>item.pieceCode===piece.pieceCode)
 
 const isHistorical = mode.kind === 'historical'
 const canAddEvent = isHistorical && clinicalRecordId !== undefined
 const tResult:Record<string,string>={FAVORABLE:'Favorable',STABLE:'Estable',PENDING:'Pendiente',REQUIRES_FOLLOW_UP:'Requiere seguimiento'};

 return <aside className="tooth-panel" aria-live="polite">
  <header className="tooth-panel-header"><div><p className="kicker">Pieza seleccionada</p><h2>{friendly(piece.pieceCode, allPieces)}</h2><code>{piece.fdiCode || piece.pieceCode}</code></div>{visual&&<span className="status-medallion" style={{'--status-color':visual.color} as CSSProperties}>{visual.symbol}</span>}</header>
  
  {isHistorical && !showForm && (
    <div style={{ padding: '0 1rem 1rem' }}>
      <button className="btn btn-primary btn-sm w-100" onClick={() => setShowForm(true)}>+ Registrar hallazgo en esta consulta</button>
    </div>
  )}

  {showForm && canAddEvent ? (
    <OdontogramEventForm 
      pieceCode={piece.pieceCode} 
      allPieces={allPieces}
      clinicalRecordId={clinicalRecordId} 
      consultationId={mode.consultationId} 
      onCancel={() => setShowForm(false)} 
      onCreated={() => { setShowForm(false); onEventCreated?.(); }} 
    />
  ) : (
    <>
      <section className="clinical-summary"><div><span>Estado clínico</span><strong>{visual?.label??'Sin hallazgos'}</strong></div><div><span>Superficies</span><strong>{event?.surfaces.map(s=>s.replaceAll('_',' ')).join(' · ')||'No registradas'}</strong></div></section>
      <section className="panel-section"><h3>Hallazgo actual</h3><p>{event?.observations||'No existen observaciones clínicas para esta pieza.'}</p>{event&&<dl className="event-meta"><div><dt>Fecha</dt><dd>{new Date(event.eventDate).toLocaleString('es-EC')}</dd></div><div><dt>Profesional</dt><dd>Usuario clínico #{event.createdBy}</dd></div></dl>}</section>
      <section className="panel-section"><h3>Historial por pieza <span>{history.length}</span></h3>{history.length===0?<p className="empty-note">Sin eventos históricos.</p>:<ol className="history-list">{history.map(item=><li key={item.id}><span className="history-marker" style={{background:STATUS_VISUALS[item.status].color}}/><div><time>{new Date(item.eventDate).toLocaleString('es-EC')}</time><strong>{STATUS_VISUALS[item.status].label}</strong><small>{item.surfaces.join(' · ')}</small><Link to={`/pacientes/${patientId}/historia-clinica?consultation=${item.consultationId}`}>Abrir consulta #{item.consultationId}</Link></div></li>)}</ol>}</section>
      <PanelList title="Procedimientos" empty="Sin procedimientos asociados." items={relatedProcedures.map(item=><p key={item.id}><strong>{item.typeName}</strong><span>{item.description}</span></p>)}/>
      <PanelList title="Tratamientos pendientes" empty="Sin tratamientos pendientes." items={pending.map(item=><p key={item.id}><strong>{item.description}</strong><span>Prioridad {item.priority} · {item.sessionsEstimated} sesiones</span></p>)}/>
      <PanelList title="Evolución" empty="Sin evoluciones asociadas." items={relatedEvolutions.map(item=><p key={item.id}><strong>{tResult[item.result]||item.result.replaceAll('_',' ')}</strong><span>{item.description}</span></p>)}/>
    </>
  )}
  <p className="lock-note">
    {!isHistorical ? 'Seleccione una consulta en la "Vista temporal" para registrar hallazgos.' : 'Los hallazgos se registrarán en la consulta activa.'}
  </p>
 </aside>
}
function PanelList({title,empty,items}:{title:string;empty:string;items:ReactNode[]}){return <section className="panel-section"><h3>{title} <span>{items.length}</span></h3>{items.length?items:<p className="empty-note">{empty}</p>}</section>}
