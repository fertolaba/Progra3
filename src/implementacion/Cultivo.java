package implementacion;

public class Cultivo {
    private String nombre;
    private double costoPorParcela;
    private double inversionRequerida;
    private double precioDeVentaPorParcela;
    private String temporadaOptima;

    public Cultivo() {
    }

    public Cultivo(String nombre, double costoPorParcela, double inversionRequerida, double precioDeVentaPorParcela, String temporadaOptima) {
        this.nombre = nombre;
        this.costoPorParcela = costoPorParcela;
        this.inversionRequerida = inversionRequerida;
        this.precioDeVentaPorParcela = precioDeVentaPorParcela;
        this.temporadaOptima = temporadaOptima;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCostoPorParcela() {
        return this.costoPorParcela;
    }

    public void setCostoPorParcela(double costoPorParcela) {
        this.costoPorParcela = costoPorParcela;
    }

    public double getInversionRequerida() {
        return this.inversionRequerida;
    }

    public void setInversionRequerida(double inversionRequerida) {
        this.inversionRequerida = inversionRequerida;
    }

    public double getPrecioDeVentaPorParcela() {
        return this.precioDeVentaPorParcela;
    }

    public void setPrecioDeVentaPorParcela(double precioDeVentaPorParcela) {
        this.precioDeVentaPorParcela = precioDeVentaPorParcela;
    }

    public String getTemporadaOptima() {
        return this.temporadaOptima;
    }

    public void setTemporadaOptima(String temporadaOptima) {
        this.temporadaOptima = temporadaOptima;
    }
}
