package programas;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import model.IWindow;
import util.Color;

import java.io.File;
import java.util.Objects;
import java.util.Scanner;

import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAspectRatio;


public class ProgramaPoligonos implements IWindow{
    private int width;
    private int height;
    private long glfwWindowAddress;

    private static ProgramaPoligonos INSTANCE = null;

    // Variaveis da janela
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int MIN_WIDTH = 400;
    private static final int MIN_HEIGHT = 600;
    private static final int MAX_WIDTH = GL_DONT_CARE;
    private static final int MAX_HEIGHT = GL_DONT_CARE;
    private static final String TITLE = "Hello World!";
    private static final Color BACKGROUND_COLOR = Color.BLACK;

    // Variaveis da janela logica do programa
    public static final int X_LOWER_BOUND = -10;
    public static final int X_UPPER_BOUND = 10;
    public static final int Y_LOWER_BOUND = -10;
    public static final int Y_UPPER_BOUND = 10;
    public static final int WORLD_WIDTH = Math.abs(X_LOWER_BOUND) + Math.abs(X_UPPER_BOUND);
    public static final int WORLD_HEIGHT = Math.abs(Y_LOWER_BOUND) + Math.abs(Y_UPPER_BOUND);

    // Flag sinalizando se o programa esta rodando ou nao (definir para falso quando quiser fechar o programa)
    private boolean running = false;

	// Variaveis do usuario
	private Ponto min, max;
	private Poligono mapa, convexHull, conjuntoDePonto;
    private FPSCounter fpsCounter = new FPSCounter();

    // Callback do evento de teclado
    private GLFWKeyCallbackI keyListenerInstance = new GLFWKeyCallbackI(){
        private static final int TOTAL_GLFW_KEY_BINDINGS = 350;
        private final boolean[] keyPressed = new boolean[TOTAL_GLFW_KEY_BINDINGS];

        public void invoke(long window, int key, int scanCode, int action, int mods) {
            // Armazena as teclas que foram clicadas e soltas
            if (action == GLFW_PRESS) {
                this.keyPressed[key] = true;
            } else if (action == GLFW_RELEASE) {
                this.keyPressed[key] = false;
            }

            // Coloque aqui as acoes referente ao teclado
            if (keyPressed[GLFW_KEY_ESCAPE]) {
                running = false;
            } 
        }
    };

    // Calback de resize da tela
    private GLFWWindowSizeCallbackI windowResizeInstance = new GLFWWindowSizeCallbackI() {
        @Override
        public void invoke(long window, int width, int height) {
            ProgramaDesenhaQuadrado.getInstance().setWidth(width);
            ProgramaDesenhaQuadrado.getInstance().setWidth(height);

            // Coloque aqui as acoes que devem ser feitas quando o usuario muda o tamanho da janela
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();

            // Define a area a ser ocupada pela área OpenGL dentro da Janela
            glViewport(0, 0, width, height);
            //glOrtho(X_LOWER_BOUND, X_UPPER_BOUND, Y_LOWER_BOUND, Y_UPPER_BOUND, 0, 1);
            glOrtho(min.x, max.x, min.y, max.y, 0, 1);
            glfwSetWindowAspectRatio(window, width, height);

            // Define os limites lógicos da área OpenGL dentro da Janela
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();

            
        }
    };

