import { Canvas } from '@react-three/fiber'
import { Html } from '@react-three/drei'
import { Suspense, useMemo } from 'react'
import { ToothMesh } from './ToothMesh'
import { isAcademicExtra, visualToothVariant, type ToothTransform } from './toothLayout'
import type { OdontogramPiece } from './types'

interface Props { pieces:OdontogramPiece[]; visibleCodes:Set<string>; selectedCode?:string; onSelect:(code:string)=>void }
type ClinicalRow='upper-front'|'upper-occlusal'|'lower-occlusal'|'lower-front'

function clinicalTransform(piece:OdontogramPiece,index:number,row:ClinicalRow):ToothTransform {
 const yByRow:Record<ClinicalRow,number>={'upper-front':3.25,'upper-occlusal':1.05,'lower-occlusal':-1.05,'lower-front':-3.25}
 const rotationByRow:Record<ClinicalRow,[number,number,number]>={'upper-front':[-Math.PI/2,0,Math.PI],'upper-occlusal':[0,0,Math.PI],'lower-occlusal':[0,0,0],'lower-front':[Math.PI/2,0,0]}
 return {piece,position:[(index-7.5)*.50,yByRow[row],0],rotation:rotationByRow[row],scale:[.62,.62,.62],variant:visualToothVariant(piece.visualOrder),quadrant:piece.arch==='UPPER'?(index<8?1:2):(index<8?4:3),academicExtra:false}
}

function OverviewContent({pieces,visibleCodes,selectedCode,onSelect}:Props){
 const rows=useMemo(()=>{const primary=pieces.filter(piece=>!isAcademicExtra(piece.visualOrder));return {upper:primary.filter(piece=>piece.arch==='UPPER').sort((a,b)=>a.visualOrder-b.visualOrder),lower:primary.filter(piece=>piece.arch==='LOWER').sort((a,b)=>a.visualOrder-b.visualOrder)}},[pieces])
 const render=(piece:OdontogramPiece,index:number,row:ClinicalRow,roots:boolean,numbers:boolean)=><ToothMesh key={`${row}-${piece.pieceCode}`} {...clinicalTransform(piece,index,row)} selected={selectedCode===piece.pieceCode} visible={visibleCodes.has(piece.pieceCode)} showRoots={roots} showNumbers={numbers} onSelect={onSelect}/>
 const numbers=(items:OdontogramPiece[],y:number)=>items.map((piece,index)=><Html key={`number-${piece.pieceCode}-${y}`} center position={[(index-7.5)*.50,y,.2]} distanceFactor={14} occlude={false}><span className="tooth-number-label">{Number(piece.pieceCode.slice(-2))}</span></Html>)
 return <><color attach="background" args={['#f8faf9']}/><ambientLight intensity={2.2}/><directionalLight position={[4,8,9]} intensity={2.8}/><directionalLight position={[-6,-2,6]} intensity={1.2}/>{rows.upper.map((piece,index)=>render(piece,index,'upper-front',true,false))}{numbers(rows.upper,2.18)}{rows.upper.map((piece,index)=>render(piece,index,'upper-occlusal',false,false))}{rows.lower.map((piece,index)=>render(piece,index,'lower-occlusal',false,false))}{numbers(rows.lower,-2.18)}{rows.lower.map((piece,index)=>render(piece,index,'lower-front',true,false))}</>
}

export function OdontogramClinicalOverview(props:Props){
 const extras=props.pieces.filter(piece=>isAcademicExtra(piece.visualOrder))
 return <div className="clinical-overview"><div className="overview-row-labels" aria-hidden="true"><span>Superior · vestibular y raíces</span><span>Superior · oclusal</span><span>Inferior · oclusal</span><span>Inferior · vestibular y raíces</span></div><div className="clinical-overview-canvas" data-testid="clinical-overview-canvas"><Canvas orthographic camera={{position:[0,0,16],zoom:76,near:.1,far:50}} dpr={[1,1.5]} gl={{antialias:true,preserveDrawingBuffer:true}}><Suspense fallback={null}><OverviewContent {...props}/></Suspense></Canvas></div><div className="academic-extra-strip"><div><strong>Piezas académicas configurables</strong><span>Fuera de la dentición principal de 32 piezas</span></div>{extras.map(piece=><button key={piece.pieceCode} type="button" aria-pressed={props.selectedCode===piece.pieceCode} onClick={()=>props.onSelect(piece.pieceCode)}><code>{piece.pieceCode}</code><span>{piece.currentEvent?.status??'Sin hallazgo'}</span></button>)}</div></div>
}
