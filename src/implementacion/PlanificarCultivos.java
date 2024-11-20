package implementacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanificarCultivos implements PlanificadorCultivos {

    @Override
    public List<CultivoSeleccionado> obtenerPlanificacion(List<Cultivo> var1, double[][] var2, String var3) {
        System.out.println("Entrando en planificación...");
        List<CultivoSeleccionado> distribucionActual = new ArrayList<>();
        List<CultivoSeleccionado> mejorDistribucion = new ArrayList<>();
        double[][] campo = new double[100][100];

        return backtracking(0, var1, campo, 0.0, distribucionActual, 0.0, mejorDistribucion, var3, var2);
    }

    private List<CultivoSeleccionado> backtracking(
            int etapa, List<Cultivo> cultivos, double[][] campo, double gananciaActual,
            List<CultivoSeleccionado> distribucionActual, double mejorGanancia,
            List<CultivoSeleccionado> mejorDistribucion, String temporadaActual,
            double[][] matrizRiesgo) {

        if (etapa >= cultivos.size()) {
            System.out.println("Fin del backtracking. Ganancia actual: " + gananciaActual);
            if (gananciaActual > mejorGanancia) {
                mejorDistribucion.clear();
                mejorDistribucion.addAll(distribucionActual);
                System.out.println("Nueva mejor distribución encontrada con ganancia: " + gananciaActual);
            }
            return mejorDistribucion;
        }

        Cultivo cultivo = cultivos.get(etapa);

        if (!cultivo.getTemporadaOptima().equals(temporadaActual)) {
            return backtracking(etapa + 1, cultivos, campo, gananciaActual,
                    distribucionActual, mejorGanancia, mejorDistribucion, temporadaActual, matrizRiesgo);
        }

        for (int x = 0; x < campo.length; x++) {
            for (int y = 0; y < campo[0].length; y++) {
                for (int n = 1; n <= 10; n++) {
                    for (int m = 1; m <= 10; m++) {
                        if (n + m < 11) {
                            Coordenada esquinaSuperiorIzquierda = new Coordenada(x, y);
                            Coordenada esquinaInferiorDerecha = new Coordenada(x + n - 1, y + m - 1);
                            if (esquinaInferiorDerecha.getX() < campo.length && esquinaInferiorDerecha.getY() < campo[0].length) {
                                rellenar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, distribucionActual, campo);
                                double riesgoPromedio = calcularRiesgoPromedio(x, y, x + n, y + m, matrizRiesgo);

                                double potencialTotal = CalcularPotencial(x, y, x + n, y + m, cultivo, matrizRiesgo);
                                double ganancia = potencialTotal - cultivo.getInversionRequerida();

                                CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado(
                                        cultivo.getNombre(), esquinaSuperiorIzquierda, esquinaInferiorDerecha,
                                        cultivo.getInversionRequerida(), (int) riesgoPromedio, ganancia);

                                if (compararGanancias(distribucionActual, mejorDistribucion, ganancia, gananciaActual)) {
                                    distribucionActual.add(cultivoSeleccionado);
                                    mejorDistribucion = backtracking(etapa + 1, cultivos, campo, gananciaActual + ganancia,
                                            distribucionActual, mejorGanancia, mejorDistribucion, temporadaActual, matrizRiesgo);
                                    distribucionActual.remove(distribucionActual.size() - 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        return mejorDistribucion;
    }

    private boolean puedeUbicar(Coordenada esquinaSuperiorIzquierda, Coordenada esquinaInferiorDerecha, double[][] campo ) {
        if (esquinaInferiorDerecha.getX() >= campo.length ||
                esquinaInferiorDerecha.getY() >= campo[0].length ||
                esquinaSuperiorIzquierda.getX() < 0 ||
                esquinaSuperiorIzquierda.getY() < 0) {
            return false;
        }
        for (int i = esquinaSuperiorIzquierda.getX(); i <= esquinaInferiorDerecha.getX(); i++) {
            for (int j = esquinaSuperiorIzquierda.getY(); j <= esquinaInferiorDerecha.getY(); j++) {
                if (campo[i][j] > 0.0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean compararGanancias(List<CultivoSeleccionado> distribucionActual,List<CultivoSeleccionado> mejorDistribucion,double ganancia,double gananciaActual){
        double mejorGanancia = 0;
        int i =0;
        while(i<distribucionActual.size() && mejorDistribucion.size()>=distribucionActual.size()){
            mejorGanancia += mejorDistribucion.get(i).getGananciaObtenida();
            i++;
        }
        if(i==mejorDistribucion.size()){
            return gananciaActual + ganancia > mejorGanancia;
        }else{
            if(gananciaActual + ganancia > mejorGanancia + mejorDistribucion.get(i).getGananciaObtenida()){
                return true;
            }
            return false;
        }
    }

    private boolean rellenar(Coordenada izq, Coordenada der, List<CultivoSeleccionado> distribucionActual, double[][] campo) {

        for (int i = izq.getX(); i <= der.getX(); i++) {
            for (int j = izq.getY(); j <= der.getY(); j++) {
                if (campo[i][j] > 0.0) {
                    return false;
                }
            }
        }
        for (int i = izq.getX(); i <= der.getX(); i++) {
            for (int j = izq.getY(); j <= der.getY(); j++) {
                campo[i][j] = 1.0;
            }
        }
        for(CultivoSeleccionado cultivo : distribucionActual){
            if(!puedeUbicar(cultivo.getEsquinaSuperiorIzquierda(),cultivo.getEsquinaInferiorDerecha(), campo)){
                return  false;
            }
        }
        return true;
    }

    public double CalcularPotencial(int xInicio, int yInicio, int xFin, int yFin, Cultivo cultivo, double[][] matrizRiesgo) {
        double suma = 0;
        for (int i = xInicio; i < xFin; i++) {
            for (int j = yInicio; j < yFin; j++) {
                suma += ((1 - matrizRiesgo[i][j])) * ((cultivo.getPrecioDeVentaPorParcela() - cultivo.getCostoPorParcela()));
            }
        }
        return suma;
    }

    private double calcularRiesgoPromedio(int filaInicio, int columnaInicio, int filaFin, int columnaFin, double[][] matrizRiesgo) {
        double riesgoTotal = 0;
        int totalParcelas = 0;


        for (int i = filaInicio; i < filaFin; i++) {
            for (int j = columnaInicio; j < columnaFin; j++) {
                riesgoTotal += matrizRiesgo[i][j];
                totalParcelas++;
            }
        }
        return riesgoTotal / totalParcelas;
    }

}