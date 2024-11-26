package implementacion;

import java.util.*;

public class PlanificarCultivos implements PlanificadorCultivos {

    @Override
    public List<CultivoSeleccionado> obtenerPlanificacion(List<Cultivo> var1, double[][] var2, String var3) {
        List<CultivoSeleccionado> mejorDistribucion = new ArrayList<>();

        List<CultivoSeleccionado> distribucionActual = new ArrayList<>();

        double[][] campo = new double[100][100];

        mejorDistribucion = backtracking(0, var1, campo, 0.0, distribucionActual, 0.0, mejorDistribucion, var3, var2);

        List<CultivoSeleccionado> resultadoRellenado = rellenarEspaciosFer(var1.get(var1.size() - 1), mejorDistribucion, var2);

//        System.out.println("Estado final del campo:");
//        imprimirCampo(campo);
        return  resultadoRellenado;
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
                            if (puedeUbicar(cultivoSeleccionado, campo) &&
                                    validarRestriccionUnion(cultivoSeleccionado, distribucionActual)){
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
            System.out.println("estoy eliminando");
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
        int xInicio = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
        int yInicio = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
        int xFin = cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
        int yFin = cultivoSeleccionado.getEsquinaInferiorDerecha().getY();

        // Verificar si el cultivo está fuera de los límites del campo
        if (xInicio < 0 || yInicio < 0 || xFin >= campo.length || yFin >= campo[0].length) {
            return false;
        }

        // Verificar si el área está libre
        for (int x = xInicio; x <= xFin; x++) {
            for (int y = yInicio; y <= yFin; y++) {
                if (campo[x][y] == 1.0) { // Región ya ocupada
                    return false;
                }
            }
        }

        return true;
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
    }

    private List<CultivoSeleccionado> rellenarEspaciosFer(Cultivo cultivo, List<CultivoSeleccionado> distribucionActual, double[][] campo) {
        for(int n=1;n<=10;n++){
            for(int m =1;m<=10;m++){
                if(n+m<=11){
                    for (int x =0 ; x <= 100 - n; x++) {
                        for (int y = 0; y <= 100 - m; y++) {
                            Coordenada izq = new Coordenada(x, y);
                            Coordenada der = new Coordenada(x + n - 1, y + m - 1);
                            CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado();
                            cultivoSeleccionado.setNombreCultivo(cultivo.getNombre());
                            cultivoSeleccionado.setEsquinaInferiorDerecha(der);
                            cultivoSeleccionado.setEsquinaSuperiorIzquierda(izq);

                            // Validar restricciones y que no se solape con la distribución actual
                            if (puedeUbicar(cultivoSeleccionado, campo) &&
                                    validarRestriccionUnion(cultivoSeleccionado, distribucionActual)) {
                                // Calcular métricas
                                double riesgoPromedio = calcularRiesgoPromedio(campo, cultivoSeleccionado);
                                double ganancia = CalcularPotencial(x, y, x + n, y + m, cultivo, campo) - cultivo.getInversionRequerida();

                                // Asegurarse de que la región sea válida antes de usarla
                                if (Double.isFinite(riesgoPromedio)) {
                                    cultivoSeleccionado.setGananciaObtenida(ganancia);
                                    cultivoSeleccionado.setMontoInvertido(cultivo.getInversionRequerida());
                                    cultivoSeleccionado.setRiesgoAsociado(riesgoPromedio);

                                    // Marcar la región ocupada
                                    distribucionActual.add(cultivoSeleccionado);
                                    marcarComoOcupado(cultivoSeleccionado, campo);
                                }

                            }
                        }
                    }
                }
            }
        }
        return distribucionActual;
    }

    private void rellenarEspacios(Cultivo cultivo, double[][] campo, List<CultivoSeleccionado> distribucionActual, double[][] matrizRiesgo) {
        for (int x = 0; x < campo.length; x++) {
            for (int y = 0; y < campo[0].length; y++) {
                for (int n = 1; n <= 10; n++) {
                    for (int m = 1; m <= 10; m++) {
                        if (n + m <= 11) {
                            Coordenada izq = new Coordenada(x, y);
                            Coordenada der = new Coordenada(x + n - 1, y + m - 1);
                            CultivoSeleccionado cultivoSeleccionado=new CultivoSeleccionado();
                            cultivoSeleccionado.setNombreCultivo(cultivo.getNombre());
                            cultivoSeleccionado.setEsquinaInferiorDerecha(der);
                            cultivoSeleccionado.setEsquinaSuperiorIzquierda(izq);
                            if (puedeUbicar(cultivoSeleccionado, campo)) {
                                double riesgoPromedio = calcularRiesgoPromedio(matrizRiesgo, cultivoSeleccionado);
                                double ganancia = CalcularPotencial(x, y, x + n, y + m, cultivo, matrizRiesgo) - cultivo.getInversionRequerida();

                                cultivoSeleccionado.setGananciaObtenida(ganancia);
                                cultivoSeleccionado.setMontoInvertido(cultivo.getInversionRequerida());
                                cultivoSeleccionado.setRiesgoAsociado(riesgoPromedio);

                                distribucionActual.add(cultivoSeleccionado);
                                marcarComoOcupado(cultivoSeleccionado, campo);
                            }
                        }
                    }
                }
            }
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