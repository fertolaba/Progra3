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



    private List<CultivoSeleccionado> backtracking(int etapa, List<Cultivo> cultivos, double[][] campo,
                                                   double gananciaActual, List<CultivoSeleccionado> distribucionActual,
                                                   double mejorGanancia, List<CultivoSeleccionado> mejorDistribucion,
                                                   String temporadaActual, double[][] matrizRiesgo) {

        if (etapa >= cultivos.size()) {
            if (gananciaActual > mejorGanancia) {
                mejorGanancia = gananciaActual;  // Actualizar la mejor ganancia
                mejorDistribucion = new ArrayList<>(distribucionActual);
            }
            return mejorDistribucion;
        }

        Cultivo cultivo = cultivos.get(etapa);
        System.out.println("Procesando cultivo: " + cultivo.getNombre() + " - Temporada: " + temporadaActual);

        // Verificar si el cultivo puede ser sembrado en la temporada actual
        if (!cultivo.getTemporadaOptima().equals(temporadaActual)) {
            return backtracking(etapa + 1, cultivos, campo, gananciaActual,
                    distribucionActual, mejorGanancia, mejorDistribucion,
                    temporadaActual, matrizRiesgo);
        }

        // Variables para rastrear la mejor configuración de esta etapa
        double mejorGananciaEtapa = -Double.MAX_VALUE;
        CultivoSeleccionado mejorCultivoSeleccionado = null;

        // Intentar colocar el cultivo en cada posible ubicación
        for (int x = 0; x < campo.length; x++) {
            for (int y = 0; y < campo[0].length; y++) {

                // Buscar todas las combinaciones de ancho y alto que cumplan con las restricciones
                for (int ancho = 1; ancho <= 10; ancho++) {
                    for (int alto = 1; alto <= 10; alto++) {
                        if (ancho + alto <= 11) { // Restricción de perímetro
                            CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado();
                            cultivoSeleccionado.setNombreCultivo(cultivo.getNombre());
                            cultivoSeleccionado.setEsquinaSuperiorIzquierda(new Coordenada(x, y));
                            cultivoSeleccionado.setEsquinaInferiorDerecha(new Coordenada(x + ancho - 1, y + alto - 1));

                            if (puedeUbicar(cultivoSeleccionado, campo)) {
                                // Calcular la ganancia y el riesgo usando los métodos actualizados
                                double[] gananciaYriesgo = calcularGananciaCultivo(cultivo, matrizRiesgo, cultivoSeleccionado);

                                double ganancia = gananciaYriesgo[0];
                                double riesgoPromedio = gananciaYriesgo[1];

                                // Imprimir información de cada configuración
                                System.out.println("Configuración: Ancho = " + ancho + ", Alto = " + alto + ", Área = " + (ancho * alto));
                                System.out.println("Ganancia: " + ganancia + ", Riesgo promedio: " + riesgoPromedio);

                                // Actualizar la mejor configuración de la etapa actual
                                if (ganancia > mejorGananciaEtapa) {
                                    mejorGananciaEtapa = ganancia;
                                    mejorCultivoSeleccionado = new CultivoSeleccionado(
                                            cultivo.getNombre(),
                                            new Coordenada(x, y),
                                            new Coordenada(x + ancho - 1, y + alto - 1),
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

        // Si encontramos una configuración válida en esta etapa
        if (mejorCultivoSeleccionado != null) {
            marcarComoOcupado(mejorCultivoSeleccionado);
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
            liberarCasillas(mejorCultivoSeleccionado);
        }

        return mejorDistribucion;
    }




    // HashSet para almacenar las coordenadas ocupadas
    private Set<String> casillasOcupadas = new HashSet<>();






    private boolean puedeUbicar(CultivoSeleccionado cultivoSeleccionado, double[][] campo) {
        System.out.println("Verificando si puede ubicar cultivo " + cultivoSeleccionado.getNombreCultivo() + " en ("
                + cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() + ", "
                + cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() + ")");

        // Validar que las coordenadas estén dentro de los límites del campo
        if (cultivoSeleccionado.getEsquinaInferiorDerecha().getX() >= campo.length ||
                cultivoSeleccionado.getEsquinaInferiorDerecha().getY() >= campo[0].length) {
            System.out.println("No cabe en el campo: Esquina inferior derecha fuera de los límites.");
            return false;
        }

        // Validar que las coordenadas sean válidas
        if (cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() > cultivoSeleccionado.getEsquinaInferiorDerecha().getX() ||
                cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() > cultivoSeleccionado.getEsquinaInferiorDerecha().getY()) {
            System.out.println("Coordenadas no válidas: superior izquierda está por debajo de inferior derecha.");
            return false;
        }

        // Validar que N + M <= 11
        int anchura = cultivoSeleccionado.getEsquinaInferiorDerecha().getX() - cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() + 1;
        int altura = cultivoSeleccionado.getEsquinaInferiorDerecha().getY() - cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() + 1;

        if (anchura + altura > 11) {
            System.out.println("No cabe en el campo: N + M > 11.");
            return false;
        }


        // Verificar si las casillas están ocupadas
        for (int x = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX(); x <= cultivoSeleccionado.getEsquinaInferiorDerecha().getX(); x++) {
            for (int y = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY(); y <= cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); y++) {
                String coordenada = x + "," + y;
                if (casillasOcupadas.contains(coordenada)) {
                    System.out.println("No se puede ubicar el cultivo: Casilla ocupada en (" + x + ", " + y + ")");
                    return false;
                }
            }
        }

        System.out.println("Ubicación válida.");
        return true;
    }
    private void marcarComoOcupado(CultivoSeleccionado cultivoSeleccionado) {
        for (int x = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX(); x <= cultivoSeleccionado.getEsquinaInferiorDerecha().getX(); x++) {
            for (int y = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY(); y <= cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); y++) {
                String coordenada = x + "," + y;
                casillasOcupadas.add(coordenada);
            }
        }
    }

    // Función para liberar las casillas ocupadas
    private void liberarCasillas(CultivoSeleccionado cultivoSeleccionado) {
        for (int x = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX(); x <= cultivoSeleccionado.getEsquinaInferiorDerecha().getX(); x++) {
            for (int y = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY(); y <= cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); y++) {
                String coordenada = x + "," + y;
                casillasOcupadas.remove(coordenada);
            }
        }
    }

    // Método para calcular la ganancia total de un cultivo
    private double[] calcularGananciaCultivo(Cultivo cultivo, double[][] matrizRiesgo, CultivoSeleccionado cultivoSeleccionado) {
        double gananciaTotal = 0;
        double sumaRiesgo = 0;
        int cantidadParcelas = 0;

        // Obtenemos las coordenadas del área del cultivo
        int xInicio = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
        int yInicio = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
        int xFin = cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
        int yFin = cultivoSeleccionado.getEsquinaInferiorDerecha().getY();

        // Iterar sobre las parcelas del área asignada
        for (int i = xInicio; i <= xFin; i++) {
            for (int j = yInicio; j <= yFin; j++) {
                // Calcular el potencial de la parcela
                double potencial = calcularPotencialPorParcela(matrizRiesgo[i][j], cultivo.getPrecioDeVentaPorParcela(), cultivo.getCostoPorParcela());
                gananciaTotal += potencial; // Sumar el potencial al total

                // Acumulamos el riesgo para calcular el promedio
                sumaRiesgo += matrizRiesgo[i][j];
                cantidadParcelas++;
            }
        }

        // Calcular el riesgo promedio
        double riesgoPromedio = cantidadParcelas > 0 ? sumaRiesgo / cantidadParcelas : 0;

        // Restar el costo de inversión del cultivo a la ganancia total
        gananciaTotal -= cultivo.getInversionRequerida();

        // Devolver la ganancia total y el riesgo promedio
        return new double[]{gananciaTotal, riesgoPromedio};
    }






    // Método para calcular el potencial de una parcela en la matriz
    public double calcularPotencialPorParcela(double riesgo, double precioDeVenta, double costoPorParcela) {
        return (1 - riesgo) * (precioDeVenta - costoPorParcela);
    }




}