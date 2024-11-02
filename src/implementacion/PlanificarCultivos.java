package implementacion;

import Lib.Cultivo;
import Lib.CultivoSeleccionado;

import java.util.ArrayList;
import java.util.List;

public class PlanificarCultivos implements Lib.PlanificarCultivos {
    @Override
    public List<CultivoSeleccionado> obtenerPlanificacion(List<Cultivo>  cultivos, double[][] coordenadas, String temporadaActual) {
        List<CultivoSeleccionado> distribucionActual = new ArrayList<>();
        List<CultivoSeleccionado> mejorDistribucion = new ArrayList<>();


        return mejorDistribucion;
    }

    private void backtracking(double[][] campo, List<Cultivo> cultivos, String temporadaActual, int x, int y,
                              List<CultivoSeleccionado> distribucionActual, List<CultivoSeleccionado> mejorDistribucion){

    }

    private boolean puedoUbicar(double [][] campo,Cultivo cultivo, int x, int y, String temporada){
        if(x+cultivo.getFilas() > campo.length ||(y+cultivo.getColumnas())> campo[0].length
                ||(x+y)<=11 || temporada!= cultivo.getTemporada()){
            return false;
        }
        return true;
    }

    private void rellenar(){

    }

    private double calcularPotencial(double riesgo, double precioVenta, double costoParcela){
        return (1-riesgo)*(precioVenta-costoParcela);
    }
    private double gananciaCultivo(Cultivo cultivo,double[][] campo, int x, int y){
        double potencialTotal=0;
        double riesgoTotal=0;
        for (int i=0;i<cultivo.getFilas();i++){
            for(int j=0; j<cultivo.getColumnas();j++){
                double riesgo= campo[x+i][y+j];
                potencialTotal += calcularPotencial(riesgo, cultivo.getPrecioVenta(), cultivo.getCostoParcela());
                riesgoTotal+=riesgo;
            }
        }

        double ganancia=potencialTotal-cultivo.getCostoInversion();

        return ganancia;

    }

    private double riesgoPromedio (Cultivo cultivo, double [][]campo, int x, int y){
        double riesgoTotal=0;
        for (int i=0; i<cultivo.getFilas();i++){
            for(int j=0; j<cultivo.getColumnas(); i++){
                 riesgoTotal += campo[x+i][y+j];
            }
        }
        double riesgoPromedio=riesgoTotal/(cultivo.getFilas()*cultivo.getColumnas());
        return riesgoPromedio;
    }
}
