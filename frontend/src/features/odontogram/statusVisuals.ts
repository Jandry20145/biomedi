import type { OdontogramStatus } from './types'

export interface StatusVisual {
  label: string
  color: string
  symbol: string
  group: 'healthy' | 'disease' | 'treatment' | 'structural' | 'other'
}

export const DEFAULT_TOOTH_COLOR = '#f4f0e8'
export const STATUS_VISUALS: Record<OdontogramStatus, StatusVisual> = {
  SANA:{label:'Sana',color:'#b9d9cc',symbol:'○',group:'healthy'},
  CARIES:{label:'Caries',color:'#c9534b',symbol:'×',group:'disease'},
  RESTAURACION:{label:'Restauración',color:'#4186a8',symbol:'◆',group:'treatment'},
  OBTURACION_TEMPORAL:{label:'Obturación temporal',color:'#d99a3f',symbol:'◐',group:'treatment'},
  OBTURACION_DEFINITIVA:{label:'Obturación definitiva',color:'#286f91',symbol:'◆',group:'treatment'},
  FRACTURA:{label:'Fractura',color:'#8e5a50',symbol:'⌁',group:'structural'},
  AUSENTE:{label:'Ausente',color:'#858e91',symbol:'—',group:'structural'},
  EXTRACCION_INDICADA:{label:'Extracción indicada',color:'#c44766',symbol:'!',group:'treatment'},
  EXTRAIDA:{label:'Extraída',color:'#697579',symbol:'—',group:'structural'},
  ENDODONCIA:{label:'Endodoncia',color:'#7556a5',symbol:'│',group:'treatment'},
  CORONA:{label:'Corona',color:'#c49b32',symbol:'♢',group:'treatment'},
  PROTESIS:{label:'Prótesis',color:'#886da3',symbol:'P',group:'treatment'},
  IMPLANTE:{label:'Implante',color:'#587887',symbol:'I',group:'treatment'},
  SELLANTE:{label:'Sellante',color:'#4c9a82',symbol:'≈',group:'treatment'},
  MOVILIDAD:{label:'Movilidad',color:'#b8733f',symbol:'↔',group:'disease'},
  PLACA_BACTERIANA:{label:'Placa bacteriana',color:'#d0b736',symbol:'•',group:'disease'},
  CALCULO_DENTAL:{label:'Cálculo dental',color:'#948044',symbol:'▲',group:'disease'},
  LESION_PERIODONTAL:{label:'Lesión periodontal',color:'#99516a',symbol:'△',group:'disease'},
  OBSERVACION:{label:'Observación',color:'#387f78',symbol:'i',group:'other'},
}

export function toothColor(status?: OdontogramStatus) {
  return status ? STATUS_VISUALS[status].color : DEFAULT_TOOTH_COLOR
}
