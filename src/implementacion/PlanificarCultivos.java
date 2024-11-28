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
            //System.out.println("Fin del backtracking. Ganancia actual: " + gananciaActual);
            if (gananciaActual > mejorGanancia) {
                mejorDistribucion.clear();
                mejorDistribucion.addAll(distribucionActual);
                //System.out.println("Nueva mejor distribución encontrada con ganancia: " + gananciaActual);
//                int i=cultivos.size()-1;
//                while(!Objects.equals(cultivos.get(i).getTemporadaOptima(), temporadaActual)){
//                    i--;
//                }
//                mejorDistribucion=rellenarEspacios(cultivos.get(i), mejorDistribucion, campo,matrizRiesgo);
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
                                //rellenar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, distribucionActual, campo);
                                if (puedeUbicar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, campo, distribucionActual) ){
                                    double riesgoPromedio = calcularRiesgoPromedio(x, y, x + n, y + m, matrizRiesgo);

                                    double potencialTotal = CalcularPotencial(x, y, x + n, y + m, cultivo, matrizRiesgo);
                                    double ganancia = potencialTotal - cultivo.getInversionRequerida();

                                    CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado(
                                            cultivo.getNombre(), esquinaSuperiorIzquierda, esquinaInferiorDerecha,
                                            cultivo.getInversionRequerida(), riesgoPromedio, ganancia);



                                    if (compararGanancias(distribucionActual, mejorDistribucion, ganancia, gananciaActual)) {
                                        distribucionActual.add(cultivoSeleccionado);
                                        //System.out.println("llamada x etapa");
                                        mejorDistribucion = backtracking(etapa + 1, cultivos, campo, gananciaActual + ganancia,
                                                distribucionActual, mejorGanancia, mejorDistribucion, temporadaActual, matrizRiesgo);

                                        distribucionActual.remove(distribucionActual.size() - 1);
                                        //System.out.println("volviendo atras");
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }

        return mejorDistribucion;
    }

    private boolean puedeUbicar(Coordenada esquinaSuperiorIzquierda, Coordenada esquinaInferiorDerecha, double[][] campo, List<CultivoSeleccionado> distribucionActual) {
        if (esquinaInferiorDerecha.getX() >= campo.length ||
                esquinaInferiorDerecha.getY() >= campo[0].length ||
                esquinaSuperiorIzquierda.getX() < 0 ||
                esquinaSuperiorIzquierda.getY() < 0) {
            return false;
        }

        for (CultivoSeleccionado cultivo : distribucionActual) {
            Coordenada cultivoEsquinaIzq = cultivo.getEsquinaSuperiorIzquierda();
            Coordenada cultivoEsquinaDer = cultivo.getEsquinaInferiorDerecha();

            if (esquinaSuperiorIzquierda.getX() <= cultivoEsquinaDer.getX() && esquinaInferiorDerecha.getX() >= cultivoEsquinaIzq.getX() &&
                    esquinaSuperiorIzquierda.getY() <= cultivoEsquinaDer.getY() && esquinaInferiorDerecha.getY() >= cultivoEsquinaIzq.getY()) {
                return false;
            }
        }

        for (int i = esquinaSuperiorIzquierda.getX(); i <= esquinaInferiorDerecha.getX(); i++) {
            for (int j = esquinaSuperiorIzquierda.getY(); j <= esquinaInferiorDerecha.getY(); j++) {
                if (campo[i][j] == 1.0) {
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

    private List<CultivoSeleccionado> rellenarEspacios(Cultivo cultivo, List<CultivoSeleccionado> distribucionActual, double[][] campo, double[][]matrizRiesgo) {
        for(int x=0;x<=100;x++){
            for(int y =1;y<=100;y++){
                for (int n =1 ; n <= 10 ; n++) {
                    for (int m = 1; m <= 10 ; m++) {
                        if(n+m<=11){
                            Coordenada izq = new Coordenada(x, y);
                            Coordenada der = new Coordenada(x + n - 1, y + m - 1);


                            // Validar restricciones y que no se solape con la distribución actual
                            if (puedeUbicar(izq, der, campo, distribucionActual) ){
                                double riesgoPromedio = calcularRiesgoPromedio(x, y, x + n, y + m, matrizRiesgo);

                                double potencialTotal = CalcularPotencial(x, y, x + n, y + m, cultivo, matrizRiesgo);
                                double ganancia = potencialTotal - cultivo.getInversionRequerida();

                                CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado(
                                        cultivo.getNombre(), izq, der,
                                        cultivo.getInversionRequerida(), riesgoPromedio, ganancia);

                            }
                            //System.out.println("no puedo ubicar");
                        }
                    }

                }

            }
        }
        return distribucionActual;

    }
    private boolean validarRestriccionUnion(CultivoSeleccionado cultivoNuevo, List<CultivoSeleccionado> distribucionActual) {
        for (CultivoSeleccionado cultivoExistente : distribucionActual) {
            // Verificar si los cultivos son adyacentes
            if (sonAdyacentes(cultivoNuevo, cultivoExistente)) {
                // Calcular el rectángulo combinado
                int xIzquierda = Math.min(cultivoNuevo.getEsquinaSuperiorIzquierda().getX(),
                        cultivoExistente.getEsquinaSuperiorIzquierda().getX());
                int ySuperior = Math.min(cultivoNuevo.getEsquinaSuperiorIzquierda().getY(),
                        cultivoExistente.getEsquinaSuperiorIzquierda().getY());
                int xDerecha = Math.max(cultivoNuevo.getEsquinaInferiorDerecha().getX(),
                        cultivoExistente.getEsquinaInferiorDerecha().getX());
                int yInferior = Math.max(cultivoNuevo.getEsquinaInferiorDerecha().getY(),
                        cultivoExistente.getEsquinaInferiorDerecha().getY());

                // Calcular dimensiones del rectángulo combinado
                int ancho = xDerecha - xIzquierda + 1;
                int alto = yInferior - ySuperior + 1;

                // Print para verificar los detalles del rectángulo combinado
//                System.out.println("Verificando unión:");
//                System.out.println("- Cultivo nuevo: " + cultivoNuevo);
//                System.out.println("- Cultivo existente: " + cultivoExistente);
//                System.out.println("- Rectángulo combinado -> Ancho: " + ancho + ", Alto: " + alto);

                // Verificar restricción: ancho + alto <= 11
                if (ancho + alto > 11) {
//                    System.out.println("Restricción violada: ancho + alto = " + (ancho + alto));
                    return false;
                }
            }
        }
//        System.out.println("Restricción cumplida para cultivo: " + cultivoNuevo);
        return true;
    }

    // Método auxiliar para verificar si dos cultivos son adyacentes
    private boolean sonAdyacentes(CultivoSeleccionado cultivo1, CultivoSeleccionado cultivo2) {
        // Verificar si comparten un borde horizontal o vertical sin solaparse
        boolean horizontalmenteAdyacentes =
                cultivo1.getEsquinaInferiorDerecha().getX() + 1 == cultivo2.getEsquinaSuperiorIzquierda().getX() ||
                        cultivo2.getEsquinaInferiorDerecha().getX() + 1 == cultivo1.getEsquinaSuperiorIzquierda().getX();

        boolean verticalmenteAdyacentes =
                cultivo1.getEsquinaInferiorDerecha().getY() + 1 == cultivo2.getEsquinaSuperiorIzquierda().getY() ||
                        cultivo2.getEsquinaInferiorDerecha().getY() + 1 == cultivo1.getEsquinaSuperiorIzquierda().getY();

        return horizontalmenteAdyacentes || verticalmenteAdyacentes;
    }  }