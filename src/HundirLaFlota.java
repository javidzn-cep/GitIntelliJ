
public class HundirLaFlota {

    static final String BACKGROUND = " · ", BARCO = "   ";
    static final String[] COLORES = {/*Reset: 0*/"\u001b[0m", /*Verde: 1*/ "\u001b[48;5;82m", /*Rojo: 2*/"\u001b[48;5;196m", /*Gris: 3*/"\u001b[48;5;250m", /*Reversed: 4*/"\u001b[7m", /*Azul: 5*/ "\u001b[48;5;39m", /*Gris Oscuro: 6*/ "\u001b[48;5;240m", /*Negrita 7*/"\u001b[1m"};
    static final String[][] BARCOS = {{"Portaaviones", "5",}, {"Acorazado nº1", "4"}, {"Acorazado nº2", "4"}, {"Submarino", "3"}, {"Destructor nº1", "3"}, {"Destructor nº2", "3"}, {"Barco Patrulla nº1", "2"}, {"Barco Patrulla nº2", "2"}, {"Barco Patrulla nº3", "2"}, {"Barco Patrulla nº4", "2"}};
    static final int NUM_COLUMNAS = 10, NUM_FILAS = 10; // No pueden ser menores que 10

    /**
     * Juego Hundir La flota
     * @param args Nose que es esto
     */
    public static void main(String[] args) {

        String[][] tablero = new String[NUM_FILAS][NUM_COLUMNAS];
        String[] nombre_jugadores = new String[2];
        int tocados_restantes, numero_dificultad = 10, num_player = 1, max_tocados = contarMaxNumTocados();
        int[][][] coordenadas_barcos_1 = BBDD_Barcos(), coordenadas_barcos_2 = BBDD_Barcos(), coordenadas_barcos = BBDD_Barcos(),
                coordenadas_probadas_1 = BBDD_Coordenadas(max_tocados), coordenadas_probadas_2 = BBDD_Coordenadas(max_tocados), coordenadas_probadas = BBDD_Coordenadas(max_tocados);
        boolean distribucion_manual, multiplayer, tocado, coordenada_repetida, coordenada_incorrecta, coordenada_inutil,
                seguir_jugando, nivel_facil_IA, filtros, tocadoIA = false, modo_tocado = false, modo_recorrer = false,
                modo_dar_la_vuelta = false, una_vez = false, posible_barco_aleatorio = true, posible_barco_tocado = true;
        int[] coordenada = new int[2], coordenada_tocada_IA = new int[2], mod_coordenada = new int[2], intervalo_bloques_restantes = new int[2];
        boolean[] aleatoriedadIA = new boolean[2];


        bienvenida();
        do {
            multiplayer = sistemaEleccion2Opciones("TIPO DE JUEGO", "Player vs Player", "Player vs IA");
            if (!multiplayer) {
                nivel_facil_IA = sistemaEleccion2Opciones("NIVEL DE DIFICULTAD", "Nivel de IA Fácil", "Nivel de IA Difícil");
                if (nivel_facil_IA) {
                    numero_dificultad = 3;
                }
            }
            pedirNombresJugadores(nombre_jugadores, multiplayer, num_player);
            distribucion_manual = sistemaEleccion2Opciones("TIPO DE DISTRIBUCIÓN DEL TABLERO", "Distribución Manual de los Barcos", "Distribución Automática de los Barcos");
            if (distribucion_manual) {
                eleccionManual(tablero, coordenadas_barcos_1, nombre_jugadores, num_player);
            } else {
                eleccionAutomatica(tablero, coordenadas_barcos_1, multiplayer, num_player, nombre_jugadores);
            }
            num_player = 2;
            pedirNombresJugadores(nombre_jugadores, multiplayer, num_player);
            if (multiplayer) {
                distribucion_manual = sistemaEleccion2Opciones("TIPO DE DISTRIBUCIÓN DEL TABLERO", "Distribución Manual de los Barcos", "Distribución Automática de los Barcos");
                if (distribucion_manual) {
                    eleccionManual(tablero, coordenadas_barcos_2, nombre_jugadores, num_player);
                } else {
                    eleccionAutomatica(tablero, coordenadas_barcos_2, true, num_player, nombre_jugadores);
                }
            } else {
                eleccionAutomatica(tablero, coordenadas_barcos_2, false, num_player, nombre_jugadores);
            }
            vaciarTablero(tablero);
            vaciarBBDDCoordenadas(coordenadas_probadas_1);
            vaciarBBDDCoordenadas(coordenadas_probadas_2);
            vaciarBBDDCoordenadas(coordenadas_probadas);
            comprobarIntervaloDeBloquesBarcosRestantes(coordenadas_probadas, coordenadas_barcos, intervalo_bloques_restantes);

            empiezaElJuego(nombre_jugadores);
            do {

                /* Cambio de Turno */
                if (num_player == 1) {
                    coordenadas_probadas_1 = coordenadas_probadas;
                    num_player = 2;
                    coordenadas_barcos = coordenadas_barcos_1;
                    coordenadas_probadas = coordenadas_probadas_2;
                } else {
                    coordenadas_probadas_2 = coordenadas_probadas;
                    num_player = 1;
                    coordenadas_barcos = coordenadas_barcos_2;
                    coordenadas_probadas = coordenadas_probadas_1;
                }

                vaciarTablero(tablero);
                insertarCoordenadasTablero(coordenadas_probadas, coordenadas_barcos, tablero);

                if (!(!multiplayer && num_player == 2)) {
                    pedirCoordenada(coordenada, coordenadas_probadas, coordenadas_barcos, num_player, tablero, nombre_jugadores);
                } else {
                    filtros = ((int) Math.round(Math.random() * 10) <= numero_dificultad);
                    mostrarTablero(tablero, nombre_jugadores, num_player);
                    mostrarBarcosRestantes(coordenadas_probadas, coordenadas_barcos);
                    Introducir.delay(1500);
                    Introducir.limpiarConsola();
                    do {
                        if (modo_tocado) {
                            if (modo_recorrer && !modo_dar_la_vuelta) {
                                moverCoordenadaEnModoRecorrer(aleatoriedadIA, mod_coordenada, false);
                            } else if (modo_dar_la_vuelta) {
                                moverCoordenadaEnModoRecorrer(aleatoriedadIA, mod_coordenada, true);
                            } else {
                                resetAleatoriedad(aleatoriedadIA, mod_coordenada);
                                moverCoordenadaEnModoRecorrer(aleatoriedadIA, mod_coordenada, false);
                            }
                            coordenada[0] = (coordenada_tocada_IA[0] + mod_coordenada[0]);
                            coordenada[1] = (coordenada_tocada_IA[1] + mod_coordenada[1]);
                        } else {
                            coordenada[0] = (int) Math.round(Math.random() * (NUM_COLUMNAS - 1));
                            coordenada[1] = (int) Math.round(Math.random() * (NUM_FILAS - 1));
                        }
                        coordenada_repetida = comprobarCoordenadaRepetida(coordenada, coordenadas_probadas);
                        coordenada_incorrecta = comprobarCoordenadaFueraDelTablero(coordenada);
                        if (filtros && !coordenada_repetida && !coordenada_incorrecta) {
                            coordenada_inutil = comprobarCoordenadaInutil(coordenadas_probadas, coordenada);
                            if (!coordenada_inutil) {
                                posible_barco_aleatorio = comprobarPosibilidadBarcoModoAleatorio(intervalo_bloques_restantes, coordenada, coordenadas_probadas, modo_tocado);
                                posible_barco_tocado = comprobarPosibilidadBarcoModoTocado(coordenadas_probadas, coordenada, aleatoriedadIA, intervalo_bloques_restantes, modo_tocado, modo_recorrer, mod_coordenada);
                            }
                        } else {
                            coordenada_inutil = false;
                            posible_barco_aleatorio = true;
                            posible_barco_tocado = true;
                        }
                        if (coordenada_inutil || coordenada_incorrecta || coordenada_repetida) {
                            tocadoIA = false;
                        }
                        modo_dar_la_vuelta = activarModoDarLaVuelta(modo_recorrer, tocadoIA);
                    } while (coordenada_repetida || coordenada_incorrecta || coordenada_inutil || !posible_barco_aleatorio || !posible_barco_tocado);
                }

                tocado = comprobarTocado(coordenadas_barcos, coordenada);

                if (!multiplayer && num_player == 2) {
                    tocadoIA = tocado;
                    modo_tocado = activarModoTocado(tocadoIA, modo_tocado, coordenada_tocada_IA, coordenada);
                    if (modo_tocado && tocadoIA) {
                        if (una_vez) {
                            modo_recorrer = true;
                        }
                        una_vez = true;
                    }
                    modo_dar_la_vuelta = activarModoDarLaVuelta(modo_recorrer, tocadoIA);
                }

                guardarCoordenada(coordenadas_probadas, coordenadas_barcos, coordenada, tocado);

                if (!multiplayer && num_player == 2 && modo_tocado) {
                    for (int i = 0; i < coordenadas_probadas[2].length; i++) {
                        if ((coordenada_tocada_IA[0] == coordenadas_probadas[2][i][0]) && (coordenada_tocada_IA[1] == coordenadas_probadas[2][i][1])) {
                            modo_tocado = false;
                            modo_recorrer = false;
                            modo_dar_la_vuelta = false;
                            una_vez = false;
                            coordenada_tocada_IA[0] = -1;
                            coordenada_tocada_IA[1] = -1;
                            comprobarIntervaloDeBloquesBarcosRestantes(coordenadas_probadas, coordenadas_barcos, intervalo_bloques_restantes);
                            break;
                        }
                    }
                }

                vaciarTablero(tablero);
                insertarCoordenadasTablero(coordenadas_probadas, coordenadas_barcos, tablero);
                mostrarTablero(tablero, nombre_jugadores, num_player);
                mostrarBarcosRestantes(coordenadas_probadas, coordenadas_barcos);

                Introducir.delay(3000);
                Introducir.limpiarConsola();
                tocados_restantes = comprobarTocadosRestantes(coordenadas_probadas);
            } while (tocados_restantes != 0);

            mensajeFinal(nombre_jugadores, multiplayer, num_player);
            statsJugadores(coordenadas_barcos_1, coordenadas_barcos_2, coordenadas_probadas_1, coordenadas_probadas_2, nombre_jugadores);
            seguir_jugando = sistemaEleccion2Opciones("VOLVER A JUGAR", "Si, quiero volver a jugar", "No, quiero cerrar el programa");
            if (seguir_jugando) {
                num_player = 1;
            }
        } while (seguir_jugando);
    }

