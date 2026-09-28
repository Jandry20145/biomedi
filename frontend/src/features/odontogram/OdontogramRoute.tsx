import { lazy, Suspense } from 'react'

const OdontogramPage=lazy(()=>import('./OdontogramPage').then(module=>({default:module.OdontogramPage})))
export function OdontogramRoute(){return <Suspense fallback={<p>Cargando odontograma 3D…</p>}><OdontogramPage/></Suspense>}
