import { fireEvent, render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it, vi } from 'vitest'
import { OdontogramAccessibilityList } from './OdontogramAccessibilityList'
import { ToothDetailPanel } from './ToothDetailPanel'
import { buildToothLayout, isAcademicExtra } from './toothLayout'
import { TOOTH_MODEL_URLS } from './toothModelUrls'
import type { OdontogramPiece } from './types'

const pieces:OdontogramPiece[]=Array.from({length:36},(_,index)=>({pieceCode:`TOOTH_${String(index+1).padStart(2,'0')}`,position:`POSITION_${index+1}`,arch:index<18?'UPPER':'LOWER',side:index%18<9?'RIGHT':'LEFT',pieceType:'CONFIGURABLE',visualOrder:index+1,modelNodeName:`tooth_${index+1}`,currentEvent:index===35?{id:6,consultationId:4,pieceCode:'TOOTH_36',status:'RESTAURACION',surfaces:['PIEZA_COMPLETA'],observations:'DATO DEMOSTRATIVO',eventDate:'2026-06-18T10:15:00',createdBy:3}:null}))

describe('odontograma 3D',()=>{
 it('genera 32 piezas principales, cuatro adicionales y nueve GLB distintos',()=>{const layout=buildToothLayout(pieces);expect(layout).toHaveLength(36);expect(new Set(layout.map(item=>item.piece.pieceCode)).size).toBe(36);expect(layout.filter(item=>item.academicExtra)).toHaveLength(4);expect(layout.filter(item=>!item.academicExtra)).toHaveLength(32);expect(Object.keys(TOOTH_MODEL_URLS)).toHaveLength(9);expect(new Set(Object.values(TOOTH_MODEL_URLS)).size).toBe(9);expect([1,18,19,36].every(isAcademicExtra)).toBe(true)})
 it('ofrece una lista textual de 36 piezas y permite seleccionar',()=>{const onSelect=vi.fn();render(<OdontogramAccessibilityList pieces={pieces} onSelect={onSelect}/>);expect(screen.getAllByRole('button')).toHaveLength(36);fireEvent.click(screen.getByRole('button',{name:/TOOTH_36/i}));expect(onSelect).toHaveBeenCalledWith('TOOTH_36')})
 it('muestra estado, superficies, observaciones e historial en el panel',()=>{render(<MemoryRouter><ToothDetailPanel piece={pieces[35]} history={[pieces[35].currentEvent!]} mode={{kind:'current'}} patientId={14}/></MemoryRouter>);expect(screen.getAllByText('Restauración')).toHaveLength(2);expect(screen.getByText('PIEZA_COMPLETA')).toBeInTheDocument();expect(screen.getAllByText('DATO DEMOSTRATIVO').length).toBeGreaterThan(0);expect(screen.getByRole('link',{name:/Abrir consulta #4/})).toBeInTheDocument()})
 it('bloquea edición visual en modo histórico',()=>{render(<MemoryRouter><ToothDetailPanel piece={pieces[35]} history={[]} mode={{kind:'historical',consultationId:4}} patientId={14}/></MemoryRouter>);expect(screen.getByText(/Consulta #4 · Solo lectura/)).toBeInTheDocument();expect(screen.getByText(/consulta histórica es inmutable/i)).toBeInTheDocument()})
 it('integra procedimientos y planes asociados a la pieza',()=>{render(<MemoryRouter><ToothDetailPanel piece={pieces[35]} history={[]} mode={{kind:'current'}} patientId={14} procedures={[{id:1,consultationId:4,type:'PROPHYLAXIS',typeName:'Profilaxis',pieceCodes:['TOOTH_36'],description:'Profilaxis localizada',status:'COMPLETED',date:'2026-06-18T11:00:00',professionalId:3}]} plans={[{id:1,title:'Plan',status:'ACTIVE',items:[{id:1,pieceCode:'TOOTH_36',description:'Control pendiente',priority:2,sessionsEstimated:2,status:'PENDING'}]}]}/></MemoryRouter>);expect(screen.getByText('Profilaxis localizada')).toBeInTheDocument();expect(screen.getByText('Control pendiente')).toBeInTheDocument()})
})
