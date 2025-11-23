package enterprise.model.line;

import enterprise.model.product.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ProductionLine<T extends Product> {
    private final String lineId;
    private final List<T> products;
    private final double efficiency;

    protected ProductionLine(String lineId, List<T> products, double efficiency) {
        this.lineId = lineId;
        this.products = products;
        this.efficiency = efficiency;
    }

    protected ProductionLine(String lineId, double efficiency) {
        this.lineId = lineId;
        this.products = new ArrayList<>();
        this.efficiency = efficiency;
    }

    public String getLineId() {
        return lineId;
    }

    public List<T> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public double getEfficiency() {
        return efficiency;
    }

    public abstract boolean canProduce(Product product);

    public void addProduct(T product) {
        products.add(product);
    }

    @Override
    public String toString() {
        return "ProductionLine{" +
                "lineId='" + lineId + '\'' +
                ", efficiency=" + efficiency +
                ", productCount=" + products.size() +
                '}';
    }
}