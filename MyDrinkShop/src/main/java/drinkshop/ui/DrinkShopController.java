package drinkshop.ui;

import drinkshop.domain.*;
import drinkshop.service.DrinkShopService;
import drinkshop.service.validator.ValidationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DrinkShopController {

    private DrinkShopService service;

    // ---------- PRODUCT ----------
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colProdId;
    @FXML private TableColumn<Product, String> colProdName;
    @FXML private TableColumn<Product, Double> colProdPrice;
    @FXML private TableColumn<Product, String> colProdCategorie;
    @FXML private TableColumn<Product, String> colProdTip;
    @FXML private TextField txtProdName, txtProdPrice;
    @FXML private TextField prodCategorie;
    @FXML private TextField prodTip;

    // ---------- RETETE ----------
    @FXML private TableView<Recipe> retetaTable;
    @FXML private TableColumn<Recipe, Integer> colRetetaId;
    @FXML private TableColumn<Recipe, String> colRetetaDesc;

    @FXML private TableView<RecipeIngredient> newRetetaTable;
    @FXML private TableColumn<RecipeIngredient, String> colNewIngredName;
    @FXML private TableColumn<RecipeIngredient, Double> colNewIngredCant;
    @FXML private TextField txtNewIngredName, txtNewIngredCant;

    // ---------- STOCURI ----------
    @FXML private TableView<Stock> stockTable;
    @FXML private TableColumn<Stock, Integer> colStockId;
    @FXML private TableColumn<Stock, String> colStockIngredient;
    @FXML private TableColumn<Stock, Double> colStockCantitate;
    @FXML private TableColumn<Stock, Double> colStockMinim;
    @FXML private TextField txtStockIngredient, txtStockCantitate, txtStockMinim;
    @FXML private Label lblStockWarning;

    // ---------- ORDER (CURRENT) ----------
    @FXML private TableView<OrderItem> currentOrderTable;
    @FXML private TableColumn<OrderItem, String> colOrderProdName;
    @FXML private TableColumn<OrderItem, Integer> colOrderQty;

    @FXML private ComboBox<Integer> comboQty;
    @FXML private Label lblOrderTotal;
    @FXML private TextArea txtReceipt;

    @FXML private Label lblTotalRevenue;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final ObservableList<Recipe> recipeList = FXCollections.observableArrayList();
    private final ObservableList<RecipeIngredient> newRecipeIngredientList = FXCollections.observableArrayList();
    private final ObservableList<OrderItem> currentOrderItems = FXCollections.observableArrayList();
    private final ObservableList<Stock> stockList = FXCollections.observableArrayList();

    private Order currentOrder = new Order(1);

    public void setService(DrinkShopService service) {
        this.service = service;
        initData();
    }

    @FXML
    private void initialize() {

        // PRODUCTS
        colProdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProdName.setCellValueFactory(new PropertyValueFactory<>("nume"));
        colProdPrice.setCellValueFactory(new PropertyValueFactory<>("pret"));
        colProdCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colProdTip.setCellValueFactory(new PropertyValueFactory<>("tip"));
        productTable.setItems(productList);

        // RETETE
        colRetetaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colRetetaDesc.setCellValueFactory(data -> {
            Recipe r = data.getValue();
            String desc = r.getIngrediente().stream()
                    .map(i -> i.getDenumire() + " (" + i.getCantitate() + ")")
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(desc);
        });
        retetaTable.setItems(recipeList);

        colNewIngredName.setCellValueFactory(new PropertyValueFactory<>("denumire"));
        colNewIngredCant.setCellValueFactory(new PropertyValueFactory<>("cantitate"));
        newRetetaTable.setItems(newRecipeIngredientList);

        // CURRENT ORDER TABLE
        colOrderProdName.setCellValueFactory(data -> {
            int prodId = data.getValue().getProduct().getId();
            Product p = productList.stream().filter(pr -> pr.getId() == prodId).findFirst().orElse(null);
            return new SimpleStringProperty(p != null ? p.getNume() : "N/A");
        });
        colOrderQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        currentOrderTable.setItems(currentOrderItems);

        comboQty.setItems(FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10));

        // STOCURI
        colStockId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStockIngredient.setCellValueFactory(new PropertyValueFactory<>("ingredient"));
        colStockCantitate.setCellValueFactory(new PropertyValueFactory<>("cantitate"));
        colStockMinim.setCellValueFactory(new PropertyValueFactory<>("stocMinim"));
        stockTable.setItems(stockList);

        // Populate stock fields when row selected
        stockTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                txtStockIngredient.setText(sel.getIngredient());
                txtStockCantitate.setText(String.valueOf(sel.getCantitate()));
                txtStockMinim.setText(String.valueOf(sel.getStocMinim()));
            }
        });
    }

    private void initData() {
        productList.setAll(service.getAllProducts());
        recipeList.setAll(service.getAllRetete());
        refreshStocks();
        lblTotalRevenue.setText("Daily Revenue: " + service.getDailyRevenue() + " RON");
        updateOrderTotal();
    }

    private void refreshStocks() {
        stockList.setAll(service.getAllStocuri());
        long subMinim = stockList.stream().filter(Stock::isSubMinim).count();
        if (lblStockWarning != null) {
            lblStockWarning.setText(subMinim > 0 ? "⚠ " + subMinim + " ingrediente sub stoc minim!" : "");
        }
    }

    // ---------- PRODUCT ----------
    @FXML
    private void onAddProduct() {
        Recipe r=retetaTable.getSelectionModel().getSelectedItem();

        if (r == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Selectati o reteta pentru care adugati un produs");
            alert.showAndWait();
            return;
        }else
        if (service.getAllProducts().stream().anyMatch(p -> p.getId() == r.getId())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Exista un produs cu reteta adaugata.");
            alert.showAndWait();
            return;
        }
        Product p = new Product(r.getId(),
                txtProdName.getText(),
                Double.parseDouble(txtProdPrice.getText()),
                prodCategorie.getText(),
                prodTip.getText()
                );
        try {
            service.addProduct(p);
        } catch (Exception e) {
            showError(e.getMessage());
            return;
        }
        initData();
    }

    @FXML
    private void onUpdateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            service.updateProduct(selected.getId(), txtProdName.getText(),
                    Double.parseDouble(txtProdPrice.getText()),
                    prodCategorie.getText(), prodTip.getText());
        } catch (Exception e) {
            showError(e.getMessage());
            return;
        }
        initData();
    }

    @FXML
    private void onDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        service.deleteProduct(selected.getId());
        initData();
    }

    @FXML
    private void onFilterCategorie() {
        productList.setAll(service.filtreazaDupaCategorie(prodCategorie.getText()));
    }

    @FXML
    private void onFilterTip() {
        productList.setAll(service.filtreazaDupaTip(prodTip.getText()));
    }

    // ---------- RETETA NOUA ----------
    @FXML
    private void onAddNewIngred() {
        newRecipeIngredientList.add(new RecipeIngredient(txtNewIngredName.getText(),
                Double.parseDouble(txtNewIngredCant.getText())));
    }

    @FXML
    private void onDeleteNewIngred() {
        RecipeIngredient sel = newRetetaTable.getSelectionModel().getSelectedItem();
        if (sel != null) newRecipeIngredientList.remove(sel);
    }

    @FXML
    private void onAddNewReteta() {
        Recipe r = new Recipe(service.getAllRetete().size()+1, new ArrayList<>(newRecipeIngredientList));
        try {
            service.addReteta(r);
        } catch (ValidationException e) {
            showError(e.getMessage());
            return;
        }
        newRecipeIngredientList.clear();
        initData();
    }

    @FXML
    private void onClearNewRetetaIngredients() {
        newRetetaTable.getItems().clear();
        txtNewIngredName.clear();
        txtNewIngredCant.clear();
    }

    @FXML
    private void onDeleteReteta() {
        Recipe sel = retetaTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Selectează o rețetă pentru a o șterge.");
            return;
        }
        service.deleteReteta(sel.getId());
        initData();
    }

    // ---------- CURRENT ORDER ----------
    @FXML
    private void onAddOrderItem() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        Integer qty = comboQty.getValue();

        if (selected == null) {
            showError("Selectează un produs din listă.");
            return;
        }
        if (qty == null) {
            showError("Selectează cantitatea.");
            return;
        }

        currentOrderItems.add(new OrderItem(selected, qty));
        updateOrderTotal();
    }

    @FXML
    private void onDeleteOrderItem() {
        OrderItem sel = currentOrderTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            currentOrderItems.remove(sel);
            updateOrderTotal();
        }
    }

    @FXML
    private void onFinalizeOrder() {
        if (currentOrderItems.isEmpty()) {
            showError("Comanda este goală. Adaugă cel puțin un produs.");
            return;
        }
        currentOrder.setItems(currentOrderItems);

        try {
            String receipt = service.finalizeOrder(currentOrder);
            txtReceipt.setText(receipt);
        } catch (Exception e) {
            showError(e.getMessage());
            return;
        }

        currentOrderItems.clear();
        currentOrder = new Order(currentOrder.getId() + 1);
        updateOrderTotal();
        refreshStocks();
        lblTotalRevenue.setText("Daily Revenue: " + service.getDailyRevenue() + " RON");
    }

    private void updateOrderTotal() {
        currentOrder.setItems(currentOrderItems);
        double total = service.computeTotal(currentOrder);
        lblOrderTotal.setText("Total: " + total);
    }

    // ---------- EXPORT + REVENUE ----------
    @FXML
    private void onExportOrdersCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Salvează export CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        fc.setInitialFileName("orders.csv");
        File file = fc.showSaveDialog(null);
        if (file != null) service.exportCsv(file.getAbsolutePath());
    }

    @FXML
    private void onExportDailySummary() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Salvează raport zilnic");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files", "*.csv"));
        fc.setInitialFileName("daily_report.csv");
        File file = fc.showSaveDialog(null);
        if (file != null) {
            service.exportDailySummary(file.getAbsolutePath());
        }
    }

    @FXML
    private void onDailyRevenue() {
        lblTotalRevenue.setText("Daily Revenue: " + service.getDailyRevenue() + " RON");
    }

    // ---------- STOCURI ----------
    @FXML
    private void onAddStock() {
        try {
            String ingredient = txtStockIngredient.getText();
            double cantitate = Double.parseDouble(txtStockCantitate.getText());
            double minim = Double.parseDouble(txtStockMinim.getText());
            int nextId = stockList.stream().mapToInt(Stock::getId).max().orElse(0) + 1;
            Stock s = new Stock(nextId, ingredient, cantitate, minim);
            service.addStock(s);
            refreshStocks();
            clearStockFields();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (NumberFormatException e) {
            showError("Cantitate și stoc minim trebuie să fie numere valide.");
        }
    }

    @FXML
    private void onUpdateStock() {
        Stock selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Selectează un ingredient din tabel."); return; }
        try {
            Stock updated = new Stock(
                    selected.getId(),
                    txtStockIngredient.getText(),
                    Double.parseDouble(txtStockCantitate.getText()),
                    Double.parseDouble(txtStockMinim.getText()));
            service.updateStock(updated);
            refreshStocks();
            clearStockFields();
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (NumberFormatException e) {
            showError("Cantitate și stoc minim trebuie să fie numere valide.");
        }
    }

    @FXML
    private void onDeleteStock() {
        Stock selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Selectează un ingredient din tabel."); return; }
        service.deleteStock(selected.getId());
        refreshStocks();
        clearStockFields();
    }

    private void clearStockFields() {
        txtStockIngredient.clear();
        txtStockCantitate.clear();
        txtStockMinim.clear();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}