package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Inventory {

//parts inventory
    private static ObservableList<Part> allParts = FXCollections.observableArrayList();

    public static void addPart(Part part) {
        allParts.add(part);
    }

    public static ObservableList<Part> getAllParts(){
        return allParts;
    }

//products inventory
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();

    public static void addProduct(Product product) {
        allProducts.add(product);
    }

    public static ObservableList<Product> getAllProducts(){
        return allProducts;
    }

    public static void removeProduct(Product product) {
        allProducts.remove(product);
    }

    static {
        addTestData();
    }

    public static int partIdCount = 6;
    public static int productIdCount = 4;

    public static void addTestData(){

        //parts
        Part wheel = new InHouse(1, "wheel", 10.24, 5, 1, 20, 1);
        Inventory.addPart(wheel);
        Part handlebar = new InHouse(2, "handlebar", 5.22, 7, 0, 30, 2);
        Inventory.addPart(handlebar);
        Part spoke = new InHouse(3, "spoke", 2.15, 50, 0, 150, 3);
        Inventory.addPart(spoke);
        Part reflectiveTape = new Outsourced(4, "reflective tape", 1.22, 24, 0, 120, "Bigelow");
        Inventory.addPart(reflectiveTape);
        Part pedal = new Outsourced(5, "pedal", 6.35, 25, 0, 160, "Huffy");
        Inventory.addPart(pedal);

        //products
        Product unicycle = new Product( 1, "unicycle", 150.55, 3, 1, 20 );
        Inventory.addProduct(unicycle);
        unicycle.addAssociatedPart(wheel);
        unicycle.addAssociatedPart(handlebar);
        unicycle.addAssociatedPart(pedal);
        Product tricycle = new Product( 2, "tricycle", 225.25, 7, 1, 20 );
        Inventory.addProduct(tricycle);
        tricycle.addAssociatedPart(wheel);
        tricycle.addAssociatedPart(spoke);
        tricycle.addAssociatedPart(pedal);
        Product bicycle = new Product( 3, "bicycle", 190.95, 12, 1, 20 );
        Inventory.addProduct(bicycle);
        bicycle.addAssociatedPart(reflectiveTape);
        bicycle.addAssociatedPart(handlebar);
        bicycle.addAssociatedPart(pedal);

    }
}