    /**
     * Crea la estructura de datos para almacenar las coordenadas de los barcos
     *
     * @return Estructura de datos para almacenar las coordenadas de los barcos
     */
    private static int[][][] BBDD_Barcos() {
        int[][][] coordenadas_barcos = new int[BARCOS.length][][];

        for (int i = 0; i < coordenadas_barcos.length; i++) {
            coordenadas_barcos[i] = new int[Integer.parseInt(BARCOS[i][1])][2];
        }
        return coordenadas_barcos;
    }

    /**
     * Crea la estructura de datos para almacenar las coordenadas probadas por el usuario
     *
     * @param max_tocados Número total de bloques de los barcos disponibles
     * @return Estructura de datos para almacenar las coordenadas probadas por el usuario
     */
    private static int[][][] BBDD_Coordenadas(int max_tocados) {
        int[][][] coordenadas_probadas = new int[3][][];
        coordenadas_probadas[0] = new int[((NUM_FILAS * NUM_COLUMNAS) - max_tocados)][2];
        coordenadas_probadas[1] = new int[max_tocados][2];
        coordenadas_probadas[2] = new int[max_tocados][2];
        return coordenadas_probadas;
    }

    /**
     * Indica los pasos a realizar para la colocación manual de los barcos
     *
     * @param tablero            Array donde se insertarán los barcos, tocados, aguas, etc…
     * @param coordenadas_barcos Coordenadas de los barcos del jugador
     * @param nombre_jugadores   Nombres de los dos jugadores
     * @param num_player         Identificador de jugador poseyente del turno
     */
    private static void eleccionManual(String[][] tablero, int[][][] coordenadas_barcos, String[] nombre_jugadores, int num_player) {

        /* Vacía los datos del Tablero y Setea Las coordenadas de los Barcos a -1 */
        vaciarTablero(tablero);
        vaciarBBDDBarcos(coordenadas_barcos);
        controlesSeleccionManualDeBarcos();

        for (int i = 0; i < BARCOS.length; i++) {
            boolean pos_horizontal = true,
                    barco_colocado = false;
            int modY = 0, modX = 0,
                    num_bloques = Integer.parseInt(BARCOS[i][1]),
                    posicion_bloque_inicial = ((((num_bloques) / 2) + 1) - num_bloques),
                    posicion_bloque_final = (posicion_bloque_inicial + num_bloques);

            do {

                /* Comprueba que no se salgan nunca los barcos del tablero */
                for (int j = 0; j < num_bloques; j++) {
                    if (pos_horizontal) {
                        modX = corregirSalidaDelTableroPorRotacion(posicion_bloque_inicial, posicion_bloque_final, modX, NUM_COLUMNAS);
                    } else {
                        modY = corregirSalidaDelTableroPorRotacion(posicion_bloque_inicial, posicion_bloque_final, modY, NUM_FILAS);
                    }
                }


                /* Inserta los barcos colocados en el tablero y el barco actual con el color según este posicionado en el mismo */
                String color = insertarElementosDelTablero(i, coordenadas_barcos, num_bloques, pos_horizontal, modX, modY, posicion_bloque_inicial, tablero);
                mostrarTablero(tablero, nombre_jugadores, num_player);

                String wasd = Introducir.string("\n ¿Como quieres mover el " + BARCOS[i][0] + "? ").toLowerCase();
                if (!wasd.isEmpty()) {


                    /* Realiza la acción que demanda el usuario */
                    switch (wasd.charAt((wasd.length() - 1))) {
                        case 'w' -> modY = accionCaso_WA(modY);
                        case 'a' -> modX = accionCaso_WA(modX);
                        case 's' -> modY = accionCaso_SD(pos_horizontal, posicion_bloque_final, modY, NUM_FILAS);
                        case 'd' -> modX = accionCaso_SD(!pos_horizontal, posicion_bloque_final, modX, NUM_COLUMNAS);
                        case 'r' -> pos_horizontal = !pos_horizontal;
                    }
                } else {


                    /* Si el barco está en una posición correcta, guarda la posición de bloques del barco */
                    if (color.equals(COLORES[1])) {
                        barco_colocado = guardarPosicionBarcoColocado(num_bloques, pos_horizontal, coordenadas_barcos, i, posicion_bloque_inicial, modX, modY);
                        if (i == (BARCOS.length - 1)) {
                            Introducir.delay(5000);
                        }
                    }
                }
                Introducir.limpiarConsola();
            } while (!barco_colocado);
        }
    }

    /**
     * Indica los pasos a realizar para la colocación automática de los barcos
     *
     * @param tablero            Array donde se insertarán los barcos, tocados, aguas, etc…
     * @param coordenadas_barcos Coordenadas de los barcos del jugador
     * @param multiplayer        Indicador de juego contra otro jugador o contra la IA
     * @param num_player         Identificador de jugador poseyente del turno
     * @param nombre_jugadores   Nombres de los dos jugadores
     */
    private static void eleccionAutomatica(String[][] tablero, int[][][] coordenadas_barcos, boolean multiplayer, int num_player, String[] nombre_jugadores) {
        boolean probar_nueva_distribucion;

        do {
            /* En caso de que el usuario quiera probar otra distribución, vacía el tablero y las coordenadas anteriores */
            vaciarTablero(tablero);
            vaciarBBDDBarcos(coordenadas_barcos);

            for (int i = 0; i < BARCOS.length; i++) {

                int num_bloques = Integer.parseInt(BARCOS[i][1]),
                        posicion_bloque_inicial = ((((num_bloques) / 2) + 1) - num_bloques),
                        posicion_bloque_final = (posicion_bloque_inicial + num_bloques);
                boolean barco_colocado;

                do {


                    /* Esta parte se encarga de la aleatoriedad de la posición del barco */
                    int modX = (int) Math.round(Math.random() * (NUM_COLUMNAS - 1)),
                            modY = (int) Math.round(Math.random() * (NUM_FILAS - 1)),
                            orientacion = (int) Math.round(Math.random());
                    boolean pos_horizontal = true;
                    barco_colocado = false;

                    if (orientacion == 1) {
                        pos_horizontal = false;
                    }
                    for (int j = 0; j < num_bloques; j++) {
                        if (pos_horizontal) {
                            modX = corregirSalidaDelTableroPorRotacion(posicion_bloque_inicial, posicion_bloque_final, modX, NUM_COLUMNAS);
                        } else {
                            modY = corregirSalidaDelTableroPorRotacion(posicion_bloque_inicial, posicion_bloque_final, modY, NUM_FILAS);
                        }
                    }


                    /* Este nos indica si el barco está bien colocado y lo guarda */
                    String color = insertarElementosDelTablero(i, coordenadas_barcos, num_bloques, pos_horizontal, modX, modY, posicion_bloque_inicial, tablero);
                    if (color.equals(COLORES[1])) {
                        barco_colocado = guardarPosicionBarcoColocado(num_bloques, pos_horizontal, coordenadas_barcos, i, posicion_bloque_inicial, modX, modY);


                        /* Parte que se muestra al usuario, siempre que el mismo no sea la IA */
                        if (!(!multiplayer && num_player == 2)) {
                            mostrarTablero(tablero, nombre_jugadores, num_player);
                            Introducir.delay(750);
                            if (i == (BARCOS.length - 1)) {
                                Introducir.delay(2000);
                            }
                            Introducir.limpiarConsola();
                        }
                    }
                } while (!barco_colocado);
            }


            /* Pregunta para confirmar la distribución de los barcos */
            if (!(!multiplayer && num_player == 2)) {
                probar_nueva_distribucion = sistemaEleccion2Opciones("GUARDAR", "Probar con otra distribución", "Quedarse con la distribución actual");
            } else {
                probar_nueva_distribucion = false;
            }
        } while (probar_nueva_distribucion);
    }

