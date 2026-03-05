package drinkshop.reports;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.repository.Repository;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DailyReportService {
    private Repository<Integer, Order> repo;

    public DailyReportService(Repository<Integer, Order> repo) {
        this.repo = repo;
    }

    public double getTotalRevenue() {
        return repo.findAll().stream().mapToDouble(Order::getTotalPrice).sum();
    }

    public int getTotalOrders() {
        return repo.findAll().size();
    }

    public void exportDailySummary(String path) {
        List<Order> orders = repo.findAll();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        try (FileWriter w = new FileWriter(path)) {
            w.write("Raport zilnic - " + date + "\n");
            w.write("OrderId,Produse,Total (RON)\n");
            for (Order o : orders) {
                String items = o.getItems().stream()
                        .map(i -> i.getProduct().getId() + "x" + i.getQuantity())
                        .collect(Collectors.joining("|"));
                w.write(o.getId() + ",\"" + items + "\"," + o.getTotalPrice() + "\n");
            }
            w.write("\nTOTAL INCASARI,," + getTotalRevenue() + " RON\n");
            w.write("NR. COMENZI,," + getTotalOrders() + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Eroare la exportul raportului zilnic: " + e.getMessage(), e);
        }
    }
}