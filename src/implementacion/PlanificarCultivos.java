package implementacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanificarCultivos implements PlanificadorCultivos {

    @Override
    public List<CultivoSeleccionado> obtenerPlanificacion(List<Cultivo> var1, double[][] var2, String var3) {
        List<CultivoSeleccionado> distribucionActual = new ArrayList<>();
        List<CultivoSeleccionado> mejorDistribucion = new ArrayList<>();
        double[][] campo = new double[100][100];

        mejorDistribucion = backtracking(0, var1, campo, 0.0, distribucionActual, 0.0, mejorDistribucion, var3, var2);

        double mayorGanancia=0;
        Cultivo mejorCultivo=null;
        for(int i=0; i<var1.size();i++){
            if(var1.get(i).getTemporadaOptima().equals(var3)){
                double potencial= CalcularPotencial(0,0, campo.length, campo[0].length, var1.get(i),var2);
                double ganancia=potencial-var1.get(i).getInversionRequerida();
                //System.out.println(var1.get(i).getNombre());
                //System.out.println(ganancia);
                if (ganancia > mayorGanancia){
                    mayorGanancia=ganancia;
                    mejorCultivo=var1.get(i);
                }
            }
        }
        mejorDistribucion=rellenarEspacios(mejorCultivo, mejorDistribucion,campo,var2);

        return mejorDistribucion;
    }


    private List<CultivoSeleccionado> backtracking(
            int etapa, List<Cultivo> cultivos, double[][] campo, double gananciaActual,
            List<CultivoSeleccionado> distribucionActual, double mejorGanancia,
            List<CultivoSeleccionado> mejorDistribucion, String temporadaActual,
            double[][] matrizRiesgo) {

//        System.out.println("entrando a backtracking");
        if (etapa >= cultivos.size()) {
            //System.out.println("Fin del backtracking. Ganancia actual: " + gananciaActual);
            if (gananciaActual > mejorGanancia) {
                mejorDistribucion.clear();
                mejorDistribucion.addAll(distribucionActual);
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
                        if (n + m <= 11) {
                            Coordenada esquinaSuperiorIzquierda = new Coordenada(x, y);
                            Coordenada esquinaInferiorDerecha = new Coordenada(x + n - 1, y + m - 1);

                            CultivoSeleccionado cultivoSeleccionado=new CultivoSeleccionado();
                            cultivoSeleccionado.setNombreCultivo(cultivo.getNombre());
                            cultivoSeleccionado.setEsquinaInferiorDerecha(esquinaInferiorDerecha);
                            cultivoSeleccionado.setEsquinaSuperiorIzquierda(esquinaSuperiorIzquierda);

                            if (esquinaInferiorDerecha.getX() < campo.length && esquinaInferiorDerecha.getY() < campo[0].length) {
                                if (puedeUbicar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, campo, distribucionActual) ){
                                    double riesgoPromedio = calcularRiesgoPromedio(x, y, x + n, y + m, matrizRiesgo);

                                    double potencialTotal = CalcularPotencial(x, y, x + n, y + m, cultivo, matrizRiesgo);
                                    double ganancia = potencialTotal - cultivo.getInversionRequerida();


                                    cultivoSeleccionado.setGananciaObtenida(ganancia);
                                    cultivoSeleccionado.setRiesgoAsociado(riesgoPromedio);
                                    cultivoSeleccionado.setMontoInvertido(cultivo.getInversionRequerida());



                                    if (compararGanancias(distribucionActual, mejorDistribucion, ganancia, gananciaActual)) {
                                        distribucionActual.add(cultivoSeleccionado);
                                        marcarComoOcupado(cultivoSeleccionado, campo);
                                        //System.out.println("llamada x etapa");
                                        mejorDistribucion = backtracking(etapa + 1, cultivos, campo, gananciaActual + ganancia,
                                                distribucionActual, mejorGanancia, mejorDistribucion, temporadaActual, matrizRiesgo);

                                        distribucionActual.remove(distribucionActual.size() - 1);
                                        liberarCasillas(cultivoSeleccionado, campo);
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


    private List<CultivoSeleccionado> rellenarEspacios(Cultivo cultivo, List<CultivoSeleccionado> mejorDistribucion, double[][] campo, double[][]matrizRiesgo) {
        //System.out.println("entrando en rellenar");

        for(CultivoSeleccionado cultivoSeleccionado: mejorDistribucion){
            marcarComoOcupado(cultivoSeleccionado, campo);
        }
        for(int x=0;x<= campo.length;x++) {
            for (int y = 0; y <= campo[0].length; y++) {
                for (int n = 3; n <= 10; n++) {
                    for (int m = 3; m <= 10; m++) {
                        if (n + m <= 11) {
                            Coordenada izq = new Coordenada(x, y);
                            Coordenada der = new Coordenada(x + n - 1, y + m - 1);

                            CultivoSeleccionado cultivoSeleccionado=new CultivoSeleccionado();
                            cultivoSeleccionado.setNombreCultivo(cultivo.getNombre());
                            cultivoSeleccionado.setEsquinaInferiorDerecha(der);
                            cultivoSeleccionado.setEsquinaSuperiorIzquierda(izq);

                            // Validar restricciones y que no se solape con la distribución actual
                            if (puedeUbicar(izq, der, campo, mejorDistribucion)) {
                                //System.out.println("puedo ubicar");
                                double riesgoPromedio = calcularRiesgoPromedio(x, y, x + n, y + m, matrizRiesgo);

                                double potencialTotal = CalcularPotencial(x, y, x + n, y + m, cultivo, matrizRiesgo);
                                double ganancia = potencialTotal - cultivo.getInversionRequerida();


                                cultivoSeleccionado.setGananciaObtenida(ganancia);
                                cultivoSeleccionado.setRiesgoAsociado(riesgoPromedio);
                                cultivoSeleccionado.setMontoInvertido(cultivo.getInversionRequerida());

                                mejorDistribucion.add(cultivoSeleccionado);
                                marcarComoOcupado(cultivoSeleccionado, campo);

                            }
                            //System.out.println("no puedo ubicar");
                        }
                    }
                }
            }
        }


        return mejorDistribucion;

    }

    private void marcarComoOcupado(CultivoSeleccionado cultivoSeleccionado, double[][] campo) {
        for (int i=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX(); i<=cultivoSeleccionado.getEsquinaInferiorDerecha().getX(); i++){
            for(int j=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() ; j<=cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); j++){
                campo[i][j]=1.0;

            }
        }

        if(cultivoSeleccionado.getEsquinaInferiorDerecha().getY()<campo.length-1){
            campo[cultivoSeleccionado.getEsquinaInferiorDerecha().getX()][cultivoSeleccionado.getEsquinaInferiorDerecha().getY()+1]=1.0;

        }
    }


    private void liberarCasillas(CultivoSeleccionado cultivoSeleccionado, double[][] campo) {
        for (int i=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX(); i<=cultivoSeleccionado.getEsquinaInferiorDerecha().getX(); i++){
            for(int j=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY() ; j<=cultivoSeleccionado.getEsquinaInferiorDerecha().getY(); j++){
                campo[i][j]=0.0;
            }
        }
    }




}

