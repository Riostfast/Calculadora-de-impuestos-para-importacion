package mx.edu.uaeh.importaciones;

/** Registro de una fracción arancelaria utilizada en el ejercicio. */
public final class Producto {
    private final String categoria, fraccion, nombre, descripcion, requisitos;
    private final double adValorem;

    public Producto(String categoria, String fraccion, String nombre,
                    String descripcion, double adValorem, String requisitos) {
        if (categoria == null || fraccion == null || adValorem < 0)
            throw new IllegalArgumentException("Datos de producto inválidos");
        this.categoria = categoria;
        this.fraccion = fraccion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.adValorem = adValorem;
        this.requisitos = requisitos;
    }

    public String categoria()   { return categoria; }
    public String fraccion()    { return fraccion; }
    public String nombre()      { return nombre; }
    public String descripcion() { return descripcion; }
    public double adValorem()   { return adValorem; }
    public String requisitos()  { return requisitos; }

    @Override
    public String toString() {
        return nombre + " - " + fraccion
            + " (IGI " + String.format("%.0f%%", adValorem * 100) + ")";
    }
}