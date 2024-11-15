package implementacion;

import Lib.Cultivo;
import Lib.CultivoSeleccionado;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanificarCultivos implements Lib.PlanificarCultivos {
    @Override
    public List<CultivoSeleccionado> obtenerPlanificacion(List<Cultivo>  cultivos, double[][] coordenadas, String temporadaActual) {
        List<CultivoSeleccionado> distribucionActual = new ArrayList<>();
        List<CultivoSeleccionado> mejorDistribucion = new ArrayList<>();
        double[][] campo = new double[100][100];
        double[][] matrizRiesgo = coordenadas;

        mejorDistribucion = backtracking(campo, cultivos, temporadaActual, 0, 0, distribucionActual, mejorDistribucion, matrizRiesgo);

        return mejorDistribucion;
    }

    private List<CultivoSeleccionado> backtracking(double[][] campo, List<Cultivo> cultivos, String temporadaActual, int x, int y,
                              List<CultivoSeleccionado> distribucionActual, List<CultivoSeleccionado> mejorDistribucion,double[][] matrizRiesgo ){
        if (x>=campo.length){
            double gananciaActual= sumaGanancias(distribucionActual);
            double gananciaMejor=sumaGanancias(mejorDistribucion);

            if (gananciaActual>gananciaMejor){
                mejorDistribucion=distribucionActual;
            }
            return mejorDistribucion;
        }

        for (int i=0; i<cultivos.size(); i++){
            Cultivo cultivo=cultivos.get(i);
            if(Objects.equals(cultivo.getTemporadaOptima(), temporadaActual)){
                CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado();

                if(puedoUbicar(campo,cultivoSeleccionado,  x, y)){
                    double ganancia=gananciaCultivo(cultivo,campo, x, y, cultivoSeleccionado, matrizRiesgo);
                    double riesgoPromedio=riesgoPromedio(cultivoSeleccionado, campo);

                    distribucionActual.add(cultivoSeleccionado);


                    int sig_x = x;
                    int sig_y = y + (cultivoSeleccionado.getEsquinaInferiorDerecha().getY() - cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() + 1); // Altura del cultivo
                    if (sig_y >= campo[0].length) {
                        sig_x += 1; // Si llegamos al final de la fila, avanzamos a la siguiente fila
                        sig_y = 0; // Reiniciamos a la primera columna
                    }

                    mejorDistribucion = backtracking(campo, cultivos, temporadaActual, sig_x, sig_y, distribucionActual, mejorDistribucion, matrizRiesgo);

                    distribucionActual.remove(distribucionActual.size() - 1);
                }

            }

        }

        return mejorDistribucion;
    }

    private boolean puedoUbicar(double [][] campo,CultivoSeleccionado cultivoSeleccionado, int x, int y){
        if (cultivoSeleccionado.getEsquinaInferiorDerecha().getX() >= campo.length ||
                cultivoSeleccionado.getEsquinaInferiorDerecha().getY() >= campo[0].length) {
            return false;
        }

        // Recorre el área del cultivo seleccionado y revisa si no está ocupada la parcela
        for (int i = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
             i <= cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
             i++) {
            for (int j = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
                 j <= cultivoSeleccionado.getEsquinaInferiorDerecha().getY();
                 j++) {
                if (campo[i][j] != 0.0) { // O != null segun como sean las parcelas vacías
                    return false;
                }
            }
        }

        // Recorre el área del cultivo seleccionado y revisa si no está ocupada la parcela
        for (int i = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
             i <= cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
             i++) {
            for (int j = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
                 j <= cultivoSeleccionado.getEsquinaInferiorDerecha().getY();
                 j++) {
                if (campo != null) {
                    return false;
                }
            }
        }

        // Valida que no supere el campo por izquierda y por arriba
        if (cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() < 0 ||
                cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() < 0) {
            return false;
        }


        if ((cultivoSeleccionado.getEsquinaInferiorDerecha().getX() - cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() +
                cultivoSeleccionado.getEsquinaInferiorDerecha().getY() - cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY()) > 11) {
            return false;
        }

        return true;
    }

    public void Repetircultivo(String[][] campo, CultivoSeleccionado cultivoSeleccionado) {
        // Recorre cada celda del campo
        for (int i = 0; i < campo.length; i++) {
            for (int j = 0; j < campo[0].length; j++) {
                // Verifica si la celda está vacía
                if (campo[i][j] == null || campo[i][j].isEmpty()) {
                    // Verifica si no hay colisión al colocar el cultivo seleccionado
                    if (colisionan(campo, cultivoSeleccionado, i, j)) {
                        // Coloca el nombre del cultivo en la celda
                        campo[i][j] = cultivoSeleccionado.getNombreCultivo();
                    }
                }
            }
        }
    }


    private void rellenar(CultivoSeleccionado[][] campo, CultivoSeleccionado cultivoSeleccionado, CultivoSeleccionado ultimoCultivo) {
        for (int i = 0; i < campo.length; i++) {
            for (int j = 0; j < campo[0].length; j++) {
                if (campo[i][j] ==null) { // en vez de null 0.0
                    if (ultimoCultivo != null && ultimoCultivo.equals(cultivoSeleccionado)) { // Si el ultimo cultivo es igual al cultivo que se está tratando de rellenar
                        continue; // Pasar al siguiente cultivo si es el mismo
                    }
                    campo[i][j] = cultivoSeleccionado; // Rellenar la parcela con el cultivo
                }
            }
        }
    }




    private double calcularPotencial(double riesgoAsociado, double precioDeVentaPorParcela, double costoPorParcela){
        return (1-riesgoAsociado)*(precioDeVentaPorParcela-costoPorParcela);
    }
    private double gananciaCultivo(Cultivo cultivo,double[][] campo, int x, int y, CultivoSeleccionado cultivoSeleccionado,  double[][] matrizRiesgo){
        int inicioX = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
        int inicioY = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
        int finX = cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
        int finY = cultivoSeleccionado.getEsquinaInferiorDerecha().getY();
        double potencialTotal=0;
        double riesgoTotal=0;


        for (int i= inicioX;i<finX;i++){
            for (int j = inicioY; j <= finY; j++) {
                double riesgo = matrizRiesgo[i][j];
                potencialTotal += calcularPotencial(riesgo, cultivo.getPrecioDeVentaPorParcela(), cultivo.getCostoPorParcela());
                riesgoTotal += riesgo;
            }
        }

        double ganancia=potencialTotal-cultivo.getInversionRequerida();

        return ganancia;

    }

    private double riesgoPromedio(CultivoSeleccionado cultivoSeleccionado, double[][] campo) {
        Lib.Coordenada esquinaSuperiorIzquierda = cultivoSeleccionado.getEsquinaSuperiorIzquierda();
        Lib.Coordenada esquinaInferiorDerecha = cultivoSeleccionado.getEsquinaInferiorDerecha();

        int xInicio = esquinaSuperiorIzquierda.getX();
        int yInicio = esquinaSuperiorIzquierda.getY();
        int filas = esquinaInferiorDerecha.getX() - xInicio + 1;
        int columnas = esquinaInferiorDerecha.getY() - yInicio + 1;

        double riesgoTotal = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                riesgoTotal += campo[xInicio + i][yInicio + j];
            }
        }

        double riesgoPromedio = riesgoTotal / (filas * columnas);
        return riesgoPromedio;
    }
    private boolean colisionan(String[][] campo, CultivoSeleccionado cultivoSeleccionado, int i, int j) {
        // Verifica si hay un cultivo del mismo tipo en la celda de arriba
        if (i > 0 && campo[i - 1][j] != null && campo[i - 1][j].equals(cultivoSeleccionado.getNombreCultivo())) {
            return false;
        }

        // Verifica si hay un cultivo del mismo tipo en la celda de abajo
        if (i < campo.length - 1 && campo[i + 1][j] != null && campo[i + 1][j].equals(cultivoSeleccionado.getNombreCultivo())) {
            return false;
        }

        // Verifica si hay un cultivo del mismo tipo en la celda de la izquierda
        if (j > 0 && campo[i][j - 1] != null && campo[i][j - 1].equals(cultivoSeleccionado.getNombreCultivo())) {
            return false;
        }

        // Verifica si hay un cultivo del mismo tipo en la celda de la derecha
        if (j < campo[0].length - 1 && campo[i][j + 1] != null && campo[i][j + 1].equals(cultivoSeleccionado.getNombreCultivo())) {
            return false;
        }

        // Si no hay colisiones, devuelve verdadero
        return true;
    }




    private double sumaGanancias(List<CultivoSeleccionado> distribucion){
        double total=0;
        for(int i=0; i< distribucion.size();i++){
            total+=distribucion.get(i).getGananciaObtenida();
        }
        return total;
    }
}
