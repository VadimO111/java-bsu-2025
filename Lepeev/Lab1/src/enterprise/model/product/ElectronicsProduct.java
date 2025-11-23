package enterprise.model.product;

public class ElectronicsProduct extends Product {
    public ElectronicsProduct(String id, String name, int productionTime) {
        super(id, name, productionTime);
    }

    @Override
    public String getCategory() {
        return "Electronics";
    }
}