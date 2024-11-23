package implementacion;

import java.util.*;

public class PlanificarCultivos implements PlanificadorCultivos {

    @Override
    public List<CultivoSeleccionado> obtenerPlanificacion(List<Cultivo> var1, double[][] var2, String var3) {
        List<CultivoSeleccionado> mejorDistribucion = new ArrayList<>();


        List<CultivoSeleccionado> distribucionActual = new ArrayList<>();


        double gananciaActual = 0;


        double[][] campo = new double[100][100];

        mejorDistribucion = backtracking(0, var1, campo, 0.0, distribucionActual, 0.0, mejorDistribucion, var3, var2);

        return  mejorDistribucion;
    }

    private List<CultivoSeleccionado> backtracking(
            int etapa, List<Cultivo> cultivos, double[][] campo, double gananciaActual,
            List<CultivoSeleccionado> distribucionActual, double mejorGanancia,
            List<CultivoSeleccionado> mejorDistribucion, String temporadaActual,
            double[][] matrizRiesgo) {

        if (etapa >= cultivos.size()) {
            System.out.println("Fin del backtracking. Ganancia actual: " + gananciaActual);
            if (gananciaActual > mejorGanancia) {
                mejorGanancia = gananciaActual;  // Actualizar la mejor ganancia
                mejorDistribucion = new ArrayList<>(distribucionActual);
            }
            return mejorDistribucion;
        }

        Cultivo cultivo = cultivos.get(etapa);

        if (!cultivo.getTemporadaOptima().equals(temporadaActual)) {
            return backtracking(etapa + 1, cultivos, campo, gananciaActual,
                    distribucionActual, mejorGanancia, mejorDistribucion, temporadaActual, matrizRiesgo);
        }
        double mejorGananciaEtapa = -Double.MAX_VALUE;
        CultivoSeleccionado mejorCultivoSeleccionado = null;

        for (int x = 0; x < campo.length; x++) {
            for (int y = 0; y < campo[0].length; y++) {
                for (int n = 1; n <= 10; n++) {
                    for (int m = 1; m <= 10; m++) {
                        if (n + m <= 11) {
                            CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado();
                            cultivoSeleccionado.setNombreCultivo(cultivo.getNombre());
                            cultivoSeleccionado.setEsquinaSuperiorIzquierda(new Coordenada(x, y));
                            cultivoSeleccionado.setEsquinaInferiorDerecha(new Coordenada(x + n - 1, y + m - 1));
                            if (puedeUbicar(cultivoSeleccionado, campo)) {
                                double riesgoPromedio = calcularRiesgoPromedio(matrizRiesgo, cultivoSeleccionado);

                                double potencialTotal = CalcularPotencial(x, y, x + n, y + m, cultivo, matrizRiesgo);
                                double ganancia = potencialTotal - cultivo.getInversionRequerida();

                                // Actualizar la mejor configuración de la etapa actual
                                if (ganancia > mejorGananciaEtapa) {
                                    mejorGananciaEtapa = ganancia;
                                    mejorCultivoSeleccionado = new CultivoSeleccionado(
                                            cultivo.getNombre(),
                                            new Coordenada(x, y),
                                            new Coordenada(x + n - 1, y + m - 1),
                                            cultivo.getInversionRequerida(),
                                            riesgoPromedio,
                                            ganancia
                                    );
                                }

                            }
                        }
                    }
                }
            }
        }
        if (mejorCultivoSeleccionado != null) {
            marcarComoOcupado(mejorCultivoSeleccionado, campo);
            distribucionActual.add(mejorCultivoSeleccionado);

            // Imprimir la mejor configuración encontrada en esta etapa
            System.out.println("Mejor configuración para el cultivo " + cultivo.getNombre() + ":");
            System.out.println("Ubicación: (" + mejorCultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() + ", " +
                    mejorCultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() + ")");
            System.out.println("Ancho: " + (mejorCultivoSeleccionado.getEsquinaInferiorDerecha().getX() -
                    mejorCultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() + 1));
            System.out.println("Alto: " + (mejorCultivoSeleccionado.getEsquinaInferiorDerecha().getY() -
                    mejorCultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() + 1));
            System.out.println("Ganancia: " + mejorGananciaEtapa);

            // Llamada recursiva para procesar el siguiente cultivo
            mejorDistribucion = backtracking(etapa + 1, cultivos, campo, gananciaActual + mejorGananciaEtapa,
                    distribucionActual, mejorGanancia, mejorDistribucion,
                    temporadaActual, matrizRiesgo);
            distribucionActual.remove(distribucionActual.size() - 1); // Retroceder
            liberarCasillas(mejorCultivoSeleccionado, campo);
        }

        return mejorDistribucion;
    }


    private void marcarComoOcupado(CultivoSeleccionado cultivoSeleccionado, double[][] campo) {
        for (int x = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
             x <= cultivoSeleccionado.getEsquinaInferiorDerecha().getX(); x++) {
            for (int y = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
                 y <= cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); y++) {
                campo[x][y] = 1.0; // Marcar como ocupado
            }
        }
    }


    private void liberarCasillas(CultivoSeleccionado cultivoSeleccionado, double[][] campo) {
        for (int x = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
             x <= cultivoSeleccionado.getEsquinaInferiorDerecha().getX(); x++) {
            for (int y = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
                 y <= cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); y++) {
                campo[x][y] = 0.0; // Marcar como disponible
            }
        }
    }


    private boolean puedeUbicar(CultivoSeleccionado cultivoSeleccionado, double[][] campo) {
        if (cultivoSeleccionado.getEsquinaInferiorDerecha().getX() >= campo.length ||
                cultivoSeleccionado.getEsquinaInferiorDerecha().getY() >= campo[0].length ||
                cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() < 0 ||
                cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() < 0) {
            return false; // Fuera de límites
        }

        for (int x = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
             x <= cultivoSeleccionado.getEsquinaInferiorDerecha().getX(); x++) {
            for (int y = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
                 y <= cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); y++) {
                if (campo[x][y] == 1.0) {
                    return false;
                }
            }
        }
        return true;
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

    private double calcularRiesgoPromedio(double[][] matrizRiesgo, CultivoSeleccionado cultivoSeleccionado) {
        double riesgoTotal = 0;
        int totalParcelas = 0;

        int filaInicio = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
        int columnaInicio = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
        int filaFin = cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
        int columnaFin = cultivoSeleccionado.getEsquinaInferiorDerecha().getY();

        for (int i = filaInicio; i < filaFin; i++) {
            for (int j = columnaInicio; j < columnaFin; j++) {
                riesgoTotal += matrizRiesgo[i][j];
                totalParcelas++;
            }
        }
        return riesgoTotal / totalParcelas;
    }

}