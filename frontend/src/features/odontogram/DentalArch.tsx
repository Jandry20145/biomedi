import { useMemo } from 'react'
import { CatmullRomCurve3, Vector3 } from 'three'
import { ToothMesh } from './ToothMesh'
import { buildToothLayout } from './toothLayout'
import type { OdontogramPiece } from './types'

interface Props {pieces:OdontogramPiece[];visibleCodes:Set<string>;selectedCode?:string;showRoots:boolean;showNumbers:boolean;showQuadrants:boolean;onSelect:(code:string)=>void}
function GingivalGuide({upper}:{upper:boolean}){
 const curve=useMemo(()=>new CatmullRomCurve3([new Vector3(-5.2,upper?1.01:-1.01,-3.35),new Vector3(-2.6,upper?1.01:-1.01,-1.0),new Vector3(0,upper?1.01:-1.01,-.34),new Vector3(2.6,upper?1.01:-1.01,-1.0),new Vector3(5.2,upper?1.01:-1.01,-3.35)]),[upper])
 return <mesh><tubeGeometry args={[curve,64,.055,8,false]}/><meshStandardMaterial color={upper?'#d7a09b':'#cb8e8b'} roughness={.8} transparent opacity={.55}/></mesh>
}
export function DentalArch({pieces,visibleCodes,selectedCode,showRoots,showNumbers,showQuadrants,onSelect}:Props){
 const layout=useMemo(()=>buildToothLayout(pieces),[pieces])
 const primary=layout.filter(tooth=>!tooth.academicExtra)
 const extras=layout.filter(tooth=>tooth.academicExtra)
 const renderTooth=(tooth:typeof layout[number])=><ToothMesh key={tooth.piece.pieceCode} {...tooth} selected={selectedCode===tooth.piece.pieceCode} visible={visibleCodes.has(tooth.piece.pieceCode)} showRoots={showRoots} showNumbers={showNumbers} onSelect={onSelect}/>
 return <group><GingivalGuide upper/><GingivalGuide upper={false}/>
  {showQuadrants&&<group>{[-1,1].map(x=><mesh key={x} position={[x*.08,0,-1.5]}><boxGeometry args={[.018,3.5,4.8]}/><meshBasicMaterial color="#6aa8a0" transparent opacity={.18}/></mesh>)}</group>}
  {primary.map(renderTooth)}
  <group name="ACADEMIC_CONFIGURABLE_TEETH" userData={{description:'Cuatro piezas académicas adicionales, fuera de la dentición principal'}}>
   <mesh position={[0,-3.23,-1.45]}><boxGeometry args={[4.15,1.32,2.15]}/><meshStandardMaterial color="#dce9e5" transparent opacity={.30}/></mesh>
   {extras.map(renderTooth)}
  </group>
 </group>
}
