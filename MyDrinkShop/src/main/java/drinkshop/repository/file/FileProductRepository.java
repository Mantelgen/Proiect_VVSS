package drinkshop.repository.file;

import drinkshop.domain.Product;
import drinkshop.domain.BeverageCategory;
import drinkshop.domain.BeverageType;

public class FileProductRepository
        extends FileAbstractRepository<Integer, Product> {

    public FileProductRepository(String fileName) {
        super(fileName);
        loadFromFile();
    }

    @Override
    protected Integer getId(Product entity) {
        return entity.getId();
    }

    @Override
    protected Product extractEntity(String line) {

        String[] elems = line.split(",");

        int id = Integer.parseInt(elems[0]);
        String name = elems[1];
        double price = Double.parseDouble(elems[2]);
        BeverageCategory categorie = BeverageCategory.valueOf(elems[3]);
        BeverageType tip = BeverageType.valueOf(elems[4]);

        return new Product(id, name, price, categorie, tip);
    }

    @Override
    protected String createEntityAsString(Product entity) {
        return entity.getId() + "," +
                entity.getNume() + "," +
                entity.getPret() + "," +
                entity.getCategorie() + "," +
                entity.getTip();
    }
}