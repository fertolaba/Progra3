package implementacion;

import Lib.Cultivo;
import Lib.CultivoSeleccionado;

import java.util.List;

public interface PlanificadorCultivos {
  List<CultivoSeleccionado> obtenerPlanificacion(List<Cultivo> var1, double[][] var2, String var3);
}
