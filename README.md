Un agricultor desea planificar la siembra de diversos cultivos en un campo cuadrado de
100mx100m de parcelas de 1mx1m. Cada tipo de cultivo tendrá un área elegida de NxM
parcelas (N filas y M columnas). Además, cada cultivo posee:
- Costo por parcela
- Inversión requerida (que no depende del total de parcelas)
- Precio de venta por unidades producidas en cada parcela
- Temporada óptima para su crecimiento
Cada parcela tendrá un riesgo asociado, que corresponde a un valor entre 0 y 1. A modo de
ejemplo, si el campo cuadrado fuese de 4mx4m, la matriz de riesgos sería de la siguiente
forma:
0.0 0.125 0.25 0.375
0.125 0.25 0.375 0.5
0.25 0.3755 0.5 0.625
0.375 0.5 0.625 0.75
Esto quiere decir que el cultivo sobre la parcela de la posición (0, 0) no tiene riesgo, mientras
que el cultivo de la parcela de la posición (2, 1) tiene 0.375 de riesgo.
El potencial de cada parcela será calculado de la siguiente forma:
● Potencial de cada parcela = (1 - Riesgo asociado)*(Precio de venta por unidades
producidas en cada parcela - Costo por parcela)
La ganancia de cada cultivo se calcula como:
● Ganancia del Cultivo = Suma de los potenciales de cada parcela en el área asignada -
Costo de inversión
Se solicita diseñar un algoritmo que utilice la estrategia de Backtracking que permita al
agricultor decidir cómo distribuir los cultivos en el campo de manera que se maximice la
ganancia total.
Ejemplo
Supongamos los cultivos A, B y C con las siguientes características:
2/2
● Cultivo A: Área elegida: 5m x 6m, Costo por parcela $100.5, Costo de inversión:
$2,000, Precio de venta por unidades producidas en cada parcela: $500.
● Cultivo B: Área elegida: 5m x 5m, Costo por parcela $105, Costo de inversión: $1,000,
Precio de venta por unidades producidas en cada parcela: $450.
● Cultivo C: Área elegida: 3m x 8m, Costo por parcela $97, Costo de inversión: $1,500,
Precio de venta por unidades producidas en cada parcela: $475.
Respuesta esperada
El algoritmo recibe como datos la lista de cultivos disponibles, el área total del campo, y los
requisitos específicos de cada cultivo. Como salida, el algoritmo deberá indicar la distribución
óptima de los cultivos, donde cada uno de ellos tendrá los siguientes datos:
● Nombre del cultivo
● Esquinas superior izquierda e inferior derecha del área asignada
● Monto invertido
● Riesgo promedio, que se calcula como el promedio de los riesgos de cada parcela del
área asignada
● Ganancia obtenida
Restricciones
- Solo un cultivo puede ser plantado múltiples veces, mientras que los demás solo una
vez.
- Si el cultivo no corresponde a la temporada indicada, entonces no se podrá usar. Las
temporadas disponibles son: “Verano”, “Otoño”, “Invierno”, “Primavera”.
- Si un cultivo ocupa NxM, deberá cumplir con N + M ≤ 11. Esto aplica para cualquier
sección contigua.
Ejemplo:
Por separado, el rectángulo verde y el rectángulo violeta cumplen la condición. El verde es de
3x6 y 3 + 6 = 9 ≤ 11. El violeta es de 2x6 y 2 + 6 = 8 ≤ 11.
Sin embargo, al ponerlos uno al lado del otro, se formó el siguiente rectángulo naranja que no
es válido, ya que mide 2x12 y 2 + 12 = 14 ≥ 11.
3/2
Entregables recibidos
Para llevar a cabo la resolución del problema en Java se provee un proyecto en el cual se
incluyen la interface PlanificarCultivos.java con el método obtenerPlanificacion, con tres
entradas como parámetros (lista de cultivos disponibles, matriz de riesgos, temporada), y la
salida es una lista de los cultivos seleccionados. No se puede modificar la entrada ni la salida,
solo se pueden agregar métodos adicionales en la clase
PlanificarCultivosImplementacion.java que implementa la interfaz, si así se requiere para
poder llevar a cabo la resolución.
Se entregan dos archivos CultivosLibreria.jar que se encuentra la interfaz y clases de
estructuras, y CultivosProyecto.jar que se encuentra el main y la clase de implementación que
será sobre los cuales se debe trabajar.
