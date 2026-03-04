package drinkshop.domain;

public class Product {

    private int id;
    private String nume;
    private double pret;
    private BeverageCategory categorie;
    private BeverageType tip;

    public Product(int id, String nume, double pret,
                  BeverageCategory categorie,
                  BeverageType tip) {
        this.id = id;
        this.nume = nume;
        this.pret = pret;
        this.categorie = categorie;
        this.tip = tip;
    }

    public int getId() { return id; }
    public String getNume() { return nume; }
    public double getPret() { return pret; }
    public BeverageCategory getCategorie() { return categorie; }

    public void setCategorie(BeverageCategory categorie) {
        this.categorie = categorie;
    }

    public BeverageType getTip() { return tip; }

    public void setTip(BeverageType tip) {
        this.tip = tip;
    }
    public void setNume(String nume) { this.nume = nume; }
    public void setPret(double pret) { this.pret = pret; }

    @Override
    public String toString() {
        return nume + " (" + categorie + ", " + tip + ") - " + pret + " lei";
    }
}