import java.util.List;

public abstract class Discoverer<T> {

    protected abstract List<Discovery<T>> discover(List<T> sources);

}
