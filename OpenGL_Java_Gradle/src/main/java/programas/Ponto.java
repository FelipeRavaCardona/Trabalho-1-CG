package programas;
import java.lang.Math;

public class Ponto{
	public double x,y,z;
	
	public Ponto(double x, double y, double z) {
		set(x, y, z);
	}
	
	public Ponto(double x, double y) {
		set(x, y, 0);
	}
	
	public Ponto() {
		this(0, 0, 0);
	}
	
	public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString() {
		return "("+x+", "+y+")";
	}

	public void rotacionaZ(double angulo)
	{
		double xr, yr;
		//cout << "Angulo: " << angulo << " ";
		double anguloRad = angulo * 3.14159265359/180.0;
		xr = x*Math.cos(anguloRad) - y*Math.sin(anguloRad);
		yr = x*Math.sin(anguloRad) + y*Math.cos(anguloRad);
		x = xr;
		y = yr;
	}
	
	
	void rotacionaY(double angulo)
	{
		double xr, zr;
		double anguloRad = angulo* 3.14159265359/180.0;
		xr =  x*Math.cos(anguloRad) + z*Math.sin(anguloRad);
		zr = -x*Math.sin(anguloRad) + z*Math.cos(anguloRad);
		x = xr;
		z = zr;
	}
	
	void rotacionaX(double angulo)
	{
		double yr, zr;
		double anguloRad = angulo* 3.14159265359/180.0;
		yr =  y*Math.cos(anguloRad) - z*Math.sin(anguloRad);
		zr =  y*Math.sin(anguloRad) + z*Math.cos(anguloRad);
		y = yr;
		z = zr;
	}

	public static Ponto ObtemMaximo (Ponto P1, Ponto P2)
	{
		Ponto Max = new Ponto();
		
		Max.x = (P2.x > P1.x) ? P2.x : P1.x;
		Max.y = (P2.y > P1.y) ? P2.y : P1.y;
		Max.z = (P2.z > P1.x) ? P2.z : P1.z;

		return Max;
	}

	public static Ponto ObtemMinimo (Ponto P1, Ponto P2)
	{
		Ponto Min = new Ponto();
		
		Min.x = (P2.x < P1.x) ? P2.x : P1.x;
		Min.y = (P2.y < P1.y) ? P2.y : P1.y;
		Min.z = (P2.z < P1.x) ? P2.z : P1.z;

		return Min;
	}

	public static Ponto Add(Ponto p1, Ponto p2){
		return new Ponto(p1.x + p2.x, p1.y + p2.y, p1.z + p2.z);
	}

	public static Ponto Sub(Ponto p1, Ponto p2){
		return new Ponto(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
	}

	public static Ponto Mult(Ponto p, double scalar){
		return new Ponto(p.x * scalar, p.y * scalar, p.z * scalar);
	}
	
	public void imprime() {
		System.out.println("(" + x + ", " + y + ", " + z + ")");
	}

	public void imprime(String msg)
	{
		System.out.println(msg);
		imprime();
	}

	public void imprime(String msgAntes, String msgDepois)
	{
		imprime(msgAntes);
		System.out.println(msgDepois);
	}
}