    // Construtor
    private ProgramaPoligonos() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
    }

    // Chamada Singleton
    public static ProgramaPoligonos getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProgramaPoligonos();
        }
        return INSTANCE;
    }

    // Metodo de IWindow, usado para iniciar o loop do programa
    public void run() {
        // Inicie as variaveis necessarias na funcao 'init'
        init();
        // Utilize a 'execution' para montar a logica do programa (atualiza a cada frame)
        execution();
        // Funcao para finalizar variaveis necessarias e fechar o programa
        terminateGracefully();
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private void init() {
        // Inicia variaveis da logica do programa
		mapa = new Poligono();
		convexHull = new Poligono();
		conjuntoDePonto = new Poligono();
		min = new Ponto(0, 0);
		max = new Ponto(10, 10);

        // Le valores do poligono
        leMapa("EstadoRS.txt");
        // Inicia contador
        FPSCounter.StartCounter();

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        boolean glfwStarted = glfwInit();

        // Throw error and terminate if GLFW initialization fails
        if (!glfwStarted) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowAddress = createAndConfigureWindow();

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindowAddress);


        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glOrtho(min.x, max.x, min.y, max.y, 0, 1);
        
        // Liga os callbacks na instancia das janelas
        setListeners();
        // Make the window visible
        glfwShowWindow(glfwWindowAddress);

        // Sinaliza que o programa esta rodando
        running = true;
    }


    private void execution() {
        // This is the main loop
        while (!glfwWindowShouldClose(glfwWindowAddress) && running) {
            display();

            glfwPollEvents();
        }
    }

    // Exibe o conteudo da janela
    private void display() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(BACKGROUND_COLOR.getRed(), BACKGROUND_COLOR.getGreen(), BACKGROUND_COLOR.getBlue(), 0.0f);

        // Insira os comandos de desenho aqui!
        //
        /////////////////////////////////////

        // Desenha Poligono
		glLineWidth(1);
		glColor3f(1, 1, 1);
		desenhaEixos();

		glLineWidth(2);
		glColor3f(1, 1, 0);
		mapa.desenhaPoligono();

        FPSCounter.StopAndPost();
            
        for (int i = 0; i < 1000; i++) {
            HaInterseccao(new Ponto(i,10), new Ponto(5,5*i), new Ponto(3,3), new Ponto(4,67));
        }

        glfwSwapBuffers(glfwWindowAddress);
    }

    
    private long createAndConfigureWindow() {
        // Create the window
        long windowAddress = glfwCreateWindow(this.width, this.height, TITLE, NULL, NULL);

        if (windowAddress == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // glfw window Configuration
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwSetWindowSizeLimits(windowAddress, MIN_WIDTH, MIN_HEIGHT, MAX_WIDTH, MAX_HEIGHT);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        return windowAddress;
    }

    private void setListeners() {
        glfwSetKeyCallback(glfwWindowAddress, keyListenerInstance);
        glfwSetWindowSizeCallback(glfwWindowAddress, windowResizeInstance);
    }

    private void terminateGracefully() {
        // Free memory upon leaving
        glfwFreeCallbacks(glfwWindowAddress);
        glfwDestroyWindow(glfwWindowAddress);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    // Funcao que monta um poligono com os dados do arquivo informado
    private void leMapa(String nome) {
		try {
			Scanner input = new Scanner(new File(nome));
			
			if (!input.hasNext()) {
				System.out.println("Erro ao abrir " + nome);
				return;
			}
			
			input.useDelimiter("\\s+");
			System.out.println("Lendo arquivo " + nome + "...");
			
			int nLinha = 0;
			int qtdVertices;
			double x, y;
			
			qtdVertices = input.nextInt();
			x = Double.parseDouble(input.next());
			y = Double.parseDouble(input.next());
			
			min = new Ponto(x, y);
			max = new Ponto(x, y);
			mapa.insereVertice(new Ponto(x, y));
			
			for (int i=0; i<qtdVertices-1; i++) {
				x = Double.parseDouble(input.next());
				y = Double.parseDouble(input.next());
				
				if (x<min.x) min.x = x;
				if (y<min.y) min.y = y;
				
				if (x>max.x) max.x = x;
				if (y>max.y) max.y = y;
				
				nLinha++;
				mapa.insereVertice(new Ponto(x, y));
			}
			
			input.close();
			
			System.out.println("Leitura concluida.");
			System.out.println("Minimo: " + min + "Maximo: " + max);
		}catch (Exception e) {
			System.out.println("Erro desconhecido: " + e.getMessage());
			e.printStackTrace();
		}
	}

    // **********************************************************************
	//
	// **********************************************************************
	private void desenhaEixos() {
		Ponto meio = new Ponto();
	    meio.x = (max.x+min.x)/2;
	    meio.y = (max.y+min.y)/2;
	    meio.z = (max.z+min.z)/2;

	    glBegin(GL_LINES);
	    	//  eixo horizontal
	        glVertex2d(min.x,meio.y);
	        glVertex2d(max.x,meio.y);
	        //  eixo vertical
	        glVertex2d(meio.x,min.y);
	        glVertex2d(meio.x,max.y);
	    glEnd();
	}

    
	
	// ###################  Funcoes do usuario  ###################
	
	// **********************************************************************
	//  Calcula o produto escalar entre os vetores V1 e V2
	//**********************************************************************

	private double prodEscalar(Ponto v1, Ponto v2) {
		return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
	}
	
	// **********************************************************************
	//  Calcula o produto vetorial entre os vetores V1 e V2
	//**********************************************************************
	private Ponto prodVetorial(Ponto v1, Ponto v2) {
		Ponto vresult = new Ponto();
	    vresult.x = v1.y * v2.z - (v1.z * v2.y);
	    vresult.y = v1.z * v2.x - (v1.x * v2.z);
	    vresult.z = v1.x * v2.y - (v1.y * v2.x);
	    return vresult;
	}
	

	
	/* ********************************************************************** */
	/*                                                                        */
	/*  Calcula a interseccao entre 2 retas (no plano "XY" Z = 0)             */
	/*                                                                        */
	/* k : ponto inicial da reta 1                                            */
	/* l : ponto final da reta 1                                              */
	/* m : ponto inicial da reta 2                                            */
	/* n : ponto final da reta 2                                              */
	/*                                                                        */
	/* s: valor do parametro no ponto de intersecao (sobre a reta KL)         */
	/* t: valor do parametro no ponto de intersecao (sobre a reta MN)         */
	/*                                                                        */
	/* ********************************************************************** */
	public ResultadoInterseccao intersec2d(Ponto k, Ponto l, Ponto m, Ponto n)
	{
	    double det, s, t;
	    
	    det = (n.x - m.x) * (l.y - k.y)  -  (n.y - m.y) * (l.x - k.x);
	    
	    if (det == 0.0)
	        return new ResultadoInterseccao(false, 0, 0);
	    
	    s = ((n.x - m.x) * (m.y - k.y) - (n.y - m.y) * (m.x - k.x))/ det ;
	    t = ((l.x - k.x) * (m.y - k.y) - (l.y - k.y) * (m.x - k.x))/ det ;
	    
	    return new ResultadoInterseccao(true, s, t);
	}
	
	public boolean HaInterseccao(Ponto k, Ponto l, Ponto m, Ponto n)
	{
	    ResultadoInterseccao resultado;

	    resultado = intersec2d( k,  l,  m,  n);
	    if (!resultado.resultado) return false;
	    if (resultado.s>=0.0 && resultado.s <=1.0 && resultado.t>=0.0 && resultado.t<=1.0)
	        return true;
	    else return false;
	}
}
