import { OdontogramScene } from '../odontogram/OdontogramScene'
import type { OdontogramPiece } from '../odontogram/types'
export function PdfOdontogramCapture({pieces,onReady}:{pieces:OdontogramPiece[];onReady:(canvas:HTMLCanvasElement)=>void}){return <div className="pdf-capture" aria-hidden="true"><OdontogramScene pieces={pieces} visibleCodes={new Set(pieces.map(piece=>piece.pieceCode))} onSelect={()=>{}} cameraView="FRONT" resetKey={0} onCanvasReady={onReady}/></div>}
