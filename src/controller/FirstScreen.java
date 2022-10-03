package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Inventory;
import model.Part;
import model.Product;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.awt.SystemColor.text;


public class FirstScreen implements Initializable
{
        //parts table
        public TableView allParts;
        public TableColumn allPartIDs;
        public TableColumn allPartNames;
        public TableColumn allPartInv;
        public TableColumn allPartPrices;

        //products table
        public TableView allProducts;
        public TableColumn allProductIDs;
        public TableColumn allProductNames;
        public TableColumn allProductInv;
        public TableColumn allProductPrices;

        //buttons
        public Button addPart;
        public Button updatePart;
        public Button deletePart;
        public Button addProduct;
        public Button updateProduct;
        public Button deleteProduct;
        public Button exitButton;

        //text fields
        public TextField partsQuery;
        public TextField productsQuery;

        //error label
        public Label errorLabel;

        //modifying parts/products and indices
        private static Part modifyPart;
        private static Product modifyingProduct;
        private static int modifyPartIndex;
        private static int modifyProductIndex;

    //methods to pass modifying indices to other controllers
        public static int partModIndex() {
            return modifyPartIndex;
        }
        public static int productModIndex() {
            return modifyProductIndex;
        }

        public static Part partToMod() {
            return modifyPart;
        }

        public static Product productToMod() {
            return modifyingProduct;
        }


        @Override
        public void initialize (URL location, ResourceBundle resources) {
            //reset modifying part and product
            modifyPart = null;
            modifyingProduct = null;


            //populate tables
            allParts.setItems(Inventory.getAllParts());
            allProducts.setItems(Inventory.getAllProducts());

            allPartIDs.setCellValueFactory(new PropertyValueFactory<>("Id"));
            allPartNames.setCellValueFactory(new PropertyValueFactory<>("name"));
            allPartInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
            allPartPrices.setCellValueFactory(new PropertyValueFactory<>("price"));

            allProductIDs.setCellValueFactory(new PropertyValueFactory<>("id"));
            allProductNames.setCellValueFactory(new PropertyValueFactory<>("name"));
            allProductInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
            allProductPrices.setCellValueFactory(new PropertyValueFactory<>("price"));
        }


    public void onAddPart(ActionEvent actionEvent) throws IOException {
            //open part scene
            Parent root = FXMLLoader.load(getClass().getResource("/view/PartForm.fxml"));
            root.setStyle("-fx-font-family: 'Times New Roman'");
            Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Add Part");
            stage.setScene(scene);
            stage.show();
    }


    public void onUpdatePart(ActionEvent actionEvent) throws IOException {
        modifyPart = (Part) allParts.getSelectionModel().getSelectedItem();
        modifyPartIndex = Inventory.getAllParts().indexOf(modifyPart);
            //open part scene
        //pass in selected part id
        Parent root = FXMLLoader.load(getClass().getResource("/view/PartForm.fxml"));
        root.setStyle("-fx-font-family: 'Times New Roman'");
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Update Part");
        stage.setScene(scene);
        stage.show();
    }


    public void onDeletePart(ActionEvent actionEvent) {
            //clear error
        errorLabel.setText("");
            // select part
        Part part = (Part) allParts.getSelectionModel().getSelectedItem();
        //verify delete
        boolean toDelete = verifyDelete(part.getName());
        if (toDelete == false ){return;}

        // loop through until part is selected
        for (int i = 0; i < Inventory.getAllParts().size(); i++) {
            Part parts = Inventory.getAllParts().get(i);
            //if match
            if (parts.getId() == part.getId()) {
                //delete and leave loop
                Inventory.getAllParts().remove(i);
                break;
            }
        }

    }


    public void onAddProduct(ActionEvent actionEvent) throws IOException {
            //go to product scene
        Parent root = FXMLLoader.load(getClass().getResource("/view/ProductForm.fxml"));
        root.setStyle("-fx-font-family: 'Times New Roman'");
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Add Product");
        stage.setScene(scene);
        stage.show();
    }


    public void onUpdateProduct(ActionEvent actionEvent) throws IOException {
            //go to product scene & pass in index of product selected
        modifyingProduct = (Product) allProducts.getSelectionModel().getSelectedItem();
        modifyProductIndex = Inventory.getAllProducts().indexOf(modifyingProduct);
        Parent modifyProducts = FXMLLoader.load(getClass().getResource("/view/ProductForm.fxml"));
        modifyProducts.setStyle("-fx-font-family: 'Times New Roman'");
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(modifyProducts);
        stage.setTitle("Update Product");
        stage.setScene(scene);
        stage.show();
    }


