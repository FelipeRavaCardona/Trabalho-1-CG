import static de.damios.guacamole.gdx.StartOnFirstThreadHelper.startNewJvmIfRequired;

import model.IWindow;
import programas.*;

public class Application {

    // WINDOW e a instancia do programa a ser executado pela metodo main
    // Para criar um novo programa, basta implementar a classe com a interface "IWindow"
    // (seguir modelo dos demais programas da pasta 'programas')

    // Programa 2D que desenha um quadrado
    //private static final IWindow WINDOW = (IWindow) ProgramaDesenhaQuadrado.getInstance();
    // Programa que le um poligono de um arquivo txt e o exibe na tela
    //private static final IWindow WINDOW = (IWindow) ProgramaPoligonos.getInstance();
    private static final IWindow WINDOW = (IWindow) ProgramaPontosNoPoligono.getInstance();

    public static void main(String[] args) {
        // Starts a new JVM if the application was started on macOS without the
        // -XstartOnFirstThread argument.
        if (startNewJvmIfRequired()) {
            System.exit(0);
        }

        // Roda o loop do programa instanciado em WINDOW
        WINDOW.run();
    }
}
