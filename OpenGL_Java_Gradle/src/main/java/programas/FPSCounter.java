package programas;

//source: http://www.41post.com/3099/programming/game-programming-basics-creating-a-fps-counter
final public class FPSCounter  
{  
    private static int currentFrame;  
    private static int lastFrame;  
    private static int totalTime;  
    private static short frames = 0;
    private static int refreshTime = 3 ;  // segundos
    private static int refreshTimeMilliseconds = refreshTime * 1000;  // milisegundos
  
    /** Comeca a contar o FPS**/  
    public final static void StartCounter()  
    {  
    	lastFrame = (int) System.currentTimeMillis();  
    }
    
    /** Define intervalo de atualizacao do FPS **/
    public static void setRefreshTime(int seconds) {
    	refreshTime = seconds;
    	refreshTimeMilliseconds = refreshTime * 1000;
    }
  
    /** Para a contagem e exibe o FPS */  
    public final static void StopAndPost()  
    {  
    	currentFrame = (int) System.currentTimeMillis();  
        totalTime += currentFrame - lastFrame;
        frames++;  
        
        if(totalTime >= refreshTimeMilliseconds)  
        {  
            System.out.println("FPS: " + Long.toString(frames / refreshTime));   
            frames = 0;
            totalTime = 0;
        }
        
        lastFrame = currentFrame;
    }  
} 