package enterprise.main;

import enterprise.model.line.ChemicalLine;
import enterprise.model.line.ElectronicsLine;
import enterprise.model.line.MechanicalLine;
import enterprise.model.line.ProductionLine;
import enterprise.model.product.ChemicalProduct;
import enterprise.model.product.ElectronicsProduct;
import enterprise.model.product.MechanicalProduct;
import enterprise.model.product.Product;
import enterprise.service.ProdAnalyzer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        var analyzer = getProdAnalyzer();

        System.out.println("Анализ:");
        System.out.println("Линии с эффективностью > 0.88: " + analyzer.getHighEfficiencyLines(0.88));
        System.out.println("Количество игрушек по категориям: " + analyzer.countProductsByCategory());
        System.out.println("Все произведенные игрушки (ID): " + analyzer.getAllProductsFromLines().stream().map(Product::getId).collect(Collectors.joining(", ")));
        System.out.println("Общее время производства всех игрушек: " + analyzer.calculateTotalProductionTime() + " минут.");

        Optional<ProductionLine<? extends Product>> mostLoaded = analyzer.findMostLoadedLine();
        mostLoaded.ifPresent(line -> System.out.println("Самая загруженная линия: " + line.getLineId() +
                " с " + line.getProducts().size() + " игрушками."));
    }

    private static ProdAnalyzer getProdAnalyzer() {
        System.out.println("Создание игрушек");
        // Electronics
        var rcCar = new ElectronicsProduct("E_001", "Радиоуправляемая машина", 111);
        var robot = new ElectronicsProduct("E_002", "Робот", 170);
        var Console = new ElectronicsProduct("E_003", "Консоль", 212);

        // Mechanics
        var constructorSet = new MechanicalProduct("M_001", "Конструктор (1000 деталей)", 242);
        var windUpCar = new MechanicalProduct("M_002", "Заводная машинка", 137);
        var puzzleCube = new MechanicalProduct("M_003", "Кубик-рубик 3x3", 42);

        // Chemical
        var slimeKit = new ChemicalProduct("Ch_001", "Набор для создания слайма", 152);
        var crystalKit = new ChemicalProduct("Ch_002", "Набор для выращивания кристаллов", 197);
        var kineticSand = new ChemicalProduct("Ch_003", "Кинетический песок (1кг)", 142);

        System.out.println(rcCar);
        System.out.println(constructorSet);
        System.out.println(slimeKit);
        System.out.println();

        System.out.println("Создание линий");
        var elLine1 = new ElectronicsLine("EL_001", 0.85);
        var elLine2 = new ElectronicsLine("EL_002", 0.92);
        var meLine1 = new MechanicalLine("ML_001", 0.78);
        var meLine2 = new MechanicalLine("ML_002", 0.95);
        var chLine1 = new ChemicalLine("ChL_001", 0.88);

        System.out.println("Добавление игрушек");
        elLine1.addProduct(rcCar);
        elLine1.addProduct(Console);
        elLine2.addProduct(robot);
        System.out.println("Добавили электронику на линии.");

        meLine1.addProduct(puzzleCube);
        // Делаем вторую мех. линию наиболее загруженной
        meLine2.addProduct(constructorSet);
        meLine2.addProduct(windUpCar);
        meLine2.addProduct(puzzleCube);
        meLine2.addProduct(puzzleCube);
        System.out.println("Добавили механические игрушки на линии.");

        chLine1.addProduct(slimeKit);
        chLine1.addProduct(crystalKit);
        chLine1.addProduct(kineticSand);
        System.out.println("Добавили химические наборы на линию.");
        System.out.println();

        System.out.println("Тест canProduce");
        System.out.println("Линия EL_001 может производить радиоуправляемую машину? " + elLine1.canProduce(rcCar));       // true
        System.out.println("Линия EL_001 может производить конструктор? " + elLine1.canProduce(constructorSet));        // false
        System.out.println("Линия ML_001 может производить конструктор? " + meLine1.canProduce(puzzleCube));            // true
        System.out.println();

        // Попытка добавить несовместимый продукт вызовет ошибку компиляции
        // elLine1.addProduct(constructorSet);

        List<ProductionLine<? extends Product>> allProductionLines = Arrays.asList(
                elLine1, elLine2, meLine1, meLine2, chLine1
        );

        return new ProdAnalyzer(allProductionLines);
    }
}