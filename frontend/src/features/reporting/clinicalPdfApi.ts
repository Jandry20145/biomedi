import { apiClient } from '../../shared/http/apiClient'
export async function generateClinicalPdf(recordId:number,image:Blob){const form=new FormData();form.append('odontogram',image,'odontogram.png');return(await apiClient.post<Blob>(`../clinical-records/${recordId}/report.pdf`,form,{responseType:'blob',headers:{'Content-Type':'multipart/form-data'}})).data}
