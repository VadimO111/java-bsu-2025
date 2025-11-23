package enterprise.model.line;

import enterprise.model.product.ElectronicsProduct;
import enterprise.model.product.Product;

import java.util.List;

public class ElectronicsLine extends ProductionLine<ElectronicsProduct> {
    public ElectronicsLine(String lineId, List<ElectronicsProduct> products, double efficiency) {
        super(lineId, products, efficiency);
    }

    public ElectronicsLine(String lineId, double efficiency) {
        super(lineId, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof ElectronicsProduct;
    }
}