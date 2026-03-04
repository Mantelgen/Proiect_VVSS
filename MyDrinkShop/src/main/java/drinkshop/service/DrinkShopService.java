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

    public void exportCsv(String path) {
        CsvExporter.exportOrders(productService.getAllProducts(), orderService.getAllOrders(), path);
    }

    // ---------- STOCK + RECIPE ----------
    public void comandaProdus(Product produs) {
        Recipe recipe = recipeService.findById(produs.getId());

        if (!stockService.areSuficient(recipe)) {
            throw new IllegalStateException("Stoc insuficient pentru produsul: " + produs.getNume());
        }
        stockService.consuma(recipe);
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