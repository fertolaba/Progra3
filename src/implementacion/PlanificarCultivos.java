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
        String[][] campo = new String[100][100];
        for (int i=0; i<100; i++){
            for (int j=0; j<100; j++){
                campo[i][j]="";  //campo es una matriz donde se va a guardar el nombre del cultivo q lo ocupa
            }
        }
        double[][] matrizRiesgo = coordenadas;
        CultivoSeleccionado cultivoRepetido=null;

        mejorDistribucion = backtracking(campo, cultivos, temporadaActual, 0, 0, distribucionActual, mejorDistribucion, matrizRiesgo, cultivoRepetido);

        return mejorDistribucion;
    }

    private List<CultivoSeleccionado> backtracking(String[][] campo, List<Cultivo> cultivos, String temporadaActual, int x, int y,
                              List<CultivoSeleccionado> distribucionActual, List<CultivoSeleccionado> mejorDistribucion,double[][] matrizRiesgo, CultivoSeleccionado cultivoRepetido){
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
                    double ganancia=gananciaCultivo(cultivo, cultivoSeleccionado, matrizRiesgo);
                    double riesgoPromedio=riesgoPromedio(cultivoSeleccionado, matrizRiesgo);

                    distribucionActual.add(cultivoSeleccionado);
                    agregarAlCampo(campo,cultivoSeleccionado);

                    if(cultivoRepetido==null){
                        cultivoRepetido=cultivoSeleccionado;
                    }
                    repetirCultivo(campo, cultivoRepetido, distribucionActual.get(distribucionActual.size() - 1));


                    if (colisionan(campo, cultivoSeleccionado, x, y)) {
                        continue; // Pasa al siguiente cultivo en la lista
                    }

                    int sig_x = x;
                    int sig_y = y + (cultivoSeleccionado.getEsquinaInferiorDerecha().getY() - cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() + 1); // Altura del cultivo
                    while (sig_x < campo.length && (sig_y >= campo[0].length || !campo[sig_x][sig_y].equals(""))) {
                        if (sig_y >= campo[0].length) {
                            sig_x++;      // Avanzamos a la siguiente fila
                            sig_y = 0;    // Reiniciamos la columna al inicio de la nueva fila
                        } else {
                            sig_y++;      // Avanzamos al siguiente "y" en la misma fila
                        }
                    }

                    mejorDistribucion = backtracking(campo, cultivos, temporadaActual, sig_x, sig_y, distribucionActual, mejorDistribucion, matrizRiesgo, cultivoRepetido);

                    distribucionActual.remove(distribucionActual.size() - 1);
                    eliminarDelCampo(campo, cultivoSeleccionado);
                }

            }
        }

        return mejorDistribucion;
    }

    private boolean puedoUbicar(String [][] campo,CultivoSeleccionado cultivoSeleccionado, int x, int y){
        //Verifica que el cultivo no se exceda por derecha y x por "debajo"
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
                if (!campo[i][j].equals("")) {
                    return false;
                }
            }
        }

        // Valida que no supere el campo por izquierda y por arriba
        if (cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() < 0 ||
                cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() < 0) {
            return false;
        }

        //Verifica que la suma de filas y columnas del area a plantar no sea mayor a 11
        if ((cultivoSeleccionado.getEsquinaInferiorDerecha().getX() - cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX() +
                cultivoSeleccionado.getEsquinaInferiorDerecha().getY() - cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY()) > 11) {
            return false;
        }

        return true;
    }

    public void repetirCultivo(String[][] campo, CultivoSeleccionado cultivoSeleccionado, CultivoSeleccionado ultimoCultivo) {
        // Recorre cada celda del campo
        for (int i = 0; i < campo.length; i++) {
            for (int j = 0; j < campo[0].length; j++) {
                // Verifica si la celda está vacía
                if (campo[i][j].equals("")) {  //si las celdas nunca van hacer null no es necesario usar objetcs.equals xddddddddddddddddd
                    // Verifica si no hay colisión al colocar el cultivo seleccionado
                    if (!(colisionan(campo, cultivoSeleccionado, i, j))) {  //agrego el not ya que me parece mas logico que si la funcion se llama colisionan devuelva true cuando lo hacen y false cuando no colisionan xd
                        // Coloca el valor del cultivo en la celda (usando el valor de riesgo asociado xq es el unico que es un double xd)
                        campo[i][j] = cultivoSeleccionado.getNombreCultivo();
                    }
                }
            }
        }
    }
    private double calcularPotencial(Cultivo cultivo,double[][] matrizRiesgo, int x, int y ){
        double riesgoAsociado = matrizRiesgo[x][y];

        return (1-riesgoAsociado)*(cultivo.getPrecioDeVentaPorParcela()-cultivo.getCostoPorParcela());
}

    private double gananciaCultivo(Cultivo cultivo, CultivoSeleccionado cultivoSeleccionado, double[][] matrizRiesgo){
        int inicioX = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
        int inicioY = cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
        int finX = cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
        int finY = cultivoSeleccionado.getEsquinaInferiorDerecha().getY();
        double potencialTotal=0;

        for (int i= inicioX;i<finX;i++){
            for (int j = inicioY; j <= finY; j++) {
                double riesgo = matrizRiesgo[i][j];
                potencialTotal += calcularPotencial(cultivo, matrizRiesgo, i, j);
            }
        }
        return potencialTotal-cultivo.getInversionRequerida();
    }

    private double riesgoPromedio(CultivoSeleccionado cultivoSeleccionado, double[][] matrizRiegso) {
        Lib.Coordenada esquinaSuperiorIzquierda = cultivoSeleccionado.getEsquinaSuperiorIzquierda();
        Lib.Coordenada esquinaInferiorDerecha = cultivoSeleccionado.getEsquinaInferiorDerecha();

        int xInicio = esquinaSuperiorIzquierda.getX();
        int yInicio = esquinaSuperiorIzquierda.getY();
        int filas = esquinaInferiorDerecha.getX() - xInicio;    //elimino el -1, agrego <= en el for
        int columnas = esquinaInferiorDerecha.getY() - yInicio; //elimino el -1, agrego <= en el for

        double riesgoTotal = 0;
        for (int i = 0; i <= filas; i++) {
            for (int j = 0; j <= columnas; j++) {
                riesgoTotal += matrizRiegso[xInicio + i][yInicio + j];  //reemplazo campo x matriz de riesgo q es la q contiene los riesgos lol
            }
        }
        return riesgoTotal / (filas * columnas);
    }

    private boolean colisionan(String[][] campo, CultivoSeleccionado cultivoSeleccionado, int i, int j) {
        // Verifica si hay un cultivo en la celda de arriba contiene un cultivo diferente al seleccionado
        if (i > 0 && !campo[i - 1][j].equals("") && !campo[i - 1][j].equals(cultivoSeleccionado.getNombreCultivo())) {
            return true; // Colisión con cultivo en la celda de arriba
        }

        // Verifica si hay un cultivo en la celda de abajo contiene un cultivo diferente al seleccionado
        if (i < (campo.length - 1) && !campo[i + 1][j].equals("") && !campo[i + 1][j].equals(cultivoSeleccionado.getNombreCultivo())) {
            return true; // Colisión con cultivo en la celda de abajo
        }

        // Verifica si hay un cultivo en la celda de la izquierda contiene un cultivo diferente al seleccionado
        if (j > 0 && !campo[i][j - 1].equals("") && !campo[i][j - 1].equals(cultivoSeleccionado.getNombreCultivo())) {
            return true; // Colisión con cultivo en la celda de la izquierda
        }

        // Verifica si hay un cultivo en la celda de la derecha contiene un cultivo diferente al seleccionado
        if (j < campo[0].length - 1 && !campo[i][j + 1].equals("") && !campo[i][j + 1].equals(cultivoSeleccionado.getNombreCultivo())) {
            return true; // Colisión con cultivo en la celda de la derecha
        }


        // Si no hay colisiones, devuelve verdadero
        return false;
    }

    private double sumaGanancias(List<CultivoSeleccionado> distribucion){
        double total=0;
        for(int i=0; i< distribucion.size();i++){
            total+=distribucion.get(i).getGananciaObtenida();
        }
        return total;
    }

    private void agregarAlCampo(String[][]campo, CultivoSeleccionado cultivoSeleccionado){
        for(int i=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX(); i<=cultivoSeleccionado.getEsquinaInferiorDerecha().getX();i++){
            for(int j=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY(); j<=cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); j++){
                campo[i][j]=cultivoSeleccionado.getNombreCultivo();
            }
        }
    }
    private void eliminarDelCampo(String[][]campo, CultivoSeleccionado cultivoSeleccionado){
        for(int i=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX(); i<=cultivoSeleccionado.getEsquinaInferiorDerecha().getX();i++){
            for(int j=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY(); j<=cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); j++){
                campo[i][j]="";
            }
        }
    }
}
