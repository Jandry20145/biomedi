export function HomePage() {
  return (
    <main className="container py-5">
      <section aria-labelledby="page-title" className="mx-auto app-introduction">
        <p className="text-uppercase text-primary fw-semibold mb-2">OdontoUcacue</p>
        <h1 id="page-title" className="display-5 fw-bold">
          Entorno base configurado
        </h1>
        <p className="lead text-body-secondary mb-0">
          La aplicación está preparada para incorporar los módulos de la Historia Clínica
          Electrónica Odontológica en las siguientes fases.
        </p>
      </section>
    </main>
  )
}

