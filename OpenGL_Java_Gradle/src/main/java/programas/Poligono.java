package programas;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Poligono {
	private List<Ponto> vertices;
	
	public Poligono() {
		vertices = new ArrayList<Ponto>();

	}
	
	public Ponto getVertice(int index) {
		return new Ponto(vertices.get(index).x, vertices.get(index).y, vertices.get(index).z);
	}
	
	public long getNVertices() {
		return vertices.size();
	}
	
	public void insereVertice(Ponto ponto) {
		vertices.add(ponto);
	}
	
	public void desenhaPoligono() {
		glBegin(GL_LINE_LOOP);
		for (int i=0; i<vertices.size(); i++) 
			glVertex3d(vertices.get(i).x, vertices.get(i).y, vertices.get(i).z);
		
		
		glEnd();
	}
	
	public void desenhaVertice() {
		glBegin(GL_POINTS);
		
		for (int i=0; i<vertices.size(); i++)
			glVertex3d(vertices.get(i).x, vertices.get(i).y, vertices.get(i).z);
		
		
		glEnd();
	}
	
	public void imprime() {
		for (int i=0; i<vertices.size(); i++) 
			System.out.println(vertices.get(i));
	};


	public Ponto obtemLimiteMin()
	{
		Ponto Min = vertices.get(0);
		
		for (int i=0; i<vertices.size(); i++)
		{
			Min = Ponto.ObtemMinimo(vertices.get(i), Min);
		}
		
		return Min;
	}
	
	
	public Ponto obtemLimiteMax()
	{
		Ponto Max = vertices.get(0);
		
		for (int i=0; i<vertices.size(); i++)
		{
			Max = Ponto.ObtemMaximo(vertices.get(i), Max);
		}
		
		return Max;
	}

	public boolean contains(Ponto a){
		if(a.x > obtemLimiteMin().x && a.x < obtemLimiteMax().x){
			if(a.y > obtemLimiteMin().y && a.y < obtemLimiteMax().y){
				return true;
			}
		}
		return false;
	}

	public void alteraVertice(int i, Ponto P)
	{
		vertices.set(i, P);
	}

	public boolean inRange(Ponto p){
		if(p.x > obtemLimiteMin().x && p.x < obtemLimiteMax().x){
			if(p.y > obtemLimiteMin().y && p.y < obtemLimiteMax().y){
				return true;
			}
		}
		return false;
	}
}
