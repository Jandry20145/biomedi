import { Html } from '@react-three/drei'
import type { ThreeEvent } from '@react-three/fiber'
import { memo, useCallback, useState } from 'react'
import { STATUS_VISUALS, DEFAULT_TOOTH_COLOR } from './statusVisuals'
import { ToothAnatomy } from './ToothAnatomy'
import { ToothSurfaceOverlay } from './ToothSurfaceOverlay'
import type { ToothTransform } from './toothLayout'

interface Props extends ToothTransform {
  selected:boolean; visible:boolean; showRoots:boolean; showNumbers:boolean; onSelect:(pieceCode:string)=>void
}
export const ToothMesh=memo(function ToothMesh({piece,position,rotation,scale,selected,visible,showRoots,showNumbers,onSelect,variant}:Props){
 const [hovered,setHovered]=useState(false)
 const select=useCallback((event:ThreeEvent<MouseEvent>)=>{event.stopPropagation();onSelect(piece.pieceCode)},[onSelect,piece.pieceCode])
 const event=piece.currentEvent
 const base=event?.status==='AUSENTE'||event?.status==='EXTRAIDA'?'#adb5b7':DEFAULT_TOOTH_COLOR
 return <group name={piece.pieceCode} userData={{nodeId:piece.pieceCode,variant}} position={position} rotation={rotation} scale={scale} visible={visible}>
   <group onClick={select} onPointerOver={e=>{e.stopPropagation();setHovered(true)}} onPointerOut={()=>setHovered(false)} scale={selected?1.12:hovered?1.06:1}>
    <ToothAnatomy variant={variant} color={base} showRoots={showRoots} selected={selected}/>
    {event&&<ToothSurfaceOverlay surfaces={event.surfaces} status={event.status}/>}
    {selected&&<mesh position={[0,.03,0]}><torusGeometry args={[.48,.035,8,28]}/><meshBasicMaterial color="#19a795" depthTest={false}/></mesh>}
   </group>
   {showNumbers&&visible&&<Html center position={[0,.82,0]} distanceFactor={10} occlude={false}><span className="tooth-number-label">{Number(piece.pieceCode.slice(-2))}</span></Html>}
   {event&&visible&&<Html center position={[.42,.58,0]} distanceFactor={11} occlude={false}><span className="tooth-status-symbol" style={{background:STATUS_VISUALS[event.status].color}}>{STATUS_VISUALS[event.status].symbol}</span></Html>}
 </group>
})
