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

        return backtracking(0, var1, campo, 0.0, distribucionActual, 0.0, mejorDistribucion, var3, var2, 1);
    }

    private List<CultivoSeleccionado> backtracking(int nivel, List<Cultivo> cultivos, double[][] campo, double gananciaActual,
                                                   List<CultivoSeleccionado> distribucionActual, double mejorGanancia,
                                                   List<CultivoSeleccionado> mejorDistribucion, String temporadaActual,
                                                   double[][] matrizRiesgo, int maxIntentos) {
        System.out.println("Ciclo backtracking - Nivel: " + nivel + ", Cultivos restantes: " + (cultivos.size() - nivel));

        // Verificar si se ha alcanzado el límite de intentos
        if (maxIntentos <= 0) {
            System.out.println("Límite de intentos alcanzado, deteniendo el algoritmo.");
            return mejorDistribucion;
        }

        // Fin del ciclo de backtracking cuando todos los cultivos hayan sido procesados
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
        double riesgoPromedio;
        double ganancia = 0;

        // Calcular todas las posiciones posibles para el cultivo en el campo
        for (int x = 0; x <= 100; x++) {
            for (int y = 0; y <= 100; y++) {
                for (int n = 1; n <= 10; n++) {
                    for (int m = 1; m <= 10; m++) {
                        if (x + n - 1 < 100 && y + m - 1 < 100) {
                            // Asignar las coordenadas de la esquina superior izquierda e inferior derecha del área
                            esquinaSuperiorIzquierda.setX(x);
                            esquinaSuperiorIzquierda.setY(y);
                            esquinaInferiorDerecha.setX(x + n - 1);  // Restar 1 para asegurarse de que no excede los límites
                            esquinaInferiorDerecha.setY(y + m - 1);  // Restar 1 para asegurarse de que no excede los límites

                            // Verificar si la temporada es la misma antes de continuar con otras comprobaciones
                            if (!puedeUbicar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, campo, temporadaActual, cultivo)) {
                                continue;  // Saltar a la siguiente posición si la temporada no coincide
                            }

                            // Calcular el riesgo promedio en el área seleccionada
                            riesgoPromedio = calculariesgoPromedio(esquinaSuperiorIzquierda.getX(), esquinaSuperiorIzquierda.getY(),
                                    esquinaInferiorDerecha.getX(), esquinaInferiorDerecha.getY(), matrizRiesgo);
                            ganancia = gananciaCultivo(cultivo, new CultivoSeleccionado(cultivo.getNombre(), esquinaSuperiorIzquierda,
                                            esquinaInferiorDerecha, cultivo.getInversionRequerida(), (int) riesgoPromedio, ganancia),
                                    matrizRiesgo, esquinaSuperiorIzquierda.getX(), esquinaSuperiorIzquierda.getY(),
                                    esquinaInferiorDerecha.getX(), esquinaInferiorDerecha.getY());

                            if (rellenar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, distribucionActual, campo)) {
                                CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado(cultivo.getNombre(),
                                        esquinaSuperiorIzquierda, esquinaInferiorDerecha, cultivo.getInversionRequerida(),
                                        (int) riesgoPromedio, ganancia);

                                distribucionActual.add(cultivoSeleccionado);
                                mejorDistribucion = backtracking(nivel + 1, cultivos, campo, gananciaActual + ganancia,
                                        distribucionActual, mejorGanancia, mejorDistribucion, temporadaActual, matrizRiesgo, maxIntentos - 1);

                                distribucionActual.remove(distribucionActual.size() - 1);
                                liberarArea(esquinaSuperiorIzquierda, esquinaInferiorDerecha, campo);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Distribución final encontrada:");
        for (CultivoSeleccionado cultivoSeleccionado : mejorDistribucion) {
            System.out.println("Cultivo: " + cultivoSeleccionado.getNombreCultivo() +
                    " - Posición: (" + cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() + ", " +
                    cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() + ") - Ganancia: " +
                    cultivoSeleccionado.getGananciaObtenida());
        }
        return mejorDistribucion;
    }



    private boolean puedeUbicar(Coordenada esquinaSuperiorIzquierda, Coordenada esquinaInferiorDerecha, double[][] campo, String temporadaCultivo, Cultivo cultivo) {
        if (esquinaSuperiorIzquierda == null || esquinaInferiorDerecha == null || campo == null || cultivo == null) {
            System.out.println("Error: Parámetros nulos.");
            return false;
        }

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
        int n = esquinaInferiorDerecha.getX() - esquinaSuperiorIzquierda.getX() + 1; // altura del cultivo
        int m = esquinaInferiorDerecha.getY() - esquinaSuperiorIzquierda.getY() + 1; // ancho del cultivo

        if (n + m > 10) {
            System.out.println("No se puede ubicar el cultivo, la suma de la altura y el ancho es mayor que 10.");
            return false;
        }

        // Verificación de la temporada
        if (temporadaCultivo == null || !cultivo.getTemporadaOptima().equals(temporadaCultivo)) {
            System.out.println("No se puede ubicar, la temporada actual es diferente de la del cultivo.");
            System.out.println("Temporada actual: " + temporadaCultivo);
            System.out.println("Temporada del cultivo: " + cultivo.getTemporadaOptima());
            return false; // Si la temporada no coincide, retornamos 'false' para indicar que no se puede ubicar
        }

        // Verificar que todas las celdas dentro del área deseada están libres
        for (int i = esquinaSuperiorIzquierda.getX(); i <= esquinaInferiorDerecha.getX(); i++) {
            for (int j = esquinaSuperiorIzquierda.getY(); j <= esquinaInferiorDerecha.getY(); j++) {
                if (campo[i][j] > 0.0) { // Si la celda está ocupada
                    System.out.println("No se puede ubicar, área ocupada en la celda: (" + i + ", " + j + ")");
                    return false;
                }
            }
        }

        return true; // El cultivo puede ser ubicado
    }






    private boolean rellenar(Coordenada izq, Coordenada der, List<CultivoSeleccionado> distribucionActual, double[][] campo) {
        System.out.println("Rellenando área con cultivo desde: (" + izq.getX() + ", " + izq.getY() + ") hasta: (" + der.getX() + ", " + der.getY() + ")");

        // Verificar si el cultivo se puede ubicar en el área especificada
        for (int i = izq.getX(); i <= der.getX(); i++) {
            for (int j = izq.getY(); j <= der.getY(); j++) {
                if (campo[i][j] > 0.0) { // Si la celda está ocupada
                    System.out.println("No se puede colocar el cultivo, posición ocupada en la celda: (" + i + ", " + j + ")");
                    return false;
                }
            }
        }

        // Si no hay celdas ocupadas, marcar el área como ocupada
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

    private void liberarArea(Coordenada izq, Coordenada der, double[][] campo) {
        // Liberar las celdas que se habían ocupado
        for (int i = izq.getX(); i <= der.getX(); i++) {
            for (int j = izq.getY(); j <= der.getY(); j++) {
                campo[i][j] = 0.0; // Marca la celda como libre
            }
        }
    }

    private double gananciaCultivo(Cultivo cultivo, CultivoSeleccionado cultivoSeleccionado, double[][] matrizRiesgo, int xInicio, int yInicio, int xFin, int yFin) {
        System.out.println("Calculando ganancia para cultivo " + cultivo.getNombre());
        int inicioX = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
        int inicioY = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
        int finX = cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
        int finY = cultivoSeleccionado.getEsquinaInferiorDerecha().getY();
        double potencialTotal = 0;


        // Recorre la matriz en el rango dado para calcular el potencial total
        for (int i = inicioX; i < finX; i++) {
            for (int j = inicioY; j < finY; j++) {
                double riesgo = matrizRiesgo[i][j];
                // Llama a la función de cálculo de potencial para cada celda
                potencialTotal += calcularPotencial(cultivo, matrizRiesgo, xInicio, yInicio, xFin, yFin);
                System.out.println("Riesgo en celda (" + i + ", " + j + "): " + riesgo);
            }
        }

        double ganancia = potencialTotal - cultivo.getInversionRequerida();
        System.out.println("Ganancia para cultivo " + cultivo.getNombre() + ": " + ganancia);
        return ganancia;
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
        double riesgoPromedio = riesgoTotal / contador;
        System.out.println("Riesgo total: " + riesgoTotal + ", Contador: " + contador + ", Riesgo promedio: " + riesgoPromedio);
        return riesgoPromedio;
    }



}
