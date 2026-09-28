import type { CameraView } from './types'

interface Props {showRoots:boolean;showNumbers:boolean;showQuadrants:boolean;isolate:boolean;onToggle:(key:'roots'|'numbers'|'quadrants'|'isolate')=>void;onView:(view:CameraView)=>void;onReset:()=>void;onResetFilters:()=>void}
const views:[CameraView,string][]=[['FRONT','Frontal'],['TOP','Superior'],['BOTTOM','Inferior'],['LEFT','Izquierda'],['RIGHT','Derecha']]
export function OdontogramToolbar({showRoots,showNumbers,showQuadrants,isolate,onToggle,onView,onReset,onResetFilters}:Props){return <div className="odontogram-toolbar" aria-label="Herramientas del odontograma">
 <section><p>Vista</p><div className="tool-row"><button className="tool-primary" onClick={onReset}>Vista general</button>{views.map(([key,label])=><button key={key} onClick={()=>onView(key)}>{label}</button>)}</div></section>
 <section><p>Capas clínicas</p><div className="tool-row"><button aria-pressed={showNumbers} onClick={()=>onToggle('numbers')}>Numeración</button><button aria-pressed={showRoots} onClick={()=>onToggle('roots')}>Raíces</button><button aria-pressed={showQuadrants} onClick={()=>onToggle('quadrants')}>Cuadrantes</button><button aria-pressed={isolate} onClick={()=>onToggle('isolate')}>Aislar pieza</button><button onClick={onResetFilters}>Limpiar filtros</button></div></section>
 <span className="interaction-hint">Arrastre para rotar · rueda para acercar</span>
 </div>}
