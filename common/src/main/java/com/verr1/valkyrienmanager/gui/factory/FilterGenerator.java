package com.verr1.valkyrienmanager.gui.factory;


import com.verr1.valkyrienmanager.foundation.data.VOwnerData;
import com.verr1.valkyrienmanager.foundation.data.VTag;
import com.verr1.valkyrienmanager.manager.db.general.item.NetworkKey;
import com.verr1.valkyrienmanager.manager.db.general.item.VItem;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.verr1.valkyrienmanager.util.StringUtil.calculateSimilarity;

/**
 * The `FilterGenerator` class provides utilities for filtering, sorting, grouping,
 * and evaluating collections of `VItem` objects based on various contexts and policies.
 */
public class FilterGenerator {

    /**
     * Enum representing different matching contexts for filtering items.
     */
    public enum MatchContext {
        ALL,          // Match all elements in the collection.
        PARTIAL,      // Match any element in the collection.
        SUB_PARTIAL;  // Match elements containing the target substring.

        /**
         * Creates a filter predicate based on the matching context.
         *
         * @param mapping A function to map entries to collections of strings.
         * @param toMatch The collection of strings to match against.
         * @return A predicate for filtering entries.
         */
        Predicate<Map.Entry<Long, VItem>> filter(
                Function<Map.Entry<Long, VItem>, Collection<String>> mapping,
                Collection<String> toMatch
        ) {
            if (toMatch.isEmpty() || toMatch.stream().allMatch(String::isEmpty)) return e -> true;

            return entry -> {
                Collection<String> value = mapping.apply(entry);
                if (value.isEmpty() || value.stream().allMatch(String::isEmpty)) return false;
                return switch (this) {
                    case ALL -> value.stream().allMatch(v -> toMatch.stream().anyMatch(v::equalsIgnoreCase));
                    case PARTIAL -> value.stream().anyMatch(v -> toMatch.stream().anyMatch(v::equalsIgnoreCase));
                    case SUB_PARTIAL -> value.stream().anyMatch(v -> toMatch.stream().anyMatch(v::contains));
                };
            };
        }

