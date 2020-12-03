import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PathFinderTest {

    private final Discoverer<String> discoverer = new Discoverer<>() {


        @Override
        protected List<Discovery<String>> discover(List<String> sources) {
            return sources
                    .stream()
                    .flatMap(this::discover)
                    .collect(Collectors.toList());
        }

        private Stream<Discovery<String>> discover(String source) {

                /*
                  a
                 / \
                d   b
                     \
                      c
                */

            return switch (source) {
                case "a" -> Stream.of(
                        new Discovery<>(source, "b"),
                        new Discovery<>(source, "d")
                );
                case "b" -> Stream.of(
                        new Discovery<>(source, "c")
                );
                default -> Stream.empty();
            };
        }
    };


    @Test
    public void test_001_find_path_if_source_is_same_as_destination() {
        PathFinder<String> pathFinder = new PathFinder<>(discoverer);
        List<String> path = pathFinder.findPath("a", "a");
        assertEquals(path, Collections.singletonList("a"));
    }

    @Test
    public void test_002_find_path_with_one_step() {
        PathFinder<String> pathFinder = new PathFinder<>(discoverer);
        List<String> path = pathFinder.findPath("a", "b");
        assertEquals(path, Arrays.asList("a", "b"));
    }

    @Test
    public void test_002_find_path_with_two_step() {
        PathFinder<String> pathFinder = new PathFinder<>(discoverer);
        List<String> path = pathFinder.findPath("a", "c");
        assertEquals(path, Arrays.asList("a", "b", "c"));
    }

    @Test
    public void test_003_non_existent_path() {
        assertThrows(NoSuchElementException.class, () -> {
            PathFinder<String> pathFinder = new PathFinder<>(discoverer);
            pathFinder.findPath("a", "x");
        });
    }
}
