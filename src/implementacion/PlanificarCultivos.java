package implementacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanificarCultivos implements PlanificadorCultivos {

    @Override
    public List<CultivoSeleccionado> obtenerPlanificacion(List<Cultivo> var1, double[][] var2, String var3) {
        System.out.println("Iniciando obtención de planificación...");
        List<CultivoSeleccionado> distribucionActual = new ArrayList<>();
        List<CultivoSeleccionado> mejorDistribucion = new ArrayList<>();
        double[][] campo = new double[100][100];

        return backtracking(0, var1, campo, 0.0, distribucionActual, 0.0, mejorDistribucion, var3, var2);
    }

    private List<CultivoSeleccionado> backtracking(int nivel, List<Cultivo> cultivos, double[][] campo, double gananciaActual,
                                                   List<CultivoSeleccionado> distribucionActual, double mejorGanancia,
                                                   List<CultivoSeleccionado> mejorDistribucion, String temporadaActual, double[][] matrizRiesgo) {
        System.out.println("Ciclo backtracking - Nivel: " + nivel);

        if (nivel >= cultivos.size()) {
            System.out.println("Fin de nivel alcanzado con ganancia actual: " + gananciaActual);
            if (gananciaActual > mejorGanancia) {
                mejorDistribucion.clear();
                mejorDistribucion.addAll(new ArrayList<>(distribucionActual));
                System.out.println("Nueva mejor distribución encontrada con ganancia: " + gananciaActual);
            }
            return mejorDistribucion;
        }

        Cultivo cultivo = cultivos.get(nivel);
        Coordenada esquinaSuperiorIzquierda = new Coordenada();
        Coordenada esquinaInferiorDerecha = new Coordenada();
        double riesgoPromedio = 0;
        double ganancia = 0;

        for (int n = 1; n <= 10; n++) {
            for (int m = 1; m <= 10; m++) {
                for (int x = 0; x <= 100 - n; x++) {
                    for (int y = 0; y <= 100 - m; y++) {
                        // Se asignan dinámicamente las coordenadas
                        esquinaSuperiorIzquierda.setX(x);
                        esquinaSuperiorIzquierda.setY(y);
                        esquinaInferiorDerecha.setX(x + n);
                        esquinaInferiorDerecha.setY(y + m);

                        // Verifica si el cultivo puede ser ubicado en esa área
                        if (puedoUbicar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, campo)) {
                            // Calcula el riesgo promedio y la ganancia para el cultivo en esa área
                            riesgoPromedio = calculariesgoPromedio(esquinaSuperiorIzquierda.getX(), esquinaSuperiorIzquierda.getY(),
                                    esquinaInferiorDerecha.getX(), esquinaInferiorDerecha.getY(), matrizRiesgo);
                            ganancia = gananciaCultivo(cultivo, new CultivoSeleccionado(cultivo.getNombre(), esquinaSuperiorIzquierda,
                                            esquinaInferiorDerecha, cultivo.getInversionRequerida(), (int) riesgoPromedio, ganancia), matrizRiesgo,
                                    esquinaSuperiorIzquierda.getX(), esquinaSuperiorIzquierda.getY(),
                                    esquinaInferiorDerecha.getX(), esquinaInferiorDerecha.getY());

                            // Si el cultivo puede ser ubicado, se agrega a la distribución
                            if (rellenar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, distribucionActual, campo)) {
                                CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado(cultivo.getNombre(),
                                        esquinaSuperiorIzquierda, esquinaInferiorDerecha, cultivo.getInversionRequerida(),
                                        (int) riesgoPromedio, ganancia);
                                distribucionActual.add(cultivoSeleccionado);
                                System.out.println("Cultivo añadido: " + cultivoSeleccionado.getNombreCultivo());

                                // Llama recursivamente al backtracking para el siguiente cultivo
                                mejorDistribucion = backtracking(nivel + 1, cultivos, campo, gananciaActual + ganancia,
                                        distribucionActual, mejorGanancia, mejorDistribucion, temporadaActual, matrizRiesgo);

                                // Elimina el cultivo de la distribución actual y vuelve atrás
                                distribucionActual.remove(distribucionActual.size() - 1);
                                System.out.println("Volviendo atrás, distribuciones actuales: " + distribucionActual.size() + " cultivos.");
                            }
                        }
                    }
                }
            }
        }

        return mejorDistribucion;
    }


    private boolean puedoUbicar(Coordenada esquinaSuperiorIzquierda, Coordenada esquinaInferiorDerecha, double[][] campo) {
        System.out.println("Verificando si se puede ubicar el cultivo en la posición: " +
                "Izq: (" + esquinaSuperiorIzquierda.getX() + ", " + esquinaSuperiorIzquierda.getY() + ") " +
                "Der: (" + esquinaInferiorDerecha.getX() + ", " + esquinaInferiorDerecha.getY() + ")");

        // Verifica si las coordenadas superiores e inferiores están dentro del límite del campo
        if (esquinaInferiorDerecha.getX() >= campo.length ||
                esquinaInferiorDerecha.getY() >= campo[0].length ||
                esquinaSuperiorIzquierda.getX() < 0 ||
                esquinaSuperiorIzquierda.getY() < 0) {
            System.out.println("No se puede ubicar, fuera de los límites.");
            return false;
        }

        // Recorre el área del cultivo seleccionado y revisa si no está ocupada la parcela
        for (int i = esquinaSuperiorIzquierda.getX(); i <= esquinaInferiorDerecha.getX(); i++) {
            for (int j = esquinaSuperiorIzquierda.getY(); j <= esquinaInferiorDerecha.getY(); j++) {
                if (i >= campo.length || j >= campo[0].length) {
                    System.out.println("No se puede ubicar, fuera de los límites al verificar celdas.");
                    return false;
                }
                // Si la celda está ocupada por cualquier valor mayor a 0.0 (por ejemplo, 0.1), no se puede ubicar
                if (campo[i][j] > 0.0) {
                    System.out.println("No se puede ubicar, área ocupada en la celda: (" + i + ", " + j + ")");
                    return false;
                }
            }
        }

        // Verifica que la suma de filas y columnas del área a plantar no sea mayor a 11
        if ((esquinaInferiorDerecha.getX() - esquinaSuperiorIzquierda.getX() +
                esquinaInferiorDerecha.getY() - esquinaSuperiorIzquierda.getY()) > 11) {
            System.out.println("No se puede ubicar, el área es demasiado grande.");
            return false;
        }

        return true;
    }




    private boolean rellenar(Coordenada izq, Coordenada der, List<CultivoSeleccionado> distribucionActual, double[][] campo) {
        System.out.println("Rellenando área con cultivo desde: (" + izq.getX() + ", " + izq.getY() + ") hasta: (" + der.getX() + ", " + der.getY() + ")");
        for (CultivoSeleccionado cultivo : distribucionActual) {
            // Llamada a la función de validación de ubicación
            if (!puedoUbicar(cultivo.getEsquinaSuperiorIzquierda(), cultivo.getEsquinaInferiorDerecha(), campo)) {
                System.out.println("No se puede colocar el cultivo, posición ocupada.");
                return false;
            }
        }
        // Marca las áreas ocupadas si es posible colocar el cultivo
        for (int i = izq.getX(); i <= der.getX(); i++) {
            for (int j = izq.getY(); j <= der.getY(); j++) {
                campo[i][j] = 1.0;  // Marca la celda como ocupada
            }
        }
        return true;
    }

    private double calcularPotencial(Cultivo cultivo, double[][] matrizRiesgo, int xInicio, int yInicio, int xFin, int yFin) {
        System.out.println("Calculando potencial para cultivo " + cultivo.getNombre() + " en el área: (" + xInicio + ", " + yInicio + ") a (" + xFin + ", " + yFin + ")");
        double suma = 0;
        for (int i = xInicio; i < xFin; i++) {
            for (int j = yInicio; j < yFin; j++) {
                suma += ((1 - matrizRiesgo[i][j])) * ((cultivo.getPrecioDeVentaPorParcela() - cultivo.getCostoPorParcela()));
            }
        }
        return suma;
    }

    private double gananciaCultivo(Cultivo cultivo, CultivoSeleccionado cultivoSeleccionado, double[][] matrizRiesgo, int xInicio, int yInicio, int xFin, int yFin) {
        System.out.println("Calculando ganancia para cultivo " + cultivo.getNombre());
        int inicioX = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
        int inicioY = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
        int finX = cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
        int finY = cultivoSeleccionado.getEsquinaInferiorDerecha().getY();
        double potencialTotal = 0;

        for (int i = inicioX; i < finX; i++) {
            for (int j = inicioY; j < finY; j++) {
                double riesgo = matrizRiesgo[i][j];
                potencialTotal += calcularPotencial(cultivo, matrizRiesgo, xInicio, yInicio, xFin, yFin);
            }
        }
        return potencialTotal - cultivo.getInversionRequerida();
    }

    private double calculariesgoPromedio(int xInicio, int yInicio, int xFin, int yFin, double[][] matrizRiesgo) {
        System.out.println("Calculando riesgo promedio en el área: (" + xInicio + ", " + yInicio + ") a (" + xFin + ", " + yFin + ")");
        double riesgoTotal = 0;
        int contador = 0;

        // Recorre la matriz en el rango especificado, asegurándose de que los índices estén dentro de los límites
        for (int i = xInicio; i < xFin; i++) {
            for (int j = yInicio; j < yFin; j++) {
                // Asegura que los índices no estén fuera de los límites de la matriz
                if (i >= 0 && i < matrizRiesgo.length && j >= 0 && j < matrizRiesgo[i].length) {
                    riesgoTotal += matrizRiesgo[i][j];
                    contador++;
                }
            }
        }

        // Si el contador es 0 (por ejemplo, si no se ha encontrado ninguna celda válida), evita la división por cero
        if (contador == 0) {
            return 0; // Retorna 0 si no se han encontrado celdas válidas
        }

        // Calcula el riesgo promedio
        return riesgoTotal / contador;
    }


}