        /**
         * Creates an evaluator function to calculate similarity scores based on the matching context.
         *
         * @param mapping A function to map entries to collections of strings.
         * @param toMatch The collection of strings to match against.
         * @return A function to evaluate similarity scores for entries.
         */
        Function<Map.Entry<Long, VItem>, Double> evaluator(
                Function<Map.Entry<Long, VItem>, Collection<String>> mapping,
                Collection<String> toMatch
        ) {
            return entry -> {
                Collection<String> value = mapping.apply(entry);

                return switch (this) {
                    case ALL -> value
                            .stream()
                            .map(v -> toMatch
                                    .stream()
                                    .mapToDouble(t -> calculateSimilarity(v, t))
                                    .average()
                                    .orElse(0))
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0);
                    case PARTIAL, SUB_PARTIAL -> value
                            .stream()
                            .map(v -> toMatch
                                    .stream()
                                    .mapToDouble(t -> calculateSimilarity(v, t))
                                    .max()
                                    .orElse(0))
                            .mapToDouble(Double::doubleValue)
                            .max()
                            .orElse(0);
                };
            };
        }
    }

    /**
     * Enum representing different search contexts for mapping entries to collections of strings.
     */
    public enum SearchContext {
        ID, SLUG, OWNER, VTAG, BORN;

        /**
         * Provides a mapping function based on the search context.
         *
         * @return A function to map entries to collections of strings.
         */
        @NotNull
        public Function<Map.Entry<Long, VItem>, Collection<String>> mapping() {
            return switch (this) {
                case ID -> entry -> Set.of(String.valueOf(entry.getKey()));
                case SLUG -> entry -> Set.of(entry.getValue().get(NetworkKey.SLUG).view());
                case OWNER -> entry -> entry.getValue().get(NetworkKey.OWNER).view().raw.stream().map(VOwnerData::playerName).collect(Collectors.toSet());
                case BORN -> entry -> entry.getValue().get(NetworkKey.BORN_AROUND).view().raw.stream().map(VOwnerData::playerName).collect(Collectors.toSet());
                case VTAG -> entry -> entry.getValue().get(NetworkKey.VTAG).view().raw.stream().map(VTag::name).collect(Collectors.toSet());
            };
        }
    }

    /**
     * Enum representing different sorting contexts for evaluating entries.
     */
    public enum SortContext {
        DISTANCE, BIRTH, SIZE, NONE, RELEVANCE;

        /**
         * Provides an evaluator function based on the sorting context.
         *
         * @param playerPosition The player's position for distance calculations.
         * @param matchContext   The matching context for relevance calculations.
         * @param searchContext  The search context for mapping entries.
         * @param toMatch        The collection of strings to match against.
         * @return A function to evaluate entries.
         */
        public Function<Map.Entry<Long, VItem>, Double> evaluator(
                Vector3d playerPosition,
                MatchContext matchContext,
                SearchContext searchContext,
                Collection<String> toMatch
        ) {
            return switch (this) {
                case DISTANCE -> entry -> entry.getValue().get(NetworkKey.COORDINATE).view().distance(playerPosition);
                case BIRTH -> entry -> entry.getValue().get(NetworkKey.BIRTH).view().doubleValue();
                case SIZE -> entry -> (double) UIFactory.volumeOf(entry.getValue().get(NetworkKey.AABB).view());
                case NONE -> entry -> 0.0;
                case RELEVANCE -> entry -> matchContext.evaluator(searchContext.mapping(), toMatch).apply(entry);
            };
        }
    }

    /**
     * Enum representing different grouping contexts for categorizing entries.
     */
    public enum GroupContext {
        BY_ID, BY_CLUSTER, BY_OWNER, BY_BORN, BY_TAG;

        /**
         * Provides a flat-mapping function based on the grouping context.
         *
         * @return A function to flat-map entries into pairs of group keys and entries.
         */
        Function<Map.Entry<Long, VItem>, Stream<Pair<Object, Map.Entry<Long, VItem>>>> flatMapper() {
            return switch (this) {
                case BY_ID -> entry -> Stream.of(new Pair<>(entry.getKey(), entry));
                case BY_CLUSTER -> entry -> Stream.of(new Pair<>(entry.getValue().get(NetworkKey.CLUSTER).view().ids, entry));
                case BY_OWNER -> ofSet(e -> e.getValue().get(NetworkKey.OWNER).view().raw, VOwnerData::playerName);
                case BY_BORN -> ofSet(e -> e.getValue().get(NetworkKey.BORN_AROUND).view().raw, VOwnerData::playerName);
                case BY_TAG -> ofSet(e -> e.getValue().get(NetworkKey.VTAG).view().raw, VTag::name);
            };
        }

        /**
         * Creates a summarizer function that converts an object into a string representation.
         * If the object is a set, it joins the string representations of its elements with commas.
         * Otherwise, it returns the object's string representation.
         *
         * @return A function that takes an object and returns its summarized string representation.
         */
        Function<Object, String> summerizer() {
            return switch (this) {
                case BY_ID, BY_CLUSTER -> o -> "";
                case BY_OWNER, BY_BORN -> Object::toString;
                case BY_TAG -> o -> {
                    if (o instanceof Set<?> set) {
                        return set.stream().map(Object::toString).collect(Collectors.joining(","));
                    }
                    return o.toString();
                };
            };
        }



        /**
         * Helper method to create a flat-mapping function for sets.
         *
         * @param getter A function to retrieve a set from an entry.
         * @param map    A function to map set elements to strings.
         * @param <T>    The type of elements in the set.
         * @return A function to flat-map entries into pairs of group keys and entries.
         */
        public <T> Function<Map.Entry<Long, VItem>, Stream<Pair<Object, Map.Entry<Long, VItem>>>> ofSet(Function<Map.Entry<Long, VItem>, Set<T>> getter, Function<T, String> map) {
            return entry -> {
                var set = getter.apply(entry);
                if (set.isEmpty()) {
                    return Stream.of(new Pair<>(Set.of(), entry));
                }
                return set.stream().map(map).map(tag -> new Pair<>(tag, entry));
            };
        }
    }

    /**
     * Enum representing different group policies for reducing grouped entries.
     */
    public enum GroupPolicy {
        MIN, MAX, AVG, SUM;

        /**
         * Provides a reducer function based on the group policy.
         *
         * @param mapping A function to map elements to double values.
         * @param <E>     The type of elements in the set.
         * @return A function to reduce a set of elements to a single double value.
         */
        <E> Function<Set<E>, Double> reducer(Function<E, Double> mapping) {
            return switch (this) {
                case MIN -> set -> set.stream().map(mapping).min(Comparator.naturalOrder()).orElse(null);
                case MAX -> set -> set.stream().map(mapping).max(Comparator.naturalOrder()).orElse(null);
                case AVG -> set -> set.stream().map(mapping).mapToDouble(Double::doubleValue).average().orElse(0);
                case SUM -> set -> set.stream().map(mapping).mapToDouble(Double::doubleValue).sum();
            };
        }
    }

    /**
     * Record representing the context for generating filters and evaluators.
     *
     * @param matchContext The matching context.
     * @param searchContext The search context.
     * @param sortContext The sorting context.
     * @param groupContext The grouping context.
     * @param groupPolicy The group policy.
     * @param playerPosition The player's position.
     * @param toMatch The collection of strings to match against.
     */
    public record Context(
            MatchContext matchContext,
            SearchContext searchContext,
            SortContext sortContext,
            GroupContext groupContext,
            GroupPolicy groupPolicy,
            Vector3d playerPosition,
            Collection<String> toMatch
    ) {}

    /**
     * Generates a function to process a set of entries based on the provided context.
     *
     * @param context The context for filtering, sorting, and grouping.
     * @return A function to process a set of entries into a stream of grouped sets.
     */
    public static Function<Set<Map.Entry<Long, VItem>>, Stream<Pair<String, Set<Map.Entry<Long, VItem>>>>> generate(Context context) {
        return set -> {
            Function<Map.Entry<Long, VItem>, Collection<String>> mapping = context.searchContext.mapping();
            Predicate<Map.Entry<Long, VItem>> filter = context.matchContext.filter(mapping, context.toMatch);
            Function<Map.Entry<Long, VItem>, Double> evaluator = context.sortContext.evaluator(context.playerPosition, context.matchContext, context.searchContext, context.toMatch);
            Function<Map.Entry<Long, VItem>, Stream<Pair<Object, Map.Entry<Long, VItem>>>> flatMapper = context.groupContext.flatMapper();
            Function<Object, String> summerizer = context.groupContext.summerizer();
            Function<Set<Map.Entry<Long, VItem>>, Double> reducer = context.groupPolicy.reducer(evaluator);

            return set
                    .stream()
                    .filter(filter)
                    .flatMap(flatMapper)
                    .collect(Collectors.groupingBy(
                            Pair::getFirst,
                            Collectors.mapping(
                                    Pair::getSecond,
                                    Collectors.toSet()
                            )
                    ))
                    .entrySet()
                    .stream()
                    .map(e -> new Pair<>(new Pair<>(summerizer.apply(e.getKey()), e.getValue()), reducer.apply(e.getValue())))
                    .sorted(Comparator.comparingDouble(Pair::getSecond))
                    .map(Pair::getFirst);
        };
    }
}

