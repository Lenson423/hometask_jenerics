import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class District<T extends Building> {
    String address;
    List<T> buildings;

    public District(String address, List<T> buildings) {
        this.address = address;
        this.buildings = buildings;
    }

    public District() {
        this.buildings = new ArrayList<>();
    }

    public District<T> withAddress(String address) {
        this.address = address;
        return this;
    }

    public District<T> withBuilding(T building) {
        this.buildings.add(building);
        return this;
    }

    public List<Penalty> collectInfo(Function<Building, Optional<Penalty>> calculator) {
        List<Penalty> results = new ArrayList<>();
        for (Building building : buildings) {
            Optional<Penalty> result = calculator.apply(building);
            result.ifPresent(results::add);
        }
        return results;
    }

    public <R> void processStatistics(Function<T, R> extractor, Consumer<R> consumer) {
        for (T building : buildings) {
            R info = extractor.apply(building);
            consumer.accept(info);
        }
    }
}
