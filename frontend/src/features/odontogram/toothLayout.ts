import type { OdontogramPiece } from './types'

export type ToothVariant =
  | 'CENTRAL_INCISOR' | 'LATERAL_INCISOR' | 'CANINE'
  | 'FIRST_PREMOLAR' | 'SECOND_PREMOLAR'
  | 'FIRST_MOLAR' | 'SECOND_MOLAR' | 'THIRD_MOLAR' | 'EXTRA'

export interface ToothTransform {
  piece: OdontogramPiece
  position: [number, number, number]
  rotation: [number, number, number]
  scale: [number, number, number]
  variant: ToothVariant
  quadrant: 1 | 2 | 3 | 4
  academicExtra: boolean
}

export const ACADEMIC_EXTRA_ORDERS = new Set([1, 18, 19, 36])
export const PRIMARY_FAMILY_BY_DISTANCE: ToothVariant[] = [
  'CENTRAL_INCISOR', 'LATERAL_INCISOR', 'CANINE', 'FIRST_PREMOLAR',
  'SECOND_PREMOLAR', 'FIRST_MOLAR', 'SECOND_MOLAR', 'THIRD_MOLAR',
]

export function isAcademicExtra(visualOrder: number) {
  return ACADEMIC_EXTRA_ORDERS.has(visualOrder)
}

export function visualToothVariant(visualOrder: number): ToothVariant {
  if (isAcademicExtra(visualOrder)) return 'EXTRA'
  const upper = visualOrder <= 18
  const primaryIndex = upper ? visualOrder - 2 : visualOrder - 20
  const distanceFromMidline = primaryIndex < 8 ? 7 - primaryIndex : primaryIndex - 8
  return PRIMARY_FAMILY_BY_DISTANCE[distanceFromMidline]
}

function extraTransform(piece: OdontogramPiece, index: number): ToothTransform {
  return {
    piece,
    position: [(index - 1.5) * .82, -3.18, -1.45],
    rotation: [0, 0, 0],
    scale: [.72, .72, .72],
    variant: 'EXTRA',
    quadrant: piece.arch === 'UPPER' ? 2 : 3,
    academicExtra: true,
  }
}

export function buildToothLayout(pieces: OdontogramPiece[]): ToothTransform[] {
  let extraIndex = 0
  return pieces.map((piece) => {
    if (isAcademicExtra(piece.visualOrder)) return extraTransform(piece, extraIndex++)
    const upper = piece.arch === 'UPPER'
    const primaryIndex = upper ? piece.visualOrder - 2 : piece.visualOrder - 20
    const normalized = (primaryIndex - 7.5) / 7.5
    const x = normalized * 5.05
    const z = -.22 - 2.72 * Math.pow(Math.abs(normalized), 1.7)
    const y = upper ? 1.18 : -1.18
    const tangent = Math.atan2(-4.65 * Math.sign(normalized) * Math.pow(Math.abs(normalized), .72), 5.05)
    const distance = Math.abs(normalized)
    const quadrant = (upper ? (primaryIndex < 8 ? 1 : 2) : (primaryIndex < 8 ? 4 : 3)) as 1 | 2 | 3 | 4
    return {
      piece,
      position: [x, y, z],
      rotation: [0, -tangent, upper ? Math.PI : 0],
      scale: [.88 + distance * .09, .88, .88 + distance * .10],
      variant: visualToothVariant(piece.visualOrder),
      quadrant,
      academicExtra: false,
    }
  })
}
