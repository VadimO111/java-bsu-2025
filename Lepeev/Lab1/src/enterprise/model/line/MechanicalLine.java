package enterprise.model.line;

import enterprise.model.product.MechanicalProduct;
import enterprise.model.product.Product;

import java.util.List;

public class MechanicalLine extends ProductionLine<MechanicalProduct> {
    public MechanicalLine(String lineId, List<MechanicalProduct> products, double efficiency) {
        super(lineId, products, efficiency);
    }

    public MechanicalLine(String lineId, double efficiency) {
        super(lineId, efficiency);
    }

    @Override
    public boolean canProduce(Product product) {
        return product instanceof MechanicalProduct;
    }
}