    public void onDeleteProduct(ActionEvent actionEvent) {
        //clear error
        errorLabel.setText("");
        //get selected product
        Product productX = (Product) allProducts.getSelectionModel().getSelectedItem();
        //verify delete
        boolean toDelete = verifyDelete(productX.getName());
        if (toDelete == false ){return;}

        //if it has parts
        ObservableList<Part> productParts = productX.getAllAssociatedParts();
        if (!productParts.isEmpty()){
            //show error and return
            errorLabel.setText("Error: cannot delete a product with associated parts.");
            return;
        }

        //loop through until ids match
        for (int i = 0; i < Inventory.getAllProducts().size(); i++) {
            Product products = Inventory.getAllProducts().get(i);
            if (products.getId() == productX.getId()) {
                //delete and end loop
                Inventory.getAllProducts().remove(i);
                break;
            }
        }
    }


    public void onExitButton(ActionEvent actionEvent) {
            Platform.exit();
    }


    public static boolean isInt( String textInput )
    {
        try
        {
            Integer.parseInt( textInput );
            return true;
        }
        catch( Exception e)
        {
            return false;
        }
    }


    public void productResults (KeyEvent actionEvent){
        String q = productsQuery.getText();
        ObservableList<Product> productsSearch = allProducts.getItems();
        //if int then search by ID
        if (isInt(q) == true) {
            productsSearch = searchByProductNum(q);
        } else {
            productsSearch = searchByProductName(q);
        }
        allProducts.setItems(productsSearch);
        //if empty show message
        allProducts.setPlaceholder(new Label("Error: Product not found"));
    }


    private ObservableList<Product> searchByProductNum(String partialProductId) {
        String idString;
        ObservableList<Product> productIds = FXCollections.observableArrayList();
        ObservableList<Product> allProducts = Inventory.getAllProducts();

        for(Product p1: allProducts ) {
            //convert ids to string
            idString = String.valueOf(p1.getId());
            if (idString.contains(partialProductId)){
                productIds.add(p1);
            }
        }

        return productIds;
    }


    private ObservableList<Product> searchByProductName(String partialProductName) {
        ObservableList<Product> productNames = FXCollections.observableArrayList();
        ObservableList<Product> allProducts = Inventory.getAllProducts();

        for(Product p1: allProducts ) {
            if (p1.getName().contains(partialProductName)){
                productNames.add(p1);
            }
        }

        return productNames;
    }



    public void partResults (KeyEvent actionEvent){
        String q = partsQuery.getText();
        ObservableList<Part> partsSearch = allParts.getItems();
        //if int then search by ID
        if (isInt(q) == true) {
            partsSearch = searchByPartNum(q);
            // else search by part name
            } else {
            partsSearch = searchByPartName(q);
            }
            //display results
            allParts.setItems(partsSearch);
        //if empty show message
        allParts.setPlaceholder(new Label("Error: Part not found"));
    }

    public static ObservableList<Part> searchByPartNum(String partialPartId) {
            String idString;
        ObservableList<Part> partIds = FXCollections.observableArrayList();
        ObservableList<Part> allParts = Inventory.getAllParts();

        for(Part p1: allParts ) {
            //convert ids to string
            idString = String.valueOf(p1.getId());
            if (idString.contains(partialPartId)){
                partIds.add(p1);
            }
        }

        return partIds;
    }

    public static ObservableList<Part> searchByPartName(String partialPartName) {
       ObservableList<Part> partNames = FXCollections.observableArrayList();
       ObservableList<Part> allParts = Inventory.getAllParts();

       for(Part p1: allParts ) {
           if (p1.getName().contains(partialPartName)){
               partNames.add(p1);
            }
       }

       return partNames;
    }

    public static boolean verifyDelete(String objName){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().getScene().getRoot().setStyle("-fx-font-family: 'Times New Roman'");
        alert.setTitle("Confirmation");
        alert.setHeaderText("Verify Delete");
        alert.setContentText("Are you sure you would like to delete " + objName + "?");

        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            return true;
        } else {
            result.get();
        }
        {
            return false;
        }
    }
}