    /**
     * Pantalla principal del juego
     */
    private static void bienvenida() {
        System.out.println(COLORES[7] + "\n\n\t\tHUNDIR LA FLOTA" + COLORES[0]);
        System.out.println("\t[Pulsa ENTER para comenzar]");
        Introducir.string();
        Introducir.limpiarConsola();
    }

    /**
     * Sistema que ofrece elegir "gráficamente" entre dos opciones
     *
     * @param labelTitulo  Etiqueta para el título
     * @param labelOpcion1 Etiqueta para la primera opción
     * @param labelOpcion2 Etiqueta para la segunda opción
     * @return Booleano resultante de la elección
     */
    private static boolean sistemaEleccion2Opciones(String labelTitulo, String labelOpcion1, String labelOpcion2) {
        String[] opcion = {COLORES[4], ""};
        boolean eleccion = false,
                eleccion_hecha = false;
        String seleccion;
        do {
            System.out.println(COLORES[7] + "\n\n   ---" + labelTitulo + "---\n" + COLORES[0]);
            System.out.println("\t" + opcion[0] + " " + labelOpcion1 + " " + COLORES[0]);
            System.out.println("\t" + opcion[1] + " " + labelOpcion2 + " " + COLORES[0]);

            seleccion = Introducir.string().toLowerCase();
            if (!seleccion.isEmpty()) {
                switch (seleccion.charAt((seleccion.length() - 1))) {
                    case 'w', 's':
                        if (opcion[1].equals(COLORES[4])) {
                            opcion[0] = COLORES[4];
                            opcion[1] = "";
                        } else {
                            opcion[0] = "";
                            opcion[1] = COLORES[4];
                        }
                        break;
                }
            } else {
                eleccion_hecha = true;
                if (opcion[0].equals(COLORES[4])) {
                    eleccion = true;
                }
            }
            Introducir.limpiarConsola();
        } while (!eleccion_hecha);
        return eleccion;
    }

    /**
     * Pantalla explicativa de los controles a la hora de colocar los barcos manualmente
     */
    private static void controlesSeleccionManualDeBarcos() {
        Introducir.limpiarConsola();
        System.out.println("\n\n\t\t-- CONTROLES --\n");
        System.out.println("  W\t->\tMover Barco hacia Arriba");
        System.out.println("  A\t->\tMover Barco hacia la Derecha");
        System.out.println("  S\t->\tMover Barco hacia la Izquierda");
        System.out.println("  D\t->\tMover Barco hacia Abajo");
        System.out.println("  R\t->\tRotar Barco");
        System.out.println(" ENTER\t->\tIntroducir Barco");
        Introducir.delay(5000);
        Introducir.limpiarConsola();
    }

    /**
     * Pantalla en la que se pedirán los nombres de los jugadores que vayan a enfrentarse
     *
     * @param nombre_jugadores Nombres de los dos jugadores
     * @param multiplayer      Indicador de juego contra otro jugador o contra la IA
     * @param num_player       Identificador de jugador poseyente del turno
     */
    private static void pedirNombresJugadores(String[] nombre_jugadores, boolean multiplayer, int num_player) {

        boolean nombre_vacio = true,
                nombre_repetido = true;

        Introducir.limpiarConsola();
        do {
            if (!(!multiplayer && num_player == 2)) {
                System.out.println(COLORES[7] + "\n\n\t\t--ELECCION DE NOMBRES--" + COLORES[0]);
                nombre_jugadores[num_player - 1] = Introducir.string(" Introduce el nombre del Player" + num_player + ": ");


                /* Comprobación para nombre vacío */
                if (nombre_jugadores[num_player - 1].isEmpty()) {
                    Introducir.limpiarConsola();
                    System.out.println("\n\n\t\t --- El nickname no puede estar vacío ---");
                    Introducir.delay(2000);
                    Introducir.limpiarConsola();
                } else {


                    /* Comprobación para nombre repetido */
                    nombre_vacio = false;
                    if (num_player == 2 && nombre_jugadores[1].trim().equals(nombre_jugadores[0].trim())) {
                        Introducir.limpiarConsola();
                        System.out.println("\n\n\t\t --- Tu nombre no puede ser el mismo que el del Player1 ---");
                        Introducir.delay(2000);
                        Introducir.limpiarConsola();
                    } else {
                        nombre_repetido = false;
                    }
                }
            } else {

                /* La IA se llamará "IA" siempre en cuando el Jugador no se haya puesto el mismo nombre */
                if (nombre_jugadores[0].trim().equals("IA")) {
                    nombre_jugadores[1] = "AI";
                } else {
                    nombre_jugadores[1] = "IA";
                }
                nombre_vacio = false;
                nombre_repetido = false;
            }
        } while (nombre_vacio || nombre_repetido);
        Introducir.limpiarConsola();
    }

    /**
     * Rellena el tablero con el valor de fondo dejándolo "limpio"
     *
     * @param tablero Array donde se insertarán los barcos, tocados, aguas, etc…
     */
    private static void vaciarTablero(String[][] tablero) {
        for (int j = 0; j < NUM_FILAS; j++) {
            for (int k = 0; k < NUM_COLUMNAS; k++) {
                tablero[j][k] = BACKGROUND;
            }
        }
    }

    /**
     * Rellena el array con la estructura de coordenadas_barcos, el valor nulo, "-1"
     *
     * @param coordenadas_barcos Array con la estructura coordenadas_barcos.
     */
    private static void vaciarBBDDBarcos(int[][][] coordenadas_barcos) {
        for (int j = 0; j < BARCOS.length; j++) {
            for (int k = 0; k < coordenadas_barcos[j].length; k++) {
                coordenadas_barcos[j][k][0] = -1;
                coordenadas_barcos[j][k][1] = -1;
            }
        }
    }

    /**
     * Rellena el array con la estructura de coordenadas_probadas, el valor nulo, "-1"
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     */
    private static void vaciarBBDDCoordenadas(int[][][] coordenadas_probadas) {
        for (int i = 0; i < coordenadas_probadas[0].length; i++) {
            for (int j = 0; j < coordenadas_probadas[0][i].length; j++) {
                coordenadas_probadas[0][i][j] = -1;
            }
        }
        for (int i = 0; i < coordenadas_probadas[1].length; i++) {
            for (int j = 0; j < coordenadas_probadas[1][i].length; j++) {
                coordenadas_probadas[1][i][j] = -1;
                coordenadas_probadas[2][i][j] = -1;
            }
        }
    }

    /**
     * Indica los pasos a realizar para introducir los barcos en el tablero
     *
     * @param i                       Número del barco el cual se encuentra en proceso de colocación
     * @param coordenadas_barcos      Coordenadas de los barcos del jugador
     * @param num_bloques             Número de bloques del barco con el que se está trabajando
     * @param pos_horizonal           En la colocación de los barcos, identificador de un barco horizontal o vertical
     * @param modX                    Adicción de coordenadas de eje X por parte de jugador
     * @param modY                    Adicción de coordenadas de eje Y por parte de jugador
     * @param posicion_bloque_inicial Posición relativa al centro del barco del primer bloque
     * @param tablero                 Array donde se insertarán los barcos, tocados, aguas, etc…
     * @return Color que nos indica si el barco se encuentra en una posición correcta
     */
    private static String insertarElementosDelTablero(int i, int[][][] coordenadas_barcos, int num_bloques, boolean pos_horizonal, int modX, int modY, int posicion_bloque_inicial, String[][] tablero) {
        vaciarTablero(tablero);
        String color = comprobarChoqueBarcoActualConOtrosBarcos(i, coordenadas_barcos, num_bloques, pos_horizonal, modX, modY, posicion_bloque_inicial);
        insertarBarcosColocados(coordenadas_barcos, tablero);
        insertarBarcoActual(num_bloques, pos_horizonal, tablero, modX, modY, posicion_bloque_inicial, color);
        return color;
    }

