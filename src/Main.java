import java.util.*;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        List<Office> officeBuildings = new ArrayList<>();
        officeBuildings.add(new Office(1000.0f, true, 10, 50));
        officeBuildings.add(new Office(800.0f, true, 8, 40));

        District<Office> officeDistrict = new District<>("Office District", officeBuildings);

        List<House> houseBuildings = new ArrayList<>();

        houseBuildings.add(new House(1200.0f, true, 20));
        houseBuildings.add(new House(1000.0f, true, 15));

        District<House> residentialDistrict = new District<>("Residential District", houseBuildings);

        ApplePark tmp = new ApplePark();
        tmp.taxesPaid = false;

        District<Office> distr = new District<Office>()
                .withAddress("Apple dr, Cupertino, Ca")
                .withBuilding(tmp);

        District<ApplePark> appleOffices = new District<ApplePark>()
                .withAddress("Apple dr, Cupertino, Ca")
                .withBuilding(new ApplePark());

        Optional<Float> medianFine = calculateMedianFine(distr);


        OfficeDistrict office = new OfficeDistrict()
                .withAddress("Mountain View, CA")
                .withCompany("Google")
                .withBuilding(new GooglePlex());
        //int tmp1 = getSummaryCOVIDCases(residentialDistrict);
        int tmp1 = getSummaryCOVIDCases(office);

        District<Office> officeDistrict1 = new District<Office>()
                .withAddress("Apple dr, Cupertino, Ca")
                .withBuilding(new ApplePark()).withBuilding(new ApplePark());
        WaterIncomeCalculator calculator = new WaterIncomeCalculator();
        officeDistrict1.processStatistics(Main::predictWaterConsumption, calculator::addSource);
        float result = calculator.getSummaryIncome();

        District<House> houseDistrict = new District<House>().
                withBuilding(new House(1, true, 1)).
                withBuilding(new House(1, true, 4));
        SummaryCalculator aggregator = new SummaryCalculator();
        houseDistrict.processStatistics(Main::extractHouseResidentCount, aggregator::add);
        float result1 = aggregator.getSum();

        District<Office> tmp2 = mergeDistricts(officeDistrict1, office);
        System.out.println(tmp2);
    }

    public static <T extends Building, R extends Building, Q extends District> Q mergeDistricts
            (District<T> d1, District<R> d2) {
        Q mergedDistrict = (Q) new District<>();
        mergedDistrict.withAddress(d1.address + " & " + d2.address);
        mergedDistrict.buildings.addAll(d1.buildings);
        mergedDistrict.buildings.addAll(d2.buildings);
        return mergedDistrict;
    }
    public static <T extends Building, R extends Building, Q extends District> Q mergeDistricts
            (District<T> d1, District<R> d2, Supplier<Q> sup) {
        Q mergedDistrict = sup.get();
        mergedDistrict.withAddress(d1.address + " & " + d2.address);
        mergedDistrict.buildings.addAll(d1.buildings);
        mergedDistrict.buildings.addAll(d2.buildings);
        return mergedDistrict;
    }

    static Float predictWaterConsumption(Office office) {
        return (float) office.employees * 45;
    }

    static float extractHouseResidentCount(House house) {
        return (float) house.flatCount * 2;
    }

    public static <T extends Building> Optional<Penalty> calculatePenalty(T building) {
        if (!building.taxesPaid) {
            return Optional.of(new Penalty((float) Math.random()));
        } else {
            return Optional.empty();
        }
    }

    public static <T extends Building> Optional<Float> calculateMedianFine(District<T> district) {
        List<Float> fines = new ArrayList<>();
        for (var elem : district.collectInfo(Main::calculatePenalty)) {
            fines.add(elem.getFineAmount());
        }
        if (fines.isEmpty()) {
            return Optional.empty();
        }
        Collections.sort(fines);
        int middle = fines.size() / 2;
        if (fines.size() % 2 == 0) {
            return Optional.of((fines.get(middle - 1) + fines.get(middle)) / 2.0f);
        } else {
            return Optional.of(fines.get(middle));
        }
    }

    public static <T extends Building & COVIDStatisticsProvider> int getSummaryCOVIDCases(District<T> district) {
        int res = 0;
        for (var elem : district.buildings) {
            res += elem.getCOVIDCases();
        }
        return res;
    }
}


class OfficeDistrict extends District<Office> {
    String company;

    public OfficeDistrict() {
    }

    @Override
    public OfficeDistrict withAddress(String address) {
        this.address = address;
        return this;
    }

    @Override
    public OfficeDistrict withBuilding(Office building) {
        this.buildings.add(building);
        return this;
    }

    public OfficeDistrict withCompany(String company) {
        this.company = company;
        return this;
    }
}

class ShopDistrict extends District<Shop> {
    String shop;

    public ShopDistrict withShop(String shop) {
        this.shop = shop;
        return this;
    }

    @Override
    public ShopDistrict withAddress(String address) {
        this.address = address;
        return this;
    }

    @Override
    public ShopDistrict withBuilding(Shop building) {
        this.buildings.add(building);
        return this;
    }
}

class GooglePlex extends Office {
    public GooglePlex() {
        super(100000, false, 500, 2000);
    }
}

class Office extends Building implements COVIDStatisticsProvider {
    int coffeeMachines;
    int employees;

    public Office(float area, boolean taxesPaid, int coffeeMachines, int employees) {
        this.area = area;
        this.taxesPaid = taxesPaid;
        this.coffeeMachines = coffeeMachines;
        this.employees = employees;
    }

    @Override
    public int getCOVIDCases() {
        return (int) (Math.random() * 10);
    }
}

class Shop extends Building {
    int dailyCustomers;

    public Shop(float area, boolean taxesPaid, int dailyCustomers) {
        this.area = area;
        this.taxesPaid = taxesPaid;
        this.dailyCustomers = dailyCustomers;
    }
}

class House extends Building {
    int flatCount;

    public House(float area, boolean taxesPaid, int flatCount) {
        this.area = area;
        this.taxesPaid = taxesPaid;
        this.flatCount = flatCount;
    }
}

class ApplePark extends Office {
    public ApplePark() {
        super(100000, true, 1000, 5000);
    }
}

class SummaryCalculator {
    private float sum = 0;

    void add(float value) {
        sum += value;
    }

    float getSum() {
        return sum;
    }
}

class WaterIncomeCalculator {
    private final float costOfLiter;
    private float summarySource = 0;

    public WaterIncomeCalculator(float costOfLiter) {
        this.costOfLiter = costOfLiter;
    }

    public WaterIncomeCalculator() {
        this.costOfLiter = 1;
    }

    void addSource(float value) {
        summarySource += value;
    }

    float getSummaryIncome() {
        return summarySource * costOfLiter;
    }
}
