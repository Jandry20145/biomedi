import { useGLTF } from '@react-three/drei'
import { useMemo } from 'react'
import type { Object3D } from 'three'
import type { ToothVariant } from './toothLayout'
import { TOOTH_MODEL_URLS } from './toothModelUrls'

interface Props {
  variant: ToothVariant
  color: string
  showRoots: boolean
  selected: boolean
}

function prepareModel(source: Object3D, showRoots: boolean, color: string) {
  const clone = source.clone(true)
  clone.traverse((child: any) => {
    if (child.isMesh) {
      if (!child.userData.originalMaterial) {
        child.userData.originalMaterial = child.material
      }
      child.material = child.userData.originalMaterial.clone()
      child.material.color.set(color)
    }
    if (child.name.toLowerCase().includes('root')) child.visible = showRoots
  })
  return clone
}

export function ToothAnatomy({ variant, color, showRoots }: Props) {
  const { scene } = useGLTF(TOOTH_MODEL_URLS[variant])
  const model = useMemo(() => prepareModel(scene, showRoots, color), [scene, showRoots, color])
  return <primitive object={model} position={[0, -0.56, 0]} scale={0.72} />
}

Object.values(TOOTH_MODEL_URLS).forEach((url) => useGLTF.preload(url))
