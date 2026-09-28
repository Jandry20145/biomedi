import { OrbitControls } from '@react-three/drei'
import { Canvas, useFrame, useThree } from '@react-three/fiber'
import { useEffect, useRef } from 'react'
// import type { OrbitControls as OrbitControlsImpl } from 'three-stdlib'
import { Vector3 } from 'three'
import { DentalArch } from './DentalArch'
import { buildToothLayout } from './toothLayout'
import type { CameraView, OdontogramPiece } from './types'

const cameraPositions:Record<CameraView,[number,number,number]>={RESET:[0,6.2,12.5],FRONT:[0,0,13],TOP:[0,13,.01],BOTTOM:[0,-13,.01],LEFT:[-13,0,-1],RIGHT:[13,0,-1]}
function CameraRig({view,resetKey,selectedCode,pieces,isolate}:{view:CameraView;resetKey:number;selectedCode?:string;pieces:OdontogramPiece[];isolate:boolean}){
 const {camera}=useThree();const controls=useRef<any>(null);const destination=useRef(new Vector3(...cameraPositions[view]));const target=useRef(new Vector3())
 useEffect(()=>{const selected=buildToothLayout(pieces).find(t=>t.piece.pieceCode===selectedCode);if(selected&&isolate){const p=new Vector3(...selected.position);target.current.set(p.x*.65,p.y*.45,p.z*.65);destination.current.set(p.x*.45,p.y+3.4,p.z+9.2)}else{target.current.set(0,0,-1);destination.current.set(...cameraPositions[view])}},[view,resetKey,selectedCode,pieces,isolate])
 useFrame(()=>{camera.position.lerp(destination.current,.08);if(controls.current){controls.current.target.lerp(target.current,.1);controls.current.update()}})
 return <OrbitControls ref={controls} enablePan={false} minDistance={3.8} maxDistance={18} minPolarAngle={.04} maxPolarAngle={Math.PI-.04} makeDefault/>
}
interface Props {pieces:OdontogramPiece[];visibleCodes:Set<string>;selectedCode?:string;onSelect:(code:string)=>void;cameraView:CameraView;resetKey:number;showRoots?:boolean;showNumbers?:boolean;showQuadrants?:boolean;isolate?:boolean;onCanvasReady?:(canvas:HTMLCanvasElement)=>void}
export function OdontogramScene(props:Props){return <div className="odontogram-canvas" data-testid="odontogram-canvas" aria-label="Vista tridimensional interactiva del odontograma">
 <Canvas shadows dpr={[1,1.5]} camera={{position:[0,6.2,12.5],fov:40,near:.1,far:100}} gl={{antialias:true,powerPreference:'high-performance',preserveDrawingBuffer:true}} onCreated={({gl})=>props.onCanvasReady?.(gl.domElement)}>
  <color attach="background" args={['#eef3f1']}/><fog attach="fog" args={['#eef3f1',15,24]}/><hemisphereLight args={['#ffffff','#b8c6c4',2.1]}/><directionalLight position={[6,10,8]} intensity={2.5} castShadow/><directionalLight position={[-6,-1,4]} intensity={.75}/>
  <DentalArch pieces={props.pieces} visibleCodes={props.visibleCodes} selectedCode={props.selectedCode} showRoots={!!props.showRoots} showNumbers={props.showNumbers!==false} showQuadrants={!!props.showQuadrants} onSelect={props.onSelect}/>
  <CameraRig view={props.cameraView} resetKey={props.resetKey} selectedCode={props.selectedCode} pieces={props.pieces} isolate={!!props.isolate}/>
 </Canvas></div>}
