package programas;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import model.IWindow;
import util.Color;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwSetWindowAspectRatio;


public class ProgramaPontosNoPoligono implements IWindow{
    private int width;
    private int height;
    private long glfwWindowAddress;

    private static ProgramaPontosNoPoligono INSTANCE = null;


    // Variaveis da janela
    private static final int DEFAULT_WIDTH = 500;
    private static final int DEFAULT_HEIGHT = 500;
    private static final int MIN_WIDTH = 500;
    private static final int MIN_HEIGHT = 500;
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
    double AnguloDoCampoDeVisao = 0.0f;
    private Ponto Min, Max, Tamanho, Meio;
    private Ponto PosicaoDoCampoDeVisao, PontoClicado;
    private Poligono PontosDoCenario, CampoDeVisao, TrianguloBase, Envelope;

    boolean desenhaEixos = true;
    boolean FoiClicado = false;

    private FPSCounter fpsCounter = new FPSCounter();

    ArrayList<Integer> contadorTeste1 = new ArrayList<>();
    ArrayList<Integer> contadorTeste2 = new ArrayList<>();
    ArrayList<Integer> contadorTeste3 = new ArrayList<>();
    private int calculo = 0;
    private int calculoLast = 0;

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
            }else if (keyPressed[GLFW_KEY_LEFT]){
                AnguloDoCampoDeVisao += 2;

            }else if (keyPressed[GLFW_KEY_RIGHT]){
                AnguloDoCampoDeVisao -= 2;

            }else if (keyPressed[GLFW_KEY_UP]){
                AvancaCampoDeVisao(2);

            }else if (keyPressed[GLFW_KEY_DOWN]){
                AvancaCampoDeVisao(-2);

            }else if (keyPressed[GLFW_KEY_F]){
                calculo = 1;
            } else if (keyPressed[GLFW_KEY_G]) {
                calculo = 2;
            } else if (keyPressed[GLFW_KEY_H]) {
                calculo = 3;
            } else if (keyPressed[GLFW_KEY_C]){
                if(calculo == 1){
                    System.out.println("Pontos verdes: " + contadorTeste1.get(1));
                    System.out.println("Pontos vermelhos: " + contadorTeste1.get(0));
                } else if(calculo == 2){
                    System.out.println("Pontos verdes: " + contadorTeste2.get(2));
                    System.out.println("Pontos amarelos: " + contadorTeste2.get(1));
                    System.out.println("Pontos vermelhos: " + contadorTeste2.get(0));
                } else if (calculo == 3) {

                } else {
                    System.out.println("Nenhuma opção foi escolhida ainda.");
                }
            }

            PosicionaTriAnguloDoCampoDeVisao();
            PosicionaEnvelope();
        }
    };

    //Atualiza a posição do envelope
    private void PosicionaEnvelope()
    {
        Ponto p1, p3;
        p1 = CampoDeVisao.obtemLimiteMin();
        p3 = CampoDeVisao.obtemLimiteMax();

        Ponto p2 = new Ponto(p1.x, p3.y);
        Ponto p4 = new Ponto(p3.x, p1.y);

        Envelope.alteraVertice(0, p1);
        Envelope.alteraVertice(1, p2);
        Envelope.alteraVertice(2, p3);
        Envelope.alteraVertice(3, p4);
    }

    public ArrayList<Integer> teste1(Poligono poligono, Poligono p2)
    {
        ArrayList<Integer> counter = new ArrayList<>();
        int pontosFora = 0;
        int pontosPoligono = 0;
        for (int i=0; i<poligono.getNVertices(); i++)
        {
            Ponto P;

            P = poligono.getVertice(i); // obtem a coordenada

            glBegin(GL_POINTS);


            if(testaColisaoBruta(P, p2)){
                glColor3f(0,1,0);
                pontosPoligono++;
            }
            else {
                glColor3f(1,0,0);
                pontosFora++;
            }

            glVertex3d(P.x,P.y,P.z);

            glEnd();
        }
        counter.add(pontosFora);
        counter.add(pontosPoligono);
        return counter;
    }

    public ArrayList<Integer> teste2(Poligono poligono, Poligono p2){
        ArrayList<Integer> counter = new ArrayList<>();
        int pontosFora = 0;
        int pontosPoligono = 0;
        int pontosEnvelope = 0;
        for (int i=0; i<poligono.getNVertices(); i++)
        {

            Ponto P;

            P = poligono.getVertice(i); // obtem a coordenada

            glBegin(GL_POINTS);

            if (testaColisaoEnvelope(P, Envelope)){
                glColor3f(255,255,0);
                pontosEnvelope++;
                if(testaColisaoBruta(P,p2)){
                    glColor3f(0,1,0);
                    pontosPoligono++;
                }
            }

            else {
                glColor3f(1,0,0);
                pontosFora++;
            }

            glVertex3d(P.x,P.y,P.z);

            glEnd();

        }
        pontosEnvelope = pontosEnvelope - pontosPoligono;
        counter.add(pontosFora);
        counter.add(pontosEnvelope);
        counter.add(pontosPoligono);
        return counter;
    }

    //todo
    public void teste3(Poligono p){
        Poligono tela = new Poligono();

        Ponto p1 = p.obtemLimiteMin();
        Ponto p3 = p.obtemLimiteMax();
        Ponto p2 = new Ponto(p1.x, p3.y);
        Ponto p4 = new Ponto(p3.x, p1.y);

        tela.insereVertice(p1);
        tela.insereVertice(p2);
        tela.insereVertice(p3);
        tela.insereVertice(p4);

        QuadTree quadTree = new QuadTree(1, p, 1);
        for(int i = 0; i < p.getNVertices(); i++){
            Ponto ponto = p.getVertice(i);
            quadTree.insert(ponto);
        }
    }

    public boolean testaColisaoEnvelope(Ponto a, Poligono poligono){
        if(a.x > poligono.obtemLimiteMin().x && a.x < poligono.obtemLimiteMax().x){
            if(a.y > poligono.obtemLimiteMin().y && a.y < poligono.obtemLimiteMax().y){
                return true;
            }
        }
        return false;
    }

    public boolean testaColisaoBruta(Ponto p, Poligono poligono){
        for(int i = 0; i < poligono.getNVertices(); i++){
            if(i+1 >= poligono.getNVertices()){
                Ponto a = poligono.getVertice(i);
                Ponto b = poligono.getVertice(0);

                Ponto v1 = new Ponto(b.x - a.x, b.y - a.y);
                Ponto v2 = new Ponto(p.x - a.x, p.y - a.y);

                Ponto z1 = prodVetorial(v1, v2);
                if(z1.z < 0){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                Ponto a = poligono.getVertice(i);
                Ponto b = poligono.getVertice(i+1);

                Ponto v1 = new Ponto(b.x - a.x, b.y - a.y);
                Ponto v2 = new Ponto(p.x - a.x, p.y - a.y);

                Ponto z1 = prodVetorial(v1, v2);
                if(z1.z < 0){

                }
                else{
                    return false;
                }
            }
        }
        return false;
    }

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
            glOrtho(Min.x, Max.x, Min.y, Max.y, 0, 1);
            glfwSetWindowAspectRatio(window, width, height);

            // Define os limites lógicos da área OpenGL dentro da Janela
            glMatrixMode(GL_MODELVIEW);
            glLoadIdentity();


        }
    };

    private GLFWMouseButtonCallbackI MouseCallback = new GLFWMouseButtonCallbackI() {
        @Override
        public void invoke(final long window, final int button, final int action, final int mods) {
            onMouseButton(window, button, action, mods);
        }
    };

    // Construtor
    private ProgramaPontosNoPoligono() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
    }

    // Chamada Singleton
    public static ProgramaPontosNoPoligono getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProgramaPontosNoPoligono();
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
        // Gera ou Carrega os pontos do cenario.
        // Note que o "aspect ratio" dos pontos deve ser o mesmo
        // da janela.
        // PontosDoCenario.LePoligono("PontosDenteDeSerra.txt");

        Tamanho = new Ponto();
        Meio = new Ponto();
        PosicaoDoCampoDeVisao = new Ponto();
        PontoClicado = new Ponto();

        PontosDoCenario = new Poligono();
        CampoDeVisao = new Poligono();
        TrianguloBase = new Poligono();
        Envelope = new Poligono();


        GeraPontos(1000, new Ponto(0,0), new Ponto(500,500));
        Min = PontosDoCenario.obtemLimiteMin();
        Max = PontosDoCenario.obtemLimiteMax();

        Min.x--;Min.y--;
        Max.x++;Max.y++;

        Meio = Ponto.Mult(Ponto.Add(Max, Min), 0.5); //(Max+Min) * 0.5; // Ponto central da janela
        Tamanho = Ponto.Sub(Max, Min);//(Max-Min);  // Tamanho da janela em X,Y

        // Ajusta variaveis do triangulo que representa o campo de visao
        PosicaoDoCampoDeVisao = Meio;
        AnguloDoCampoDeVisao = 0;

        // Cria o triangulo que representa o campo de visao
        CriaTriAnguloDoCampoDeVisao();
        PosicionaTriAnguloDoCampoDeVisao();
        CriaEnvelope();

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

        glOrtho(Min.x, Max.x, Min.y, Max.y, 0, 1);

        // Liga os callbacks na instancia das janelas
        setListeners();
        // Make the window visible
        glfwShowWindow(glfwWindowAddress);

        // Sinaliza que o programa esta rodando
        running = true;
    }


    // **********************************************************************
    //
    // **********************************************************************
    private void DesenhaEixos()
    {

        glBegin(GL_LINES);
        //  eixo horizontal
        glVertex2d(Min.x,Meio.y);
        glVertex2d(Max.x,Meio.y);
        //  eixo vertical
        glVertex2d(Meio.x,Min.y);
        glVertex2d(Meio.x,Max.y);
        glEnd();
    }


    public void DesenhaLinha(Ponto P1, Ponto P2){
        glBegin(GL_LINES);
        glVertex3d(P1.x,P1.y,P1.z);
        glVertex3d(P2.x,P2.y,P2.z);
        glEnd();
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

        if (desenhaEixos)
        {
            glLineWidth(1);
            glColor3f(1,1,1); // R, G, B  [0..1]
            DesenhaEixos();
        }

        //glPointSize(5);
        glColor3f(1,1,0); // R, G, B  [0..1]
        PontosDoCenario.desenhaVertice();

        glLineWidth(3);
        glColor3f(1,0,0); // R, G, B  [0..1]
        CampoDeVisao.desenhaPoligono();



        if (FoiClicado)
        {
            PontoClicado.imprime("- Ponto no universo: ", "\n");
            FoiClicado = false;
        }

        switch (calculo){
            case 1:
                contadorTeste1 = teste1(PontosDoCenario, CampoDeVisao);
                break;
            case 2:
                Envelope.desenhaPoligono();
                contadorTeste2 = teste2(PontosDoCenario, CampoDeVisao);
                break;
            case 3:
                teste3(PontosDoCenario);
                break;
        }

        FPSCounter.StopAndPost();

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
        glfwSetMouseButtonCallback(glfwWindowAddress, MouseCallback);
    }

    private void terminateGracefully() {
        // Free memory upon leaving
        glfwFreeCallbacks(glfwWindowAddress);
        glfwDestroyWindow(glfwWindowAddress);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
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

    // **********************************************************************
    // GeraPontos(int qtd)
    //      MŽtodo que gera pontos aleat—rios no intervalo [Min..Max]
    // **********************************************************************
    public void GeraPontos(int qtd, Ponto min, Ponto max){
        Ponto escala;

        // (min - max) * (1.0 / 1000.0)
        escala = Ponto.Mult(Ponto.Sub(max, min), (1.0 / 1000.0));

        Random r = new Random();

        for (int i=0; i<qtd; i++){
            double x = r.nextInt(1000);
            double y = r.nextInt(1000);

            x = x * escala.x + min.x;
            y = y * escala.y + min.y;

            PontosDoCenario.insereVertice(new Ponto(x, y));
        }
    }

    // **********************************************************************
    // void CriaTriAnguloDoCampoDeVisao()
    //  Cria um triangulo a partir do vetor (1,0,0), girando este vetor
    //  em 45 e -45 graus.
    //  Este vetor fica armazenado nas vari‡veis "TrianguloBase" e
    //  "CampoDeVisao"
    // **********************************************************************
    private void CriaTriAnguloDoCampoDeVisao(){
        Ponto vetor = new Ponto(1, 0, 0);

        TrianguloBase.insereVertice(new Ponto(0, 0, 0));
        CampoDeVisao.insereVertice(new Ponto(0, 0, 0));

        vetor = new Ponto(1, 0, 0);
        vetor.rotacionaZ(45);
        TrianguloBase.insereVertice(vetor);
        CampoDeVisao.insereVertice(vetor);

        vetor = new Ponto(1, 0, 0);
        vetor.rotacionaZ(-45);
        TrianguloBase.insereVertice(vetor);
        CampoDeVisao.insereVertice(vetor);
    }

    // Cria e insere pontos do envelope
    private void CriaEnvelope()
    {
        Ponto p1, p3;
        p1 = CampoDeVisao.obtemLimiteMin();
        p3 = CampoDeVisao.obtemLimiteMax();

        Ponto p2 = new Ponto(p1.x, p3.y);
        Ponto p4 = new Ponto(p3.x, p1.y);

        Envelope.insereVertice(p1);
        Envelope.insereVertice(p2);
        Envelope.insereVertice(p3);
        Envelope.insereVertice(p4);
    }


    // **********************************************************************
    // void PosicionaTriAnguloDoCampoDeVisao()
    //  Posiciona o campo de vis‹o na posicao PosicaoDoCampoDeVisao,
    //  com a orientacao "AnguloDoCampoDeVisao".
    //  O tamanho do campo de vis‹o eh de 25% da largura da janela.
    // **********************************************************************
    public void PosicionaTriAnguloDoCampoDeVisao()
    {
        double tamanho = Tamanho.x * 0.25;

        Ponto temp;

        for (int i=0;i<TrianguloBase.getNVertices();i++)
        {
            temp = TrianguloBase.getVertice(i);
            temp.rotacionaZ(AnguloDoCampoDeVisao);
            CampoDeVisao.alteraVertice(i, Ponto.Add(PosicaoDoCampoDeVisao, Ponto.Mult(temp, tamanho)));
        }
    }

    // **********************************************************************
    // void AvancaCampoDeVisao(double distancia)
    //  Move o campo de vis‹o "distancia" unidades pra frente ou pra tras.
    // **********************************************************************
    void AvancaCampoDeVisao(double distancia)
    {
        Ponto vetor = new Ponto(1,0,0);
        vetor.rotacionaZ(AnguloDoCampoDeVisao);
        PosicaoDoCampoDeVisao = Ponto.Add(PosicaoDoCampoDeVisao, Ponto.Mult(vetor, distancia));
    }

    private void onMouseButton(final long window, int button, int action, int mods){
        // Evento de mouse AQUI!
    }
}
