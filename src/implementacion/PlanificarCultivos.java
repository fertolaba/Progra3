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
//
//        if (!mejorDistribucion.isEmpty()) {
//            Cultivo ultimoCultivo = var1.get(var1.size() - 1);
//            rellenarEspacios(ultimoCultivo, mejorDistribucion, campo, var2);
//        }

        int i=var1.size()-1;
        while(!Objects.equals(var1.get(i).getTemporadaOptima(), var3)){
            i--;
        }
        mejorDistribucion=rellenarEspacios(var1.get(i), mejorDistribucion,campo,var2);

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
//            for(int x=0; x<mejorDistribucion.size(); x++){
//                System.out.println(mejorDistribucion.get(x));
//            }
//            int i=cultivos.size()-1;
//            while(!Objects.equals(cultivos.get(i).getTemporadaOptima(), temporadaActual)){
//                i--;
//            }
//            mejorDistribucion=rellenarEspacios(cultivos.get(i), mejorDistribucion, campo,matrizRiesgo);
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

//                                    CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado(
//                                            cultivo.getNombre(), esquinaSuperiorIzquierda, esquinaInferiorDerecha,
//                                            cultivo.getInversionRequerida(), riesgoPromedio, ganancia);

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

//        for (CultivoSeleccionado cultivo : distribucionActual) {
//            Coordenada cultivoEsquinaIzq = cultivo.getEsquinaSuperiorIzquierda();
//            Coordenada cultivoEsquinaDer = cultivo.getEsquinaInferiorDerecha();
//
//            if(esquinaSuperiorIzquierda.getX() <= cultivoEsquinaDer.getX() && esquinaSuperiorIzquierda.getY()<=cultivoEsquinaDer.getY()){
//                return false;
//            }
//
//            if(esquinaInferiorDerecha.getX()>=cultivoEsquinaIzq.getX() && esquinaInferiorDerecha.getY()>= cultivoEsquinaIzq.getY()){
//                return false;
//            }
//
//            if (esquinaSuperiorIzquierda.getX() <= cultivoEsquinaDer.getX() && esquinaInferiorDerecha.getX() >= cultivoEsquinaIzq.getX() &&
//                    esquinaSuperiorIzquierda.getY() <= cultivoEsquinaDer.getY() && esquinaInferiorDerecha.getY() >= cultivoEsquinaIzq.getY()) {
//                return false;
//            }
//        }

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

//                                CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado(
//                                        cultivo.getNombre(), izq, der,
//                                        cultivo.getInversionRequerida(), riesgoPromedio, ganancia);

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
//        if (i<99 && j<99){  //Marcar como ocupado para q no se genere un rectangulo de n+m>11
//            campo[i+1][j+1]=1.0;
//        }
        if(cultivoSeleccionado.getEsquinaInferiorDerecha().getY()<99){
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
//    private List<CultivoSeleccionado> rellenarEspacios(Cultivo cultivo, List<CultivoSeleccionado> mejorDistribucion, double[][] campo, double[][] matrizRiesgo) {
//        for (int i = 0; i < campo.length; i++) {
//            for (int j = 0; j < campo[0].length; j++) {
//                if (campo[i][j] == 0.0) {
//                    for (int n = 1; n <= 10; n++) {
//                        for (int m = 1; m <= 10; m++) {
//                            if (n + m <= 11) {
//                                Coordenada esquinaSuperiorIzquierda = new Coordenada(i, j);
//                                Coordenada esquinaInferiorDerecha = new Coordenada(i + n - 1, j + m - 1);
//                                if (/*esquinaInferiorDerecha.getX() < campo.length &&
//                                        esquinaInferiorDerecha.getY() < campo[0].length &&*/
//                                        puedeUbicar(esquinaSuperiorIzquierda, esquinaInferiorDerecha, campo, mejorDistribucion)) {
//                                    CultivoSeleccionado cultivoSeleccionado = new CultivoSeleccionado(
//                                            cultivo.getNombre(), esquinaSuperiorIzquierda, esquinaInferiorDerecha,
//                                            cultivo.getInversionRequerida(), 0.0, 0.0);
//
//                                    if (validarRestriccionUnion(cultivoSeleccionado, mejorDistribucion)) {
//                                        double riesgoPromedio = calcularRiesgoPromedio(
//                                                esquinaSuperiorIzquierda.getX(), esquinaSuperiorIzquierda.getY(),
//                                                esquinaInferiorDerecha.getX() + 1, esquinaInferiorDerecha.getY() + 1, matrizRiesgo);
//                                        double potencialTotal = CalcularPotencial(
//                                                esquinaSuperiorIzquierda.getX(), esquinaSuperiorIzquierda.getY(),
//                                                esquinaInferiorDerecha.getX() + 1, esquinaInferiorDerecha.getY() + 1, cultivo, matrizRiesgo);
//                                        double ganancia = potencialTotal - cultivo.getInversionRequerida();
//
//                                        cultivoSeleccionado.setRiesgoAsociado(riesgoPromedio);
//                                        cultivoSeleccionado.setGananciaObtenida(ganancia);
//
//                                        mejorDistribucion.add(cultivoSeleccionado);
//
//                                        marcarComoOcupado(cultivoSeleccionado, campo);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return mejorDistribucion;
//    }




    private boolean sonAdyacentes(CultivoSeleccionado cultivo1, CultivoSeleccionado cultivo2) {
        boolean horizontalmenteAdyacentes =
                cultivo1.getEsquinaInferiorDerecha().getX() + 1 == cultivo2.getEsquinaSuperiorIzquierda().getX() ||
                        cultivo2.getEsquinaInferiorDerecha().getX() + 1 == cultivo1.getEsquinaSuperiorIzquierda().getX();

        boolean verticalmenteAdyacentes =
                cultivo1.getEsquinaInferiorDerecha().getY() + 1 == cultivo2.getEsquinaSuperiorIzquierda().getY() ||
                        cultivo2.getEsquinaInferiorDerecha().getY() + 1 == cultivo1.getEsquinaSuperiorIzquierda().getY();

        return horizontalmenteAdyacentes || verticalmenteAdyacentes;
    }

//    private boolean puedeUbicarPorDistribucion(Cultivo cultivo, List<CultivoSeleccionado> mejorDistribucion,double[][]campo, double[][] matrizRiesgo){
//        for(CultivoSeleccionado cultivoSeleccionado : mejorDistribucion){
//            int izqX=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getX();
//            int izqY=cultivoSeleccionado.getEsquinaSuperiorIzquierda().getY();
//            int derX=cultivoSeleccionado.getEsquinaInferiorDerecha().getX();
//            int derY=cultivoSeleccionado.getEsquinaInferiorDerecha().getY();
//
//
//            for(int i=izqX; i<=derX;i++){
//                for(int j=izqY; j<=derY; j++){
//                    campo[i][j]=1.0;
//                }
//            }
//        }
//        for(int i=0; i< campo.length;i++){
//            for(int j=0; j<campo[0].length; j++){
//                if(campo[i][j]==1.0){
//                    return false;
//                }
//            }
//        }
//        return true;
//    }


}

