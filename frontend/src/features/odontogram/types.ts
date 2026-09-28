export type OdontogramStatus='SANA'|'CARIES'|'RESTAURACION'|'OBTURACION_TEMPORAL'|'OBTURACION_DEFINITIVA'|'FRACTURA'|'AUSENTE'|'EXTRACCION_INDICADA'|'EXTRAIDA'|'ENDODONCIA'|'CORONA'|'PROTESIS'|'IMPLANTE'|'SELLANTE'|'MOVILIDAD'|'PLACA_BACTERIANA'|'CALCULO_DENTAL'|'LESION_PERIODONTAL'|'OBSERVACION'
export type DentalSurface='OCLUSAL'|'VESTIBULAR'|'LINGUAL'|'PALATINA'|'MESIAL'|'DISTAL'|'CERVICAL'|'RAIZ'|'PIEZA_COMPLETA'
export interface OdontogramEvent {id:number;consultationId:number;pieceCode:string;status:OdontogramStatus;surfaces:DentalSurface[];observations?:string;eventDate:string;createdBy:number}
export interface OdontogramPiece {pieceCode:string;position:string;arch:'UPPER'|'LOWER';side:'RIGHT'|'LEFT';pieceType:string;visualOrder:number;modelNodeName:string;fdiCode?:string;currentEvent:OdontogramEvent|null}
export type OdontogramFilter='ALL'|'AFFECTED'|'PENDING'|'HISTORY'
export type CameraView='RESET'|'FRONT'|'TOP'|'BOTTOM'|'LEFT'|'RIGHT'
export type OdontogramMode={kind:'current'}|{kind:'historical';consultationId:number}