    /**
     * Comprueba que el barco en proceso de colocación no colisione con los barcos ya colocados
     *
     * @param i                       Número del barco el cual se encuentra en proceso de colocación
     * @param coordenadas_barcos      Coordenadas de los barcos del jugador
     * @param num_bloques             Número de bloques del barco con el que se está trabajando
     * @param pos_horizonal           En la colocación de los barcos, identificador de un barco horizontal o vertical
     * @param modX                    Adicción de coordenadas de eje X por parte de jugador
     * @param modY                    Adicción de coordenadas de eje Y por parte de jugador
     * @param posicion_bloque_inicial Posición relativa al centro del barco del primer bloque
     * @return Color que nos indica si el barco se encuentra en una posición correcta
     */
    private static String comprobarChoqueBarcoActualConOtrosBarcos(int i, int[][][] coordenadas_barcos, int num_bloques, boolean pos_horizonal, int modX, int modY, int posicion_bloque_inicial) {
        String color = COLORES[1];

        for (int j = 0; j < i; j++) {
            for (int k = 0; k < coordenadas_barcos[j].length; k++) {
                for (int l = 0; l < num_bloques; l++) {
                    if (pos_horizonal) {
                        color = comprobadorDeChoque(coordenadas_barcos, j, k, l, posicion_bloque_inicial, modX, modY, 0, 1, color);
                    } else {
                        color = comprobadorDeChoque(coordenadas_barcos, j, k, l, posicion_bloque_inicial, modY, modX, 1, 0, color);
                    }
                    if (color.equals(COLORES[2])) {
                        break;
                    }
                }
            }
        }
        return color;
    }

    /**
     * Condicional que comprueba si un bloque del barco en proceso de colocación colisiona con otro barco
     *
     * @param coordenadas_barcos      Coordenadas de los barcos del jugador
     * @param j                       Número del barco colocado el cual se está comprobando que no esté colisionando con el barco en proceso de colocación
     * @param k                       Número del bloque del barco colocado el cual se está comprobando que no esté colisionando con el barco en proceso de colocación
     * @param l                       Número del bloque del barco en proceso de colocación
     * @param posicion_bloque_inicial Posición relativa al centro del barco del primer bloque
     * @param mod1                    Indicador de la adición a los ejes tenemos que sumar dependiendo de si su posición es horizontal o vertical (modX o modY)
     * @param mod2                    Indicador de la adición a los ejes tenemos que sumar dependiendo de si su posición es horizontal o vertical (modX o modY)
     * @param indice1                 Indicador de con que eje tenemos que usar una manera de comprobación de colisión u otra dependiendo de si su posición es horizontal o vertical (0 o 1)
     * @param indice2                 Indicador de con que eje tenemos que usar una manera de comprobación de colisión u otra dependiendo de si su posición es horizontal o vertical (0 o 1)
     * @param color                   Color que indica si el barco está mal colocado
     * @return Color que indica si el barco está mal colocado
     */
    private static String comprobadorDeChoque(int[][][] coordenadas_barcos, int j, int k, int l, int posicion_bloque_inicial, int mod1, int mod2, int indice1, int indice2, String color) {
        if (((coordenadas_barcos[j][k][indice1] == ((posicion_bloque_inicial + mod1) + l)) || ((coordenadas_barcos[j][k][indice1] - 1) == ((posicion_bloque_inicial + mod1) + l)) || ((coordenadas_barcos[j][k][indice1] + 1) == ((posicion_bloque_inicial + mod1) + l))) &&
                (((coordenadas_barcos[j][k][indice2] == mod2) || (coordenadas_barcos[j][k][indice2] - 1) == mod2) || ((coordenadas_barcos[j][k][indice2] + 1) == mod2))) {
            color = COLORES[2];
        }
        return color;
    }

    /**
     * En la fase de colocación de los barcos, se encarga en insertar los barcos que ya han sido colocados
     *
     * @param coordenadas_barcos Coordenadas de los barcos del jugador
     * @param tablero            Array donde se insertarán los barcos, tocados, aguas, etc…
     */
    private static void insertarBarcosColocados(int[][][] coordenadas_barcos, String[][] tablero) {
        for (int j = 0; j < BARCOS.length; j++) {
            for (int k = 0; k < coordenadas_barcos[j].length; k++) {
                if (coordenadas_barcos[j][k][0] != -1) {
                    tablero[coordenadas_barcos[j][k][1]][coordenadas_barcos[j][k][0]] = (COLORES[3] + BARCO + COLORES[0]);
                }
            }
        }
    }

    /**
     * Bucle que añade el barco en proceso de colocación con su respectivo color al tablero
     *
     * @param num_bloques             Número de bloques del barco con el que se está trabajando
     * @param pos_horizonal           En la colocación de los barcos, identificador de un barco horizontal o vertical
     * @param tablero                 Array donde se insertarán los barcos, tocados, aguas, etc…
     * @param modX                    Adicción de coordenadas de eje X por parte de jugador
     * @param modY                    Adicción de coordenadas de eje Y por parte de jugador
     * @param posicion_bloque_inicial Posición relativa al centro del barco del primer bloque
     * @param color                   Color que indica si el barco está en una posición correcta o no
     */
    private static void insertarBarcoActual(int num_bloques, boolean pos_horizonal, String[][] tablero, int modX, int modY, int posicion_bloque_inicial, String color) {
        for (int j = 0; j < num_bloques; j++) {
            if (pos_horizonal) {
                tablero[modY][(posicion_bloque_inicial + modX) + j] = (color + BARCO + COLORES[0]);
            } else {
                tablero[(posicion_bloque_inicial + modY) + j][modX] = (color + BARCO + COLORES[0]);
            }
        }
    }

    /**
     * Mueve el barco hacia dentro del tablero en caso de que, por rotación, se haya salido del mismo
     *
     * @param posicion_bloque_inicial Posición relativa al centro del barco del primer bloque
     * @param posicion_bloque_final   Posición relativa al centro del barco del último bloque
     * @param mod                     Indicador de la adición a los ejes tenemos que sumar dependiendo de si su posición es horizontal o vertical (modX o modY)
     * @param num_limite              Indicador del límite del tablero dependiendo de si su posición es horizontal o vertical (NUM_COLUMNAS o NUM_FILAS)
     * @return Cantidad de modificaciones que se deben realizar para mantener el barco dentro del tablero
     */
    private static int corregirSalidaDelTableroPorRotacion(int posicion_bloque_inicial, int posicion_bloque_final, int mod, int num_limite) {
        while ((posicion_bloque_final + mod) > num_limite) {
            mod--;
        }
        while ((posicion_bloque_inicial + mod) < 0) {
            mod++;
        }
        return mod;
    }


    /**
     * Acción a realizar en caso de que el barco quiera ir hacia arriba o hacia la izquierda
     *
     * @param mod Indicador de la variable a la cual se realiza la acción dependiendo de si su posición es horizontal o vertical (modX o modY)
     * @return Variable modificada
     */
    private static int accionCaso_WA(int mod) {
        if (mod > 0) {
            mod--;
        }
        return mod;
    }

    /**
     * Acción a realizar en caso de que el barco quiera ir hacia abajo o hacia la derecha
     *
     * @param pos_horizontal        En la colocación de los barcos, identificador de un barco horizontal o vertical
     * @param posicion_bloque_final Posición relativa al centro del barco del último bloque
     * @param mod                   Indicador de la variable a la cual se realiza la acción dependiendo de si su posición es horizontal o vertical (modX o modY)
     * @param num_limite            Indicador de número límite del tablero dependiendo de si su posición es horizontal o vertical (NUM_COLUMNAS o NUM_FILAS)
     * @return Variable modificada
     */
    private static int accionCaso_SD(boolean pos_horizontal, int posicion_bloque_final, int mod, int num_limite) {
        if (pos_horizontal) {
            if (mod < (num_limite - 1)) {
                mod++;
            }
        } else {
            if (posicion_bloque_final < num_limite) {
                mod++;
            }
        }
        return mod;
    }

    /**
     * Guarda las coordenadas del barco que se quiere colocar en el array coordenadas_barcos
     *
     * @param num_bloques             Número de bloques del barco con el que se está trabajando
     * @param pos_horizonal           En la colocación de los barcos, identificador de un barco horizontal o vertical
     * @param coordenadas_barcos      Coordenadas de los barcos del jugador
     * @param i                       Número del barco que se quiere guardar
     * @param posicion_bloque_inicial Posición relativa al centro del barco del primer bloque
     * @param modX                    Adicción de coordenadas de eje X por parte de jugador
     * @param modY                    Adicción de coordenadas de eje Y por parte de jugador
     * @return Confirmación de que el barco se ha guardado
     */
    private static boolean guardarPosicionBarcoColocado(int num_bloques, boolean pos_horizonal, int[][][] coordenadas_barcos, int i, int posicion_bloque_inicial, int modX, int modY) {
        for (int j = 0; j < num_bloques; j++) {
            if (pos_horizonal) {
                coordenadas_barcos[i][j][0] = (posicion_bloque_inicial + modX) + j;
                coordenadas_barcos[i][j][1] = (modY);
            } else {
                coordenadas_barcos[i][j][0] = (modX);
                coordenadas_barcos[i][j][1] = (posicion_bloque_inicial + modY) + j;
            }
        }
        return true;
    }

