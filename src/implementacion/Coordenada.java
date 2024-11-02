package implementacion;

public class Coordenada {
    private int x;
    private int y;

    public Coordenada() {
    }

    public Coordenada(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public String toString() {
        return "Coordenada{x=" + this.x + ", y=" + this.y + '}';
    }
}
