package implementacion;


public class CultivoSeleccionado {
    private String nombreCultivo;
    private Coordenada esquinaSuperiorIzquierda;
    private Coordenada esquinaInferiorDerecha;
    private double montoInvertido;
    private double riesgoAsociado;
    private double gananciaObtenida;

    public CultivoSeleccionado() {
    }

    public CultivoSeleccionado(String nombreCultivo, Coordenada esquinaSuperiorIzquierda, Coordenada esquinaInferiorDerecha, double montoInvertido, double riesgoAsociado, double gananciaObtenida) {
        this.nombreCultivo = nombreCultivo;
        this.esquinaSuperiorIzquierda = esquinaSuperiorIzquierda;
        this.esquinaInferiorDerecha = esquinaInferiorDerecha;
        this.montoInvertido = montoInvertido;
        this.riesgoAsociado = riesgoAsociado;
        this.gananciaObtenida = gananciaObtenida;
    }

    public String getNombreCultivo() {
        return this.nombreCultivo;
    }

    public void setNombreCultivo(String nombreCultivo) {
        this.nombreCultivo = nombreCultivo;
    }

    public Coordenada getEsquinaSuperiorIzquierda() {
        return this.esquinaSuperiorIzquierda;
    }

    public void setEsquinaSuperiorIzquierda(Coordenada esquinaSuperiorIzquierda) {
        this.esquinaSuperiorIzquierda = esquinaSuperiorIzquierda;
    }

    public Coordenada getEsquinaInferiorDerecha() {
        return this.esquinaInferiorDerecha;
    }

    public void setEsquinaInferiorDerecha(Coordenada esquinaInferiorDerecha) {
        this.esquinaInferiorDerecha = esquinaInferiorDerecha;
    }

    public double getMontoInvertido() {
        return this.montoInvertido;
    }

    public void setMontoInvertido(double montoInvertido) {
        this.montoInvertido = montoInvertido;
    }

    public double getRiesgoAsociado() {
        return this.riesgoAsociado;
    }

    public void setRiesgoAsociado(int riesgoAsociado) {
        this.riesgoAsociado = riesgoAsociado;
    }

    public double getGananciaObtenida() {
        return this.gananciaObtenida;
    }

    public void setGananciaObtenida(double gananciaObtenida) {
        this.gananciaObtenida = gananciaObtenida;
    }

    public String toString() {
        return "CultivoSeleccionado{nombreCultivo='" + this.nombreCultivo + '\'' + ", esquinaSuperiorIzquierda=" + this.esquinaSuperiorIzquierda + ", esquinaInferiorDerecha=" + this.esquinaInferiorDerecha + ", montoInvertido=" + this.montoInvertido + ", riesgoAsociado=" + this.riesgoAsociado + ", gananciaObtenida=" + this.gananciaObtenida + '}';
    }
}