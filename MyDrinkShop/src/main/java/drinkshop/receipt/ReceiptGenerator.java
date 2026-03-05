package drinkshop.receipt;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReceiptGenerator {
    public static String generate(Order o, List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BON FISCAL =====\n").append("Comanda #").append(o.getId()).append("\n");
        for (OrderItem i : o.getItems()) {
            Product p = products.stream().filter((p1)->i.getProduct().getId()==p1.getId()).findFirst().orElse(null);
            if (p == null) continue;
            sb.append(p.getNume()+": ").append(p.getPret()).append(" x ").append(i.getQuantity()).append(" = ").append(i.getTotal()).append(" RON\n");
        }
        sb.append("---------------------\nTOTAL: ").append(o.getTotalPrice()).append(" RON\n=====================\n");
        return sb.toString();
    }

    /**
     * Saves the receipt of an order as a CSV file.
     * Called automatically when an order is finalized (Req 7).
     */
    public static void saveAsCsv(Order o, List<Product> products, String path) {
        try (FileWriter w = new FileWriter(path)) {
            w.write("OrderId,Product,Quantity,UnitPrice,ItemTotal\n");
            for (OrderItem i : o.getItems()) {
                Product p = products.stream().filter((p1)->i.getProduct().getId()==p1.getId()).findFirst().orElse(null);
                if (p == null) continue;
                w.write(o.getId() + "," + p.getNume() + "," + i.getQuantity() + "," + p.getPret() + "," + i.getTotal() + "\n");
            }
            w.write(",,,TOTAL," + o.getTotalPrice() + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Could not save receipt CSV: " + e.getMessage(), e);
        }
    }
}