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

    private List<CultivoSeleccionado> backtracking(double[][] campo, List<Cultivo> cultivos, String temporadaActual, int x, int y,
                              List<CultivoSeleccionado> distribucionActual, List<CultivoSeleccionado> mejorDistribucion){
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
            if(puedoUbicar(campo, cultivo, x, y, temporadaActual)){
                double ganancia=gananciaCultivo(cultivo,campo, x, y);
                double riesgoPromedio=riesgoPromedio(cultivo, campo, x, y);

                distribucionActual.add();

                int sig_x=x;
                int sig_y= y + cultivo.getColumnas();
                if(sig_y>campo[0].length){
                    sig_x+=1;
                    sig_y=0;
                }

                backtracking(campo, cultivos, temporadaActual, x, y, distribucionActual, mejorDistribucion);
                //eliminar(distrib_Actual)
            }
        }

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

    private double sumaGanancias(List<CultivoSeleccionado> distribucion){
        double total=0;
        for(int i=0; i< distribucion.size();i++){
            total+=distribucion.get(i).getGananciaObtenida();
        }
        return total;
    }
}