    /**
     * Muestra el tablero
     *
     * @param tablero          Array donde se insertarán los barcos, tocados, aguas, etc…
     * @param nombre_jugadores Nombres de los dos jugadores
     * @param num_player       Identificador de jugador poseyente del turno
     */
    private static void mostrarTablero(String[][] tablero, String[] nombre_jugadores, int num_player) {
        System.out.println(COLORES[7] + "\n\t\t--- TURNO DE " + nombre_jugadores[num_player - 1].toUpperCase() + " ---" + COLORES[0]);
        System.out.println();
        for (int j = 0; j <= NUM_FILAS; j++) {

            /* Espaciado para compensar el cambio de 1 carácter a 2 caracteres*/
            if (j < 10) {
                System.out.print(" ");
            }

            /* Introducción del número correspondiente a la fila */
            if (j != 0) {
                System.out.print(j + "| ");
            }

            for (int k = 0; k <= NUM_COLUMNAS; k++) {

                if (j == 0) {
                    if (k != 0) {
                        /* Introducimos las letras, con ASCII*/
                        System.out.print("¯" + (char) (64 + k) + "¯");
                    } else {
                        System.out.print(" | ");
                    }
                } else {
                    if (k != 0) {
                        /* Mostramos finalmente el tablero */
                        System.out.print(tablero[(j - 1)][(k - 1)]);
                    }
                }
            }
            System.out.println();
        }
    }

    /**
     * Muestra los barcos que quedan restantes y los barcos ya hundidos
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param coordenadas_barcos   Coordenadas de los barcos del rival
     */
    private static void mostrarBarcosRestantes(int[][][] coordenadas_probadas, int[][][] coordenadas_barcos) {
        String color;
        final int CANTIDAD_ESPACIOS = (NUM_COLUMNAS * 4);
        int contador_de_longitud = 0,
                contador_de_intros = 1;
        boolean barco_hundido;

        System.out.println(COLORES[7] + "\n\t -- BARCOS RESTANTES -- \n" + COLORES[0]);
        for (int i = 0; i < BARCOS.length; i++) {

            /* Primero se comprueba que el barco en el que estamos trabajando, esté hundido */
            barco_hundido = comprobacionDeBarcoHundido(coordenadas_probadas, coordenadas_barcos, i);

            if (barco_hundido) {
                color = COLORES[6];
            } else {
                color = COLORES[3];
            }

            for (int j = 0; j < Integer.parseInt(BARCOS[i][1]); j++) {

                /* Se introducirá el barco del color que le pertoca y se contarán cuantos caracteres ocupa */
                System.out.print(color + BARCO + COLORES[0]);
                contador_de_longitud += BARCO.length();
            }

            /* Si sobrepasa la constante "CANTIDAD_ESPACIOS" se introduce un intro y si no, se espacia para el siguiente barco */
            if (contador_de_longitud > (CANTIDAD_ESPACIOS * contador_de_intros) ||
                    (contador_de_longitud - 3) > (CANTIDAD_ESPACIOS * contador_de_intros) ||
                    (contador_de_longitud + 3) > (CANTIDAD_ESPACIOS * contador_de_intros)) {
                System.out.println("\n");
                contador_de_intros++;
            } else {
                System.out.print("  ");
                contador_de_longitud += 2;
            }
        }
        System.out.println("\n");
    }

    /**
     * Pantalla indicadora de que empieza el juego
     *
     * @param nombre_jugadores Nombres de los dos jugadores
     */
    private static void empiezaElJuego(String[] nombre_jugadores) {
        Introducir.limpiarConsola();
        System.out.println(COLORES[7] + "\n\n\n\t\t--- EMPIEZA EL JUEGO ---\n\t\t\t " + COLORES[0] + nombre_jugadores[0].toUpperCase() + " vs " + nombre_jugadores[1].toUpperCase());
        Introducir.delay(5000);
        Introducir.limpiarConsola();

    }

    /**
     * Pantalla en la que se demanda al jugador que introduzca una coordenada
     *
     * @param coordenada           Coordenada introducida por el jugador poseyente del turno
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param coordenadas_barcos   Coordenadas de los barcos del rival
     * @param num_player           Identificador de jugador poseyente del turno
     * @param tablero              Array donde se insertarán los barcos, tocados, aguas, etc…
     * @param nombre_jugadores     Nombres de los dos jugadores
     */
    private static void pedirCoordenada(int[] coordenada, int[][][] coordenadas_probadas, int[][][] coordenadas_barcos, int num_player, String[][] tablero, String[] nombre_jugadores) {
        String peticion;
        boolean coordenada_incorrecta,
                coordenada_repetida;

        do {
            coordenada_repetida = false;

            mostrarTablero(tablero, nombre_jugadores, num_player);
            mostrarBarcosRestantes(coordenadas_probadas, coordenadas_barcos);
            peticion = Introducir.string("¿Que coordenada quieres probar? ");
            coordenada_incorrecta = comprobarCoordenadaIncorrecta(peticion);
            if (!coordenada_incorrecta) {


                /* Si es válida la coordenada convertir a índices del array "tablero" y comprobar si está repetida */
                coordenada[0] = ((int) (Character.toUpperCase(peticion.charAt(0))) - 65);
                coordenada[1] = Integer.parseInt(peticion.substring(1)) - 1;
                coordenada_repetida = comprobarCoordenadaRepetida(coordenada, coordenadas_probadas);
            }

            /* Respuesta para cerciorar porque el dato introducido no es válido */
            if (coordenada_incorrecta) {
                if (peticion.isEmpty()) {
                    System.out.println("\n -- No has introducido ninguna coordenada --");
                } else {
                    System.out.println("\n -- La coordenada \"" + peticion.toUpperCase() + "\" no és válida --");
                }
            } else if (coordenada_repetida) {
                System.out.println("\n -- La coordenada \"" + peticion.toUpperCase() + "\" ya ha sido probada con anterioridad --");
            }
            Introducir.delay(1500);
            Introducir.limpiarConsola();
        } while (coordenada_incorrecta || coordenada_repetida);
    }

    /**
     * Comprobación de que el String introducido corresponde a una coordenada del tablero
     *
     * @param peticion Input introducido por el jugador para denominar una coordenada
     * @return Indicador que el String és o no és una coordenada del tablero
     */
    private static boolean comprobarCoordenadaIncorrecta(String peticion) {
        boolean coordenada_incorrecta = false;


        /* Requisitos: Tiene que tener 2 o más caracteres, el primero alfabético y los demás numéricos y estar dentro del rango del tablero */
        if (peticion.length() > 1) {
            if (Character.isAlphabetic(peticion.charAt(0))) {
                if (((int) (Character.toUpperCase(peticion.charAt(0))) - 64) > NUM_FILAS || ((int) (Character.toUpperCase(peticion.charAt(0))) - 64) < 0) {
                    coordenada_incorrecta = true;
                }
            } else {
                coordenada_incorrecta = true;
            }
            for (int i = 1; i < peticion.length(); i++) {
                if (!Character.isDigit(peticion.charAt(i))) {
                    coordenada_incorrecta = true;
                }
            }
            if (!coordenada_incorrecta && (Integer.parseInt(peticion.substring(1)) > NUM_COLUMNAS || Integer.parseInt(peticion.substring(1)) <= 0)) {
                coordenada_incorrecta = true;
            }
        } else {
            coordenada_incorrecta = true;
        }
        return coordenada_incorrecta;
    }

