module drinkshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;


    opens drinkshop.ui to javafx.fxml;
    exports drinkshop.ui;

    opens drinkshop.domain to  javafx.base;
    exports drinkshop.domain;
    exports drinkshop.service;
    exports drinkshop.repository;
}