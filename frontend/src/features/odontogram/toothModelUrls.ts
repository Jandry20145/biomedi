import centralIncisorUrl from '../../assets/odontogram/models/central-incisor.glb?url'
import lateralIncisorUrl from '../../assets/odontogram/models/lateral-incisor.glb?url'
import canineUrl from '../../assets/odontogram/models/canine.glb?url'
import firstPremolarUrl from '../../assets/odontogram/models/first-premolar.glb?url'
import secondPremolarUrl from '../../assets/odontogram/models/second-premolar.glb?url'
import firstMolarUrl from '../../assets/odontogram/models/first-molar.glb?url'
import secondMolarUrl from '../../assets/odontogram/models/second-molar.glb?url'
import thirdMolarUrl from '../../assets/odontogram/models/third-molar.glb?url'
import configurableExtraUrl from '../../assets/odontogram/models/configurable-extra.glb?url'
import type { ToothVariant } from './toothLayout'

export const TOOTH_MODEL_URLS:Record<ToothVariant,string>={
 CENTRAL_INCISOR:centralIncisorUrl,LATERAL_INCISOR:lateralIncisorUrl,CANINE:canineUrl,
 FIRST_PREMOLAR:firstPremolarUrl,SECOND_PREMOLAR:secondPremolarUrl,FIRST_MOLAR:firstMolarUrl,
 SECOND_MOLAR:secondMolarUrl,THIRD_MOLAR:thirdMolarUrl,EXTRA:configurableExtraUrl,
}
