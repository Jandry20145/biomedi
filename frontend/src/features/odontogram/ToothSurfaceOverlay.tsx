import type { DentalSurface, OdontogramStatus } from './types'
import { STATUS_VISUALS } from './statusVisuals'

const SURFACE_POSITION: Record<DentalSurface, [number, number, number]> = {
  OCLUSAL:[0,.39,0], VESTIBULAR:[0,.03,.32], LINGUAL:[0,.03,-.32], PALATINA:[0,.03,-.32],
  MESIAL:[-.32,.04,0], DISTAL:[.32,.04,0], CERVICAL:[0,-.25,.25], RAIZ:[0,-.72,.08], PIEZA_COMPLETA:[0,.08,0],
}
const SURFACE_ROTATION: Record<DentalSurface, [number, number, number]> = {
  OCLUSAL:[-Math.PI/2,0,0], VESTIBULAR:[0,0,0], LINGUAL:[0,Math.PI,0], PALATINA:[0,Math.PI,0],
  MESIAL:[0,-Math.PI/2,0], DISTAL:[0,Math.PI/2,0], CERVICAL:[0,0,0], RAIZ:[0,0,0], PIEZA_COMPLETA:[-Math.PI/2,0,0],
}

export function ToothSurfaceOverlay({surfaces,status}:{surfaces:DentalSurface[];status:OdontogramStatus}) {
  const visual=STATUS_VISUALS[status]
  return <group aria-hidden>{surfaces.map((surface,index)=><mesh key={surface} position={SURFACE_POSITION[surface]} rotation={SURFACE_ROTATION[surface]} renderOrder={3}>
    {surface==='PIEZA_COMPLETA'?<torusGeometry args={[.28,.045,8,24]}/>:<circleGeometry args={[surface==='OCLUSAL'?.23:.14,20]}/>}
    <meshStandardMaterial color={visual.color} emissive={visual.color} emissiveIntensity={.18} roughness={.4} depthTest={false} depthWrite={false} polygonOffset polygonOffsetFactor={-2-index}/>
  </mesh>)}</group>
}
