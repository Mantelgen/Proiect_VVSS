package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.export.CsvExporter;
import drinkshop.receipt.ReceiptGenerator;
import drinkshop.reports.DailyReportService;
import drinkshop.repository.Repository;

import java.util.List;

public class DrinkShopService {

    private final ProductService productService;
    private final OrderService orderService;
    private final RecipeService recipeService;
    private final StockService stockService;
    private final DailyReportService report;

    public DrinkShopService(
            Repository<Integer, Product> productRepo,
            Repository<Integer, Order> orderRepo,
            Repository<Integer, Recipe> retetaRepo,
            Repository<Integer, Stock> stockRepo
    ) {
        this.productService = new ProductService(productRepo);
        this.orderService = new OrderService(orderRepo, productRepo);
        this.recipeService = new RecipeService(retetaRepo);
        this.stockService = new StockService(stockRepo);
        this.report = new DailyReportService(orderRepo);
    }

    // ---------- PRODUCT ----------
    public void addProduct(Product p) {
        productService.addProduct(p);
    }

    public void updateProduct(int id, String name, double price, BeverageCategory categorie, BeverageType tip) {
        productService.updateProduct(id, name, price, categorie, tip);
    }

    public void deleteProduct(int id) {
        productService.deleteProduct(id);
    }

    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    public List<Product> filtreazaDupaCategorie(BeverageCategory categorie) {
        return productService.filterByCategorie(categorie);
    }

    public List<Product> filtreazaDupaTip(BeverageType tip) {
        return productService.filterByTip(tip);
    }

    // ---------- ORDER ----------
    public void addOrder(Order o) {
        orderService.addOrder(o);
    }

    /**
     * Finalizes an order: saves it, deducts stock for each item (Req 7),
     * and saves the receipt as a CSV file.
     */
    public String finalizeOrder(Order o) {
        // Save order
        orderService.addOrder(o);

        // Deduct stock for every item in the order (Req 7)
        for (OrderItem item : o.getItems()) {
            Recipe recipe = recipeService.findById(item.getProduct().getId());
            if (recipe != null) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    if (stockService.areSuficient(recipe)) {
                        stockService.consuma(recipe);
                    }
                }
            }
        }

        // Generate receipt text
        String receipt = ReceiptGenerator.generate(o, productService.getAllProducts());

        // Save receipt as CSV (Req 7)
        String receiptPath = "receipt_order_" + o.getId() + ".csv";
        ReceiptGenerator.saveAsCsv(o, productService.getAllProducts(), receiptPath);

        return receipt;
    }

    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    public double computeTotal(Order o) {
        return orderService.computeTotal(o);
    }

    public String generateReceipt(Order o) {
        return ReceiptGenerator.generate(o, productService.getAllProducts());
    }

    public double getDailyRevenue() {
        return report.getTotalRevenue();
    }

    public int getTotalOrders() {
        return report.getTotalOrders();
    }

    public void exportCsv(String path) {
        CsvExporter.exportOrders(productService.getAllProducts(), orderService.getAllOrders(), path);
    }

    /**
     * Exports daily summary CSV (Req 8) – triggered manually by user.
     */
    public void exportDailySummary(String path) {
        report.exportDailySummary(path);
    }

    // ---------- STOCK + RECIPE ----------
    public void comandaProdus(Product produs) {
        Recipe recipe = recipeService.findById(produs.getId());
        if (recipe == null) return;

        if (!stockService.areSuficient(recipe)) {
            throw new IllegalStateException("Stoc insuficient pentru produsul: " + produs.getNume());
        }
        stockService.consuma(recipe);
    }

    public List<Stock> getAllStocuri() {
        return stockService.getAll();
    }

    public void addStock(Stock s) {
        stockService.add(s);
    }

    public void updateStock(Stock s) {
        stockService.update(s);
    }

    public void deleteStock(int id) {
        stockService.delete(id);
    }

    public List<Recipe> getAllRetete() {
        return recipeService.getAll();
    }

    public void addReteta(Recipe r) {
        recipeService.addReteta(r);
    }

    public void updateReteta(Recipe r) {
        recipeService.updateReteta(r);
    }

    public void deleteReteta(int id) {
        recipeService.deleteReteta(id);
    }
}