    /**
     * Comprobación que la coordenada introducida no se ha introducido con anterioridad
     *
     * @param coordenada           Coordenada introducida por el jugador poseyente del turno
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @return Indicador de que la coordenada introducida se ha o no se ha introducido con anterioridad
     */
    private static boolean comprobarCoordenadaRepetida(int[] coordenada, int[][][] coordenadas_probadas) {
        boolean coordenada_repetida = false;

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < coordenadas_probadas[i].length; j++) {
                if (coordenada[0] == coordenadas_probadas[i][j][0] && coordenada[1] == coordenadas_probadas[i][j][1]) {
                    coordenada_repetida = true;
                    break;
                }
            }
        }
        return coordenada_repetida;
    }

    /**
     * Comprueba que la coordenada introducida sea tocado o agua
     *
     * @param coordenadas_barcos Coordenadas de los barcos del rival
     * @param coordenada         Coordenada introducida por el jugador poseyente del turno
     * @return Indicador de si la coordenada tocado o agua
     */
    private static boolean comprobarTocado(int[][][] coordenadas_barcos, int[] coordenada) {
        boolean tocado = false;

        for (int i = 0; i < BARCOS.length; i++) {
            for (int j = 0; j < Integer.parseInt(BARCOS[i][1]); j++) {
                if ((coordenadas_barcos[i][j][0] == coordenada[0]) && (coordenadas_barcos[i][j][1] == coordenada[1])) {
                    tocado = true;
                    break;
                }
            }
        }
        return tocado;
    }

    /**
     * Indica los pasos para guardar la coordenada introducida, en el array coordenadas_probadas
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param coordenadas_barcos   Coordenadas de los barcos del rival
     * @param coordenada           Coordenada introducida por el jugador poseyente del turno
     * @param tocado               Identificador de que la coordenada probada ha sido tocado o no
     */
    private static void guardarCoordenada(int[][][] coordenadas_probadas, int[][][] coordenadas_barcos, int[] coordenada, boolean tocado) {

        if (!tocado) {
            buscarUltimoIndiceYGuardarCoordenada(coordenadas_probadas, coordenada, 0);
        } else {
            buscarUltimoIndiceYGuardarCoordenada(coordenadas_probadas, coordenada, 1);
            comprobarTocadoYHundido(coordenadas_probadas, coordenadas_barcos);
        }
    }

    /**
     * Busca el último índice del array que le pertoca, y guarda la coordenada introducida en él
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param coordenada           Coordenada introducida por el jugador poseyente del turno
     * @param indice               Indica en que array guardar la coordenada dependiendo si la misma es tocada o no
     */
    private static void buscarUltimoIndiceYGuardarCoordenada(int[][][] coordenadas_probadas, int[] coordenada, int indice) {
        int ultimo_indice = 0;

        for (int i = 0; i < coordenadas_probadas[indice].length; i++) {
            if (coordenadas_probadas[indice][i][0] != -1) {
                ultimo_indice++;
            } else {
                break;
            }
        }
        coordenadas_probadas[indice][ultimo_indice][0] = coordenada[0];
        coordenadas_probadas[indice][ultimo_indice][1] = coordenada[1];
    }

    /**
     * Comprueba el barco al que le corresponde la coordenada tocada se encuentre en tocado y hundido, en caso afirmativo, carga el barco en el array coordenadas_probadas
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param coordenadas_barcos   Coordenadas de los barcos del rival
     */
    private static void comprobarTocadoYHundido(int[][][] coordenadas_probadas, int[][][] coordenadas_barcos) {
        int tocado_mismo_barco;

        for (int i = 0; i < coordenadas_barcos.length; i++) {
            tocado_mismo_barco = 0;

            /* Por cada barco comprueba cuantas coordenadas tocadas */
            for (int j = 0; j < coordenadas_barcos[i].length; j++) {
                for (int k = 0; k < coordenadas_probadas[2].length; k++) {
                    if (coordenadas_barcos[i][j][0] == coordenadas_probadas[1][k][0] && coordenadas_barcos[i][j][1] == coordenadas_probadas[1][k][1]) {
                        tocado_mismo_barco++;
                    }
                }

                /* Si el barco tiene todas sus coordenadas en el array de tocados, se introduce en bloque al array de tocado y hundido */
                if (tocado_mismo_barco == coordenadas_barcos[i].length) {
                    for (int k = 0; k < coordenadas_barcos[i].length; k++) {
                        for (int l = 0; l < coordenadas_probadas[2].length; l++) {
                            if (coordenadas_probadas[2][l][0] == -1 || ((coordenadas_probadas[2][l][0] == coordenadas_barcos[i][k][0]) && (coordenadas_probadas[2][l][1] == coordenadas_barcos[i][k][1]))) {
                                coordenadas_probadas[2][l][0] = coordenadas_barcos[i][k][0];
                                coordenadas_probadas[2][l][1] = coordenadas_barcos[i][k][1];
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * En el juego, inserta las coordenadas de coordenadas_probadas, del color que le pertoca
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param coordenadas_barcos   Coordenadas de los barcos del rival
     * @param tablero              Array donde se insertarán los barcos, tocados, aguas, etc…
     */
    private static void insertarCoordenadasTablero(int[][][] coordenadas_probadas, int[][][] coordenadas_barcos, String[][] tablero) {

        /* Trampitas jejejej*/

//        for (int j = 0; j < BARCOS.length; j++) {
//            for (int k = 0; k < coordenadas_barcos[j].length; k++) {
//                tablero[coordenadas_barcos[j][k][1]][coordenadas_barcos[j][k][0]] = (COLORES[6] + BARCO + COLORES[0]);
//            }
//        }

        /* Introduce las coordenadas "Agua" */
        for (int i = 0; i < coordenadas_probadas[0].length; i++) {
            if (coordenadas_probadas[0][i][0] != -1) {
                tablero[coordenadas_probadas[0][i][1]][coordenadas_probadas[0][i][0]] = COLORES[5] + BARCO + COLORES[0];
            }
        }

        /* Introduce las coordenadas "Tocado" */
        for (int i = 0; i < coordenadas_probadas[1].length; i++) {
            if (coordenadas_probadas[1][i][0] != -1) {
                tablero[coordenadas_probadas[1][i][1]][coordenadas_probadas[1][i][0]] = COLORES[2] + BARCO + COLORES[0];
            }
        }

        /* Sobreescribe las coordenadas "Tocado y Hundido" por encima de los "Tocados" */
        for (int i = 0; i < coordenadas_probadas[2].length; i++) {
            if (coordenadas_probadas[2][i][0] != -1) {
                tablero[coordenadas_probadas[2][i][1]][coordenadas_probadas[2][i][0]] = COLORES[3] + BARCO + COLORES[0];
            }
        }
    }

    /**
     * Cuenta el número total de bloques de los barcos disponibles
     *
     * @return Número total de bloques de los barcos disponibles
     */
    private static int contarMaxNumTocados() {
        int max_tocados = 0;

        for (int i = 0; i < BARCOS.length; i++) {
            max_tocados += Integer.parseInt(BARCOS[i][1]);
        }
        return max_tocados;
    }

    /**
     * Comprueba cuantos bloques de barcos quedan restantes por tocar
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @return Número de bloques de barcos que quedan restantes por tocar.
     */
    private static int comprobarTocadosRestantes(int[][][] coordenadas_probadas) {
        int tocados_restantes = 0;

        for (int i = 0; i < coordenadas_probadas[1].length; i++) {
            if (coordenadas_probadas[1][i][0] == -1) {
                tocados_restantes++;
            }
        }
        return tocados_restantes;
    }

    /**
     * Asigna un lado aleatorio de la coordenada IA para el "Modo Tocado" de la IA
     *
     * @param aleatoriedadIA Array booleanano que indica que lado que dirección respecto al tocado va a tomar la IA a partir del "Modo Tocado"
     * @param mod_coordenada Adición a la coordenada tocada por la IA a partir del modo "Modo Tocado"
     */
    private static void resetAleatoriedad(boolean[] aleatoriedadIA, int[] mod_coordenada) {
        aleatoriedadIA[0] = false;
        aleatoriedadIA[1] = false;
        mod_coordenada[0] = 0;
        mod_coordenada[1] = 0;
        if ((int) Math.round(Math.random()) == 1) {
            aleatoriedadIA[0] = true;
        }
        if ((int) Math.round(Math.random()) == 1) {
            aleatoriedadIA[1] = true;
        }
    }

    /**
     * Añade o Resta un Valor a la modificación de la coordenada según la dirección del array "aleatoriedadIA"
     *
     * @param aleatoriedadIA     Array booleanano que indica que lado que dirección respecto al tocado va a tomar la IA a partir del "Modo Tocado"
     * @param mod_coordenada     Adición a la coordenada tocada por la IA a partir del modo "Modo Tocado""
     * @param modo_dar_la_vuelta Indicador del “Modo Dar La Vuelta” de la IA
     */
    private static void moverCoordenadaEnModoRecorrer(boolean[] aleatoriedadIA, int[] mod_coordenada, boolean modo_dar_la_vuelta) {
        if (aleatoriedadIA[0]) {
            if (!(aleatoriedadIA[1] == modo_dar_la_vuelta)) {
                mod_coordenada[0]++;
            } else {
                mod_coordenada[0]--;
            }
        } else {
            if (!(aleatoriedadIA[1] == modo_dar_la_vuelta)) {
                mod_coordenada[1]++;
            } else {
                mod_coordenada[1]--;
            }
        }
    }

    /**
     * Comprueba que la coordenada introducida no tiene un barco hundido alrededor sabiendo que este siempre será agua
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param coordenada           Coordenada introducida por el jugador poseyente del turno
     * @return Indicador de que la coordenada introducida no tiene un barco hundido alrededor
     */
    private static boolean comprobarCoordenadaInutil(int[][][] coordenadas_probadas, int[] coordenada) {
        boolean coordenada_inutil = false;

        for (int i = 0; i < coordenadas_probadas[2].length; i++) {
            if (coordenadas_probadas[2][i][0] != -1 && coordenadas_probadas[2][i][1] != -1) {
                for (int j = -1; j < 2; j++) {
                    for (int k = -1; k < 2; k++) {
                        if ((coordenada[0] + j) == coordenadas_probadas[2][i][0] && (coordenada[1] + k) == coordenadas_probadas[2][i][1]) {
                            coordenada_inutil = true;
                            break;
                        }
                    }
                }
            } else {
                break;
            }

        }
        return coordenada_inutil;
    }

    /**
     * Comprueba que la coordenada introducida por la IA a partir del "Modo Tocado" no se encuentra fuera del tablero
     *
     * @param coordenada Coordenada introducida por el jugador poseyente del turno
     * @return Indicador de que la coordenada introducida por la IA no se encuentra fuera del tablero
     */
    private static boolean comprobarCoordenadaFueraDelTablero(int[] coordenada) {
        return (coordenada[0] < 0 || coordenada[0] >= NUM_COLUMNAS || coordenada[1] < 0 || coordenada[1] >= NUM_FILAS);
    }

    /**
     * Condicional para activar el "Modo Tocado"
     *
     * @param tocadoIA             Indicador de que en la jugada anterior, la IA ha tocado o no un barco
     * @param modo_tocado          Indicador del “Modo Tocado” de la IA
     * @param coordenada_tocada_IA Coordenada tocada por la IA en “Modo Aleatorio”
     * @param coordenada           Coordenada introducida por el jugador poseyente del turno
     * @return Indicador de que se ha activado o no el "Modo Dar La Vuelta"
     */
    private static boolean activarModoTocado(boolean tocadoIA, boolean modo_tocado, int[] coordenada_tocada_IA, int[] coordenada) {
        if (tocadoIA && !modo_tocado) {
            coordenada_tocada_IA[0] = coordenada[0];
            coordenada_tocada_IA[1] = coordenada[1];
            modo_tocado = true;
        }
        return modo_tocado;
    }

    /**
     * Condicional para activar el "Modo Dar La Vuelta"
     *
     * @param modo_recorrer Indicador del “Modo Recorrer” de la IA
     * @param tocadoIA      Indicador de que en la jugada anterior, la IA ha tocado o no un barco
     * @return Indicador de que se ha activado o no el "Modo Dar La Vuelta"
     */
    private static boolean activarModoDarLaVuelta(boolean modo_recorrer, boolean tocadoIA) {
        return modo_recorrer && !tocadoIA;
    }

    /**
     * Comprueba que la coordenada introducida por la IA en "Modo Aleatorio" cabe el barco más grande disponible en horizontal o en vertical
     *
     * @param intervalo_bloques_restantes Intervalo de entre el número de bloques del barco más pequeño y del más grande no hundidos
     * @param coordenada                  Coordenada introducida por el jugador poseyente del turno
     * @param coordenadas_probadas        Coordenadas probadas por el jugador
     * @return Indicador de que en esa coordenada cabe el barco más grande disponible en horizontal o en vertical
     */
    private static boolean comprobarPosibilidadBarcoModoAleatorio(int[] intervalo_bloques_restantes, int[] coordenada, int[][][] coordenadas_probadas, boolean modo_tocado) {
        boolean posible_barco = true;
        boolean dar_la_vuelta_prueba = false,
                una_vez = false;
        if (!modo_tocado) {


            int[] mod_prueba = {0, 0},
                    prueba = new int[2];

            for (int i = 0; i < (intervalo_bloques_restantes[1] - 1); i++) {
                if (!dar_la_vuelta_prueba) {


                    /* Comprueba que tenga espacios vacíos a su derecha*/
                    mod_prueba[0]++;
                    prueba[0] = coordenada[0] + mod_prueba[0];
                    prueba[1] = coordenada[1];
                    posible_barco = posibleBarco(coordenadas_probadas, prueba);


                    /* Si se topa con una coordenada que sabemos que es agua, da la vuelta y sigue comprobando */
                    if (!posible_barco) {
                        dar_la_vuelta_prueba = true;
                        i -= 2;
                    }
                } else if (!una_vez) {
                    /* Vuelve el loop a la posicion de la última coordenada vacía y pone vuelve a la coordenada inicial  */
                    mod_prueba[0] = 0;
                    una_vez = true;
                } else {

                    /* Comprueba si tiene los espacios restantes a su izquierda */
                    mod_prueba[0]--;
                    prueba[0] = coordenada[0] + mod_prueba[0];
                    posible_barco = posibleBarco(coordenadas_probadas, prueba);
                    if (!posible_barco) {
                        break;
                    }
                }
            }

            /* En caso de que no tenga espacio horizontal, vuelve a probar pero en vertical */
            if (!posible_barco) {
                dar_la_vuelta_prueba = false;
                una_vez = false;
                mod_prueba[0] = 0;

                for (int i = 0; i < (intervalo_bloques_restantes[1] - 1); i++) {
                    if (!dar_la_vuelta_prueba) {


                        /* Comprueba que tenga espacios vacíos hacia abajo*/
                        mod_prueba[1]++;
                        prueba[0] = coordenada[0];
                        prueba[1] = coordenada[1] + mod_prueba[1];
                        posible_barco = posibleBarco(coordenadas_probadas, prueba);




                        /* Si se topa con una coordenada que sabemos que es agua, da la vuelta y sigue comprobando */
                        if (!posible_barco) {
                            dar_la_vuelta_prueba = true;
                            i -= 2;
                        }
                    } else if (!una_vez) {

                        /* Vuelve el loop a la posicion de la última coordenada vacía y pone vuelve a la coordenada inicial  */
                        mod_prueba[1] = 0;
                        una_vez = true;
                        posible_barco = true;
                    } else {

                        /* Comprueba que tenga espacios vacíos hacia arriba*/
                        mod_prueba[1]--;
                        prueba[0] = coordenada[0];
                        prueba[1] = coordenada[1] + mod_prueba[1];
                        posible_barco = posibleBarco(coordenadas_probadas, prueba);

                        /* Esta vez si no hay espacio, devolverá que la coordenada no es práctica */
                        if (!posible_barco) {
                            break;
                        }
                    }
                }
            }
        }
        return posible_barco;
    }

    /**
     * Comprueba que cabe el barco más pequeño en dirección de la prueba en el "Modo Tocado"
     *
     * @param coordenadas_probadas        Coordenadas probadas por el jugador
     * @param coordenada                  Coordenada introducida por el jugador poseyente del turno
     * @param aleatoriedadIA              Array booleanano que indica que lado que dirección respecto al tocado va a tomar la IA a partir del "Modo Tocado"
     * @param intervalo_bloques_restantes Intervalo de entre el número de bloques del barco más pequeño y del más grande no hundidos
     * @param modo_tocado                 Modo en el que la IA ha tocado un barco en modo aleatorio, y se encuentra buscando la orientación del mismo
     * @param modo_recorrer               Modo en el que la IA ya sabe la orientación del barco y lo está recorriendo hasta hundirlo, o hasta activar el "Modo Dar La Vuelta"
     * @param mod_coordenada              Adición a la coordenada tocada por la IA a partir del modo "Modo Tocado"
     * @return Indicador de que cabe o no el barco más pequeño en dirección a dirección de la prueba en el "Modo Tocado"
     */
    private static boolean comprobarPosibilidadBarcoModoTocado(int[][][] coordenadas_probadas, int[] coordenada, boolean[] aleatoriedadIA, int[] intervalo_bloques_restantes, boolean modo_tocado, boolean modo_recorrer, int[] mod_coordenada) {
        boolean posible_barco = true;
        if (intervalo_bloques_restantes[0] > 2) {
            if (modo_tocado && !modo_recorrer) {
                int[] mod_prueba = {0, 0},
                        prueba = new int[2];
                int veces_comprobadas = 0;
                final int MAX_COMPROBACIONES = 20;
                do {
                    for (int i = 0; i < (intervalo_bloques_restantes[0] - 2); i++) {
                        moverCoordenadaEnModoRecorrer(aleatoriedadIA, mod_prueba, false);
                        prueba[0] = coordenada[0] + mod_prueba[0];
                        prueba[1] = coordenada[1] + mod_prueba[1];
                        posible_barco = posibleBarco(coordenadas_probadas, prueba);
                        if (!posible_barco && veces_comprobadas <= MAX_COMPROBACIONES) {
                            veces_comprobadas++;
                            resetAleatoriedad(aleatoriedadIA, mod_coordenada);
                        } else if (veces_comprobadas > MAX_COMPROBACIONES) {
                            posible_barco = true;
                        } else {
                            break;
                        }
                    }
                } while (!posible_barco || veces_comprobadas <= MAX_COMPROBACIONES);
            }
        }
        return true;
    }

    /**
     * Comprueba que las coordenadas a comprobar por las funciones "comprobarPosibilidadBarcoModo..." son correctas o no
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param prueba               Coordenada a probar si és válida
     * @return Indicador de que la coordenada a probar és o no és válida
     */
    private static boolean posibleBarco(int[][][] coordenadas_probadas, int[] prueba) {
        boolean coordenada_inutil_prueba,
                coordenada_incorrecta_prueba,
                coordenada_repetida_prueba;

        coordenada_inutil_prueba = comprobarCoordenadaInutil(coordenadas_probadas, prueba);
        coordenada_incorrecta_prueba = comprobarCoordenadaFueraDelTablero(prueba);
        coordenada_repetida_prueba = comprobarCoordenadaRepetida(prueba, coordenadas_probadas);
        return !coordenada_inutil_prueba && !coordenada_incorrecta_prueba && !coordenada_repetida_prueba;
    }

    /**
     * Comprueba cuál és el intervalo de entre el número de bloques del barco más pequeño y del más grande no hundidos
     *
     * @param coordenadas_probadas        Coordenadas probadas por el jugador
     * @param coordenadas_barcos          Coordenadas de los barcos del rival
     * @param intervalo_bloques_restantes Intervalo de entre el número de bloques del barco más pequeño y del más grande no hundidos
     */
    private static void comprobarIntervaloDeBloquesBarcosRestantes(int[][][] coordenadas_probadas, int[][][] coordenadas_barcos, int[] intervalo_bloques_restantes) {
        boolean barco_hundido;
        if (NUM_COLUMNAS >= NUM_FILAS) {
            intervalo_bloques_restantes[0] = NUM_COLUMNAS;
        } else {
            intervalo_bloques_restantes[0] = NUM_FILAS;
        }
        intervalo_bloques_restantes[1] = 0;

        for (int i = 0; i < BARCOS.length; i++) {

            /* Primero comprueba que el barco no se encuentra hundido */
            barco_hundido = comprobacionDeBarcoHundido(coordenadas_probadas, coordenadas_barcos, i);

            /* De los barcos restantes comprueba que cual és el que contiene el mayor número de bloques*/
            if (!barco_hundido) {
                if (intervalo_bloques_restantes[0] > Integer.parseInt(BARCOS[i][1])) {
                    intervalo_bloques_restantes[0] = Integer.parseInt(BARCOS[i][1]);
                }
                if (intervalo_bloques_restantes[1] < Integer.parseInt(BARCOS[i][1])) {
                    intervalo_bloques_restantes[1] = Integer.parseInt(BARCOS[i][1]);
                }
            }
        }
    }

    /**
     * Comprueba si el barco que se pide se encuentra hundido
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param coordenadas_barcos   Coordenadas de los barcos del jugador
     * @param i                    Indicador del barco al que se le pida la comprobación
     * @return Indicador de sí el barco que se pide se encuentra o no, hundido
     */
    private static boolean comprobacionDeBarcoHundido(int[][][] coordenadas_probadas, int[][][] coordenadas_barcos, int i) {
        boolean barco_hundido = false;

        for (int k = 0; k < coordenadas_probadas[2].length; k++) {
            if ((coordenadas_barcos[i][1][0] == coordenadas_probadas[2][k][0]) && (coordenadas_barcos[i][1][1] == coordenadas_probadas[2][k][1])) {
                barco_hundido = true;
                break;
            }
        }
        return barco_hundido;
    }

    /**
     * Pantalla que contiene el mensaje de quien ha sido el ganador de la partida
     *
     * @param nombre_jugadores Nombres de los dos jugadores
     * @param multiplayer      Indicador de juego contra otro jugador o contra la IA
     * @param num_player       Identificador de jugador poseyente del turno
     */
    private static void mensajeFinal(String[] nombre_jugadores, boolean multiplayer, int num_player) {
        if (!(!multiplayer && num_player == 2)) {
            System.out.print("\n\n\n\t\t!Felicidades \"" + nombre_jugadores[(num_player - 1)].toUpperCase() + "\"!, has ganado esta partida");
        } else {
            System.out.print("\n\n\n\t\tOhhh... la IA te ha ganado esta vez, pero ánimos, en la siguiente seguro que la ganas");
        }
        Introducir.delay(5000);
        Introducir.limpiarConsola();
    }

    /**
     * Pantalla que muestra los datos resultantes de la partida de los dos jugadores
     *
     * @param coordenadas_barcos_1   Coordenadas de los barcos del jugador 1
     * @param coordenadas_barcos_2   Coordenadas de los barcos del jugador 2
     * @param coordenadas_probadas_1 Coordenadas probadas por el jugador 1
     * @param coordenadas_probadas_2 Coordenadas probadas por el jugador 2
     * @param nombre_jugadores       Nombres de los dos jugadores
     */
    private static void statsJugadores(int[][][] coordenadas_barcos_1, int[][][] coordenadas_barcos_2, int[][][] coordenadas_probadas_1, int[][][] coordenadas_probadas_2, String[] nombre_jugadores) {
        int[][] datos_de_partida = datosDePartida(coordenadas_barcos_1, coordenadas_barcos_2, coordenadas_probadas_1, coordenadas_probadas_2);

        System.out.println("\n\n\t\t\t\t\t ---- STATS ---");
        System.out.println("\t\t\t" + nombre_jugadores[0] + "\t\t\t vs \t\t" + nombre_jugadores[1]);
        System.out.println("\t\t\t" + datos_de_partida[0][0] + "\t\tBarcos Hundidos\t\t" + datos_de_partida[0][1]);
        System.out.println("\t\t\t" + datos_de_partida[1][0] + "\t\tNumero de Aguas\t\t" + datos_de_partida[1][1]);
        System.out.println("\t\t\t" + datos_de_partida[2][0] + "\t\tNumero de Tocados\t" + datos_de_partida[2][1]);
        System.out.println("\t\t\t" + datos_de_partida[3][0] + "%  \tPorc. de Aciertos\t" + datos_de_partida[3][1] + "%");

        Introducir.delay(10000);
        Introducir.limpiarConsola();
    }

    /**
     * Rellena de datos el array "datos_de_partida"
     *
     * @param coordenadas_barcos_1   Coordenadas de los barcos del jugador 1
     * @param coordenadas_barcos_2   Coordenadas de los barcos del jugador 2
     * @param coordenadas_probadas_1 Coordenadas probadas por el jugador 1
     * @param coordenadas_probadas_2 Coordenadas probadas por el jugador 2
     * @return Array "datos_de_partida" con los datos introducidos
     */
    private static int[][] datosDePartida(int[][][] coordenadas_barcos_1, int[][][] coordenadas_barcos_2, int[][][] coordenadas_probadas_1, int[][][] coordenadas_probadas_2) {
        int[][] datos_de_partida = new int[4][2];
        datos_de_partida[0][0] = contadorBarcosHundidos(coordenadas_barcos_2, coordenadas_probadas_1);
        datos_de_partida[0][1] = contadorBarcosHundidos(coordenadas_barcos_1, coordenadas_probadas_2);
        datos_de_partida[1][0] = contadorAguasYTocados(coordenadas_probadas_1, 0);
        datos_de_partida[1][1] = contadorAguasYTocados(coordenadas_probadas_2, 0);
        datos_de_partida[2][0] = contadorAguasYTocados(coordenadas_probadas_1, 1);
        datos_de_partida[2][1] = contadorAguasYTocados(coordenadas_probadas_2, 1);
        datos_de_partida[3][0] = Introducir.porcentaje(datos_de_partida[2][0], (datos_de_partida[1][0] + datos_de_partida[2][0]));
        datos_de_partida[3][1] = Introducir.porcentaje(datos_de_partida[2][1], (datos_de_partida[1][1] + datos_de_partida[2][1]));
        return datos_de_partida;
    }

    /**
     * Cuenta la cantidad de barcos hundidos por el jugador antes de que finalizase la partida
     *
     * @param coordenadas_barcos   Coordenadas de los barcos del rival
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @return Número de barcos hundidos por el jugador antes de que finalizase la partida
     */
    private static int contadorBarcosHundidos(int[][][] coordenadas_barcos, int[][][] coordenadas_probadas) {
        boolean barco_hundido;
        int num_barcos_hundidos = 0;
        for (int i = 0; i < BARCOS.length; i++) {
            barco_hundido = false;
            for (int j = 0; j < Integer.parseInt(BARCOS[i][1]); j++) {
                barco_hundido = comprobacionDeBarcoHundido(coordenadas_probadas, coordenadas_barcos, i);
            }
            if (barco_hundido) {
                num_barcos_hundidos++;
            }
        }
        return num_barcos_hundidos;
    }

    /**
     * Cuenta la cantidad de Aguas y de Tocados que ha realizado el jugador antes de que acabase la partida
     *
     * @param coordenadas_probadas Coordenadas probadas por el jugador
     * @param indice               Indicador para contar aguas o tocados dependiendo del índice del array "coordenadas_probadas" (0 o 1)
     * @return Número de aguas o tocados del array introducido
     */
    private static int contadorAguasYTocados(int[][][] coordenadas_probadas, int indice) {
        int numero = 0;

        for (int i = 0; i < coordenadas_probadas[indice].length; i++) {
            if (coordenadas_probadas[indice][i][0] != -1) {
                numero++;
            }
        }
        return numero;
    }
}