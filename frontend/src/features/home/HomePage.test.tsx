import { render, screen } from '@testing-library/react'

import { HomePage } from './HomePage'

describe('HomePage', () => {
  it('identifica el entorno base de OdontoUcacue', () => {
    render(<HomePage />)

    expect(screen.getByRole('heading', { name: 'Entorno base configurado' })).toBeInTheDocument()
    expect(screen.getByText('OdontoUcacue')).toBeInTheDocument()
  })
})
