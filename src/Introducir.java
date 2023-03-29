import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.util.Scanner;

public class Introducir {
    private static Scanner sc = new Scanner(System.in);

    public static char caracter(String... label) {
        String[] labels = label;
        String s;
        char c = '\0';
        boolean b = false;
        do {
            if (labels.length != 0) {
                System.out.print(labels[0]);
            }
            s = sc.nextLine();
            if (s.isEmpty()) {
                System.out.println("\n-Primero tienes que introducir un carácter-");
            } else {
                if (s.length() == 1) {
                    if (Character.isAlphabetic(s.charAt(0))) {
                        c = s.charAt(0);
                        b = true;
                    } else if (Character.isDigit(s.charAt(0))) {
                        System.out.println("\n-No se aceptan valores numéricos-");
                    } else {
                        System.out.println("\n-No se aceptan símbolos-");
                    }
                } else {
                    System.out.println("\n-El dato que has introducido no es un único carácter-");
                }
            }
        } while (!b);
        return c;
    }

    public static int entero(String... label) {
        String[] labels = label;
        String s;
        int i = 0;
        boolean b = false;
        int simb = 0;
        int decim = 0;
        do {
            if (labels.length != 0) {
                System.out.print(labels[0]);
            }
            s = sc.nextLine();
            for (int j = 0; j < s.length(); j++) {
                if (!Character.isDigit(s.charAt(j))) {
                    simb++;
                    if (s.charAt(j) == ',' || s.charAt(j) == '.') {
                        decim++;
                    }
                }
            }
            if (simb == 1 && decim == 1 && s.length() >= 3 && Character.isDigit(s.charAt(0)) && Character.isDigit(s.charAt(s.length() - 1))) {
                System.out.println("\n-No se aceptan valores decimales en este caso-");
            } else if (s.isEmpty()) {
                System.out.println("\n-Tienes que seleccionar introducir un número primero-");
            } else if (simb == 0) {
                i = Integer.parseInt(s);
                b = true;
            } else {
                System.out.println("\n-El dato que has introducido no es un número-");
            }
            simb = 0;
            decim = 0;
        } while (!b);
        return i;
    }

    public static String string(String... label) {
        String[] labels = label;
        if (labels.length != 0) {
            System.out.print(labels[0]);
        }
        return new Scanner(System.in).nextLine();
    }

    public static boolean booleano(String... label) {
        String[] labels = label;
        boolean b = false;
        boolean st = false;
        char c;
        do {
            if (labels.length != 0) {
                System.out.print(labels[0] + " (S - Si / N - No): ");
            }
            c = Character.toLowerCase(caracter());
            if (c == 's') {
                b = true;
                st = true;
            } else if (c == 'n') {
                b = false;
                st = true;
            } else {
                System.out.println("\n-Este carácter no es una opción-");
            }
        } while (!st);
        return b;
    }

    public static int porcentaje(float valor1, float valor2){
        return  (int) ((valor1 / valor2) * 100);
    }

    public static void limpiarConsola() {
        //File >Settings> Keymap  -   ShortCut "ClearAll": Alt + Shift + 1
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_SHIFT);
            robot.keyPress(KeyEvent.VK_1);
            robot.keyRelease(KeyEvent.VK_ALT);
            robot.keyRelease(KeyEvent.VK_SHIFT);
            robot.keyRelease(KeyEvent.VK_1);
            robot.delay(30);
        } catch (AWTException ignored) {
        }
    }

    public static void delay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }
}