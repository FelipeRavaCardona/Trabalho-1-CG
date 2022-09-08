package programas;

import java.util.ArrayList;
import java.util.List;
/*
Base para QuadTree obtida através do link:
https://gist.github.com/AbhijeetMajumdar/c7b4f10df1b87f974ef4

Eu fiz alterações necessárias para que a QuadTree funcionasse para o meu caso
 */
public class QuadTree {
    int MAX_CAPACITY;
    int level = 0;
    List<Ponto> pontos;
    QuadTree northWest = null;
    QuadTree northEast = null;
    QuadTree southWest = null;
    QuadTree southEast = null;
    Poligono boundry;

    QuadTree(int level, Poligono boundry, int capacidade) {
        this.level = level;
        pontos = new ArrayList<>();
        this.boundry = boundry;
        MAX_CAPACITY = capacidade;
        boundry.desenhaPoligono();
    }


    void split() {

        Ponto p1 = boundry.obtemLimiteMin();
        Ponto p2 = new Ponto(boundry.obtemLimiteMin().x, boundry.obtemLimiteMax().y);
        Ponto p3 = boundry.obtemLimiteMax();
        Ponto p4 = new Ponto(boundry.obtemLimiteMax().x, boundry.obtemLimiteMin().y);

        Ponto central = new Ponto(p3.x - (p3.x - p2.x), p2.y - (p2.y - p1.y));

        Poligono polNw = new Poligono();

        Ponto nwP1 = new Ponto(p2.x,central.y);
        Ponto nwP2 = p2;
        Ponto nwP3 = new Ponto(central.x, p3.y);
        Ponto nwP4 = central;

        polNw.insereVertice(nwP1);
        polNw.insereVertice(nwP2);
        polNw.insereVertice(nwP3);
        polNw.insereVertice(nwP4);

        Poligono polNe = new Poligono();

        Ponto neP1 = central;
        Ponto neP2 = new Ponto(central.x, p3.y);
        Ponto neP3 = p3;
        Ponto neP4 = new Ponto(p4.x, central.y);

        polNe.insereVertice(neP1);
        polNe.insereVertice(neP2);
        polNe.insereVertice(neP3);
        polNe.insereVertice(neP4);

        Poligono polSe = new Poligono();

        Ponto seP1 = new Ponto(central.x, p1.y);
        Ponto seP2 = central;
        Ponto seP3 = new Ponto(p3.x, central.y);
        Ponto seP4 = p4;

        polSe.insereVertice(seP1);
        polSe.insereVertice(seP2);
        polSe.insereVertice(seP3);
        polSe.insereVertice(seP4);

        Poligono polSw = new Poligono();

        Ponto swP1 = p1;
        Ponto swP2 = new Ponto(p1.x, central.y);
        Ponto swP3 = central;
        Ponto swP4 = new Ponto(central.x, p4.y);

        polSw.insereVertice(swP1);
        polSw.insereVertice(swP2);
        polSw.insereVertice(swP3);
        polSw.insereVertice(swP4);


        northWest = new QuadTree(this.level + 1, polNw, MAX_CAPACITY);
        northEast = new QuadTree(this.level + 1, polNe, MAX_CAPACITY);
        southWest = new QuadTree(this.level + 1, polSw, MAX_CAPACITY);
        southEast = new QuadTree(this.level + 1, polSe, MAX_CAPACITY);

    }

    void insert(Ponto p) {
        if (!this.boundry.inRange(p)) {
            return;
        }

        if (pontos.size() < MAX_CAPACITY) {
            pontos.add(p);
            return;
        }
        // Exceeded the capacity so split it in FOUR
        if (northWest == null) {
            split();
        }

        // Check coordinates belongs to which partition
        if (this.northWest.boundry.inRange(p))
            this.northWest.insert(p);
        else if (this.northEast.boundry.inRange(p))
            this.northEast.insert(p);
        else if (this.southWest.boundry.inRange(p))
            this.southWest.insert(p);
        else if (this.southEast.boundry.inRange(p))
            this.southEast.insert(p);
        else
            System.out.printf("ERROR : Unhandled partition x: " + p.x + ", y: " + p.y);
    }
}