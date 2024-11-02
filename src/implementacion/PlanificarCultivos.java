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

    private void puedoUbicar(){

    }

    private void rellenar(){

    }

    private void calcularPotencia(){

    }
    private void gananciaCultivo(){

    }
}
