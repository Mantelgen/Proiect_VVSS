package drinkshop.export;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.service.dep.ICsvExporter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CsvExporter implements ICsvExporter {
    private static final String CSV_HEADER = "OrderId,Product,Quantity,Price\n";
    private static final String CURRENCY_SUFFIX = " RON\n";
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String ORDER_SEPARATOR = "-------------------------------\n";
    private static final String ORDER_TOTAL_PREFIX = "total order: ";
    private static final String DAILY_TOTAL_PREFIX = "TOTAL OF ";
    private static final String DAILY_TOTAL_MIDDLE = " is: ";

    public void exportOrders(List<Product> products, List<Order> orders, String path) {
        try (FileWriter w = new FileWriter(path)) {
            w.write(CSV_HEADER);
            double sum = 0.0;
            for (Order o : orders) {
                for (OrderItem i : o.getItems()) {
                    Product p = products.stream().filter((p1) -> i.getProduct().getId() == p1.getId()).collect(Collectors.toList()).get(0);
                    w.write(o.getId() + "," + p.getNume() + "," + i.getQuantity() + "," + i.getTotal() + "\n");
                }
                w.write(ORDER_TOTAL_PREFIX + o.getTotalPrice() + CURRENCY_SUFFIX);
                w.write(ORDER_SEPARATOR);
                sum += o.getTotalPrice();
            }
            String date = LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            w.write(DAILY_TOTAL_PREFIX + date + DAILY_TOTAL_MIDDLE + sum + CURRENCY_SUFFIX);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}