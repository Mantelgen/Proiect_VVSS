package drinkshop.receipt;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptGenerator {
    public static String generate(Order o, List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BON FISCAL =====\n").append("Comanda #").append(o.getId()).append("\n");
        for (OrderItem i : o.getItems()) {
            Product p = products.stream().filter((p1)->i.getProduct().getId()==p1.getId()).toList().get(0);
            sb.append(p.getNume()+": ").append(p.getPret()).append(" x ").append(i.getQuantity()).append(" = ").append(i.getTotal()).append(" RON\n");
        }
        sb.append("---------------------\nTOTAL: ").append(o.getTotalPrice()).append(" RON\n=====================\n");
        return sb.toString();
    }

    public static void saveAsCsv(Order o, List<Product> products, String path) {
        try (FileWriter w = new FileWriter(path)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            w.write("Bon fiscal - Comanda #" + o.getId() + " - " + timestamp + "\n");
            w.write("Produs,Cantitate,Pret unitar,Total\n");
            for (OrderItem i : o.getItems()) {
                Product p = products.stream()
                        .filter(pr -> pr.getId() == i.getProduct().getId())
                        .findFirst().orElse(null);
                if (p != null) {
                    w.write(p.getNume() + "," + i.getQuantity() + "," + p.getPret() + "," + i.getTotal() + "\n");
                }
            }
            w.write("TOTAL,,," + o.getTotalPrice() + " RON\n");
        } catch (IOException e) {
            throw new RuntimeException("Eroare la salvarea bonului CSV: " + e.getMessage(), e);
        }
    }
}