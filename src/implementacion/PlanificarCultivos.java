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
        double[][] campo = new double[10][10];

        return backtracking(0,0,var1, campo, 0.0, distribucionActual, 0.0, mejorDistribucion, var3, var2);
    }

    private List<CultivoSeleccionado> backtracking(
            int x, int y, List<Cultivo> cultivos, double[][] campo, double gananciaActual,
            List<CultivoSeleccionado> distribucionActual, double mejorGanancia,
            List<CultivoSeleccionado> mejorDistribucion, String temporadaActual,
            double[][] matrizRiesgo) {

        System.out.println("Ciclo backtracking - Nivel: Cultivo " + "nombre:"  + x + ", Coordenadas: (" + x + ", " + y + ")");

        // Fin del ciclo de backtracking cuando todos los cultivos hayan sido procesados
        if (x >= campo.length) {  // Asegúrate de que `x` no se pase de los límites del campo
            System.out.println("Fin de nivel alcanzado con ganancia actual: " + gananciaActual);
            if (gananciaActual > mejorGanancia) {
                mejorDistribucion = new ArrayList<>(distribucionActual);
                System.out.println("Nueva mejor distribución encontrada con ganancia: " + gananciaActual);
            }
            return mejorDistribucion;
        }

        for (int i = 0; i < cultivos.size(); i++) {

            Cultivo cultivo = cultivos.get(i);  // Obtener el cultivo en la posición i
            Coordenada esquinaSuperiorIzquierda = new Coordenada(x, y);


            int n = 1;
            int m = 1;
            Coordenada esquinaInferiorDerecha = new Coordenada(x + n - 1, y + m - 1);

            // Verificar si se puede ubicar el cultivo
            if (Objects.equals(cultivo.getTemporadaOptima(), temporadaActual) &&
                    puedeUbicar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, campo, temporadaActual, cultivo)) {

                // Calcular la ganancia de colocar este cultivo
                double ganancia = gananciaCultivo(
                        cultivo, new CultivoSeleccionado(
                                cultivo.getNombre(), esquinaSuperiorIzquierda, esquinaInferiorDerecha,
                                cultivo.getInversionRequerida(), 0, 0),
                        matrizRiesgo, esquinaSuperiorIzquierda.getX(), esquinaSuperiorIzquierda.getY(),
                        esquinaInferiorDerecha.getX(), esquinaInferiorDerecha.getY());

                // Intentar ubicar el cultivo
                if (rellenar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, distribucionActual, campo)) {
                    CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado(
                            cultivo.getNombre(), esquinaSuperiorIzquierda, esquinaInferiorDerecha,
                            cultivo.getInversionRequerida(), 0, ganancia);  // No calculamos riesgo aquí

                    distribucionActual.add(cultivoSeleccionado);

                    // Calcular la siguiente posición
                    int sig_x = x;
                    int sig_y = y + 1;  // Avanzamos a la siguiente columna

                    // Si hemos llegado al final de la fila (columna máxima), pasamos a la siguiente fila
                    if (sig_y >= campo[0].length) {
                        sig_x = sig_x + 1;  // Pasamos a la siguiente fila
                        sig_y = 0;  // Reiniciamos sig_y a 0 (primera columna)
                    }


                    // Llamada recursiva
                    mejorDistribucion = backtracking(sig_x, sig_y, cultivos, campo, gananciaActual + ganancia,
                            distribucionActual, mejorGanancia, mejorDistribucion, temporadaActual, matrizRiesgo);

                    // Limpiar área y retroceder: Se deben limpiar las celdas del campo ocupadas por el cultivo
                    limpiarCampo(esquinaSuperiorIzquierda, esquinaInferiorDerecha, campo);

                    // Eliminar el cultivo de la distribución actual
                    distribucionActual.remove(distribucionActual.size() - 1);
                }
            }

        }

        // Imprimir la distribución final
        System.out.println("Distribución final encontrada:");
        for (CultivoSeleccionado cultivoSeleccionado : mejorDistribucion) {
            System.out.println("Cultivo: " + cultivoSeleccionado.getNombreCultivo() +
                    " - Posición: (" + cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() + ", " +
                    cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() + ") - Ganancia: " +
                    cultivoSeleccionado.getGananciaObtenida());
        }
        return mejorDistribucion;
    }




    private void limpiarCampo(Coordenada esquinaSuperiorIzquierda, Coordenada esquinaInferiorDerecha, double[][] campo) {
        for (int i = esquinaSuperiorIzquierda.getX(); i <= esquinaInferiorDerecha.getX(); i++) {
            for (int j = esquinaSuperiorIzquierda.getY(); j <= esquinaInferiorDerecha.getY(); j++) {
                campo[i][j] = 0.0;  // Asumimos que el valor 0.0 indica que la celda está libre
            }
        }
    }



    private boolean puedeUbicar(Coordenada esquinaSuperiorIzquierda, Coordenada esquinaInferiorDerecha, double[][] campo, String temporadaCultivo, Cultivo cultivo) {
        // Comprobación de parámetros nulos
        if (esquinaSuperiorIzquierda == null || esquinaInferiorDerecha == null || campo == null || cultivo == null) {
            System.out.println("Error: Parámetros nulos.");
            return false;
        }

        // Mensaje de depuración detallado
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

        // Cálculo de dimensiones del cultivo
        int n = esquinaInferiorDerecha.getX() - esquinaSuperiorIzquierda.getX() + 1; // altura del cultivo
        int m = esquinaInferiorDerecha.getY() - esquinaSuperiorIzquierda.getY() + 1; // ancho del cultivo

        // Verificación de que la suma de altura y ancho no supere 10
        if (n + m > 10) {
            System.out.println("No se puede ubicar el cultivo, la suma de la altura y el ancho es mayor que 10.");
            return false;
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


        System.out.println("El cultivo puede ser ubicado en la posición indicada.");
        return true;
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

    private double calcularPotencial(double riesgo, double precioDeVenta, double costoPorParcela) {
        // Lógica para calcular el potencial basado en la fórmula proporcionada
        return (1 - riesgo) * (precioDeVenta - costoPorParcela);
    }



    private double gananciaCultivo(Cultivo cultivo, CultivoSeleccionado cultivoSeleccionado, double[][] matrizRiesgo, int xInicio, int yInicio, int xFin, int yFin) {
        double potencialTotal = 0;

        // Obtén las coordenadas de las esquinas del área asignada
        int inicioX = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
        int inicioY = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
        int finX = cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
        int finY = cultivoSeleccionado.getEsquinaInferiorDerecha().getY();

        // Recorre la matriz en el rango dado para calcular el potencial total
        for (int i = inicioX; i <= finX; i++) { // Usamos <= para incluir finX
            for (int j = inicioY; j <= finY; j++) { // Usamos <= para incluir finY
                if (i < matrizRiesgo.length && j < matrizRiesgo[i].length) { // Verifica que las coordenadas estén dentro de los límites de la matriz
                    double riesgo = matrizRiesgo[i][j];
                    // Calcula el potencial para cada celda
                    potencialTotal += calcularPotencial(riesgo, cultivo.getPrecioDeVentaPorParcela(), cultivo.getCostoPorParcela());
                }
            }
        }

        // Calcula la ganancia restando el costo de inversión
        double ganancia = potencialTotal - cultivo.getInversionRequerida();

        // Imprime el cálculo de la ganancia para depuración
        System.out.println("Potencial Total: " + potencialTotal);
        System.out.println("Costo de Inversión: " + cultivo.getInversionRequerida());
        System.out.println("Ganancia: " + ganancia);

        return ganancia;
    }




    private double calcularRiesgoPromedio(CultivoSeleccionado cultivoSeleccionado, double[][] campo) {
        double riesgoTotal = 0;
        int totalParcelas = 0;

        // Recorrer el área seleccionada para calcular el riesgo promedio
        for (int i = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
             i < cultivoSeleccionado.getEsquinaInferiorDerecha().getX(); i++) {
            for (int j = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
                 j < cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); j++) {
                riesgoTotal += campo[i][j];
                totalParcelas++;
            }
        }

        return totalParcelas == 0 ? 0 : riesgoTotal / totalParcelas;
    }



}
