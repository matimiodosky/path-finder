import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class PathFinder<T> {

    private final Graph<T, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

    private final DijkstraShortestPath<T, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);

    private final Discoverer<T> discoverer;

    private final int frontLineMaxCount;

    private static final int FRONT_LINE_MAX_COUNT_DEFAULT = 100;

    public PathFinder(Discoverer<T> discover) {
        this.discoverer = discover;
        this.frontLineMaxCount = FRONT_LINE_MAX_COUNT_DEFAULT;
    }

    public PathFinder(Discoverer<T> discoverer, int frontLineMaxCount) {
        this.discoverer = discoverer;
        this.frontLineMaxCount = frontLineMaxCount;
    }

    public List<T> findPath(T source, T destination) throws NoSuchElementException {

        graph.addVertex(source);
        graph.addVertex(destination);

        GraphPath<T, DefaultEdge> path = dijkstraAlg.getPath(source, destination);
        List<T> frontLine = Collections.singletonList(source);

        while (path == null){
            if (frontLine.size() > this.frontLineMaxCount || frontLine.isEmpty()) throw new NoSuchElementException();
            List<Discovery<T>> discoveries = discoverer.discover(frontLine);

            frontLine = discoveries
                    .stream()
                    .map(Discovery::getTo)
                    .filter(location -> !graph.vertexSet().contains(location))
                    .collect(Collectors.toList());

            discoveries.forEach(discovery -> {
                graph.addVertex(discovery.getTo());
                graph.addEdge(discovery.getFrom(), discovery.getTo());
            });

            path = dijkstraAlg.getPath(source, destination);
        }

        return path.getVertexList();
    }




}
