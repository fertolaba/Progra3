package implementacion;

public class Cultivo {
    private String nombre;
    private int filas;
    private int columnas;
    private double costoParcela;
    private double precioVenta;
    private double costoInversion;
    private String temporada;


    public Cultivo(String nombre, int filas, int columnas, double costoParcela, double precioVenta,double costoInversion, String temporada) {
        this.nombre = nombre;
        this.filas = filas;
        this.columnas = columnas;
        this.costoParcela = costoParcela;
        this.precioVenta = precioVenta;
        this.costoInversion=costoInversion;
        this.temporada = temporada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getFilas() {
        return filas;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }

    public double getCostoParcela() {
        return costoParcela;
    }

    public void setCostoParcela(double costoParcela) {
        this.costoParcela = costoParcela;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public double getCostoInversion() {
        return costoInversion;
    }

    public void setCostoInversion(double costoInversion) {
        this.costoInversion = costoInversion;
    }

    public String getTemporada() {
        return temporada;
    }

    public void setTemporada(String temporada) {
        this.temporada = temporada;
    }
}
