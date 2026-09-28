import { beforeEach, describe, expect, it, vi } from 'vitest'
import { apiClient } from '../../shared/http/apiClient'
import { getConsultationOdontogram, getCurrentOdontogram, getPieceHistory } from './odontogramApi'

vi.mock('../../shared/http/apiClient',()=>({apiClient:{get:vi.fn()}}))
const get=vi.mocked(apiClient.get)
describe('odontogramApi',()=>{beforeEach(()=>get.mockReset())
 it('consume la proyección actual real',async()=>{get.mockResolvedValue({data:Array(36).fill({})});expect(await getCurrentOdontogram(11)).toHaveLength(36);expect(get).toHaveBeenCalledWith('../clinical-records/11/odontogram/current')})
 it('consume consulta histórica e historial por pieza',async()=>{get.mockResolvedValueOnce({data:[{id:5}]}).mockResolvedValueOnce({data:[{id:5},{id:6}]});expect(await getConsultationOdontogram(11,3)).toHaveLength(1);expect(await getPieceHistory(11,'TOOTH_36')).toHaveLength(2)})
})
