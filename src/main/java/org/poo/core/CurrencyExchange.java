package org.poo.core;

import org.poo.fileio.ExchangeInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class CurrencyExchange {

    private final Map<String, List<Pair<String, Double>>> exchangeGraph;

    public CurrencyExchange(final ExchangeInput[] exchangeRates) {
        exchangeGraph = new HashMap<>();
        buildGraph(exchangeRates);
    }

    private record Pair<K, V>(K key, V value) { }

    private void buildGraph(final ExchangeInput[] exchangeRates) {

        for (ExchangeInput rate : exchangeRates) {

            exchangeGraph.computeIfAbsent(rate.getFrom(), k -> new ArrayList<>())
                    .add(new Pair<>(rate.getTo(), rate.getRate()));

            exchangeGraph.computeIfAbsent(rate.getTo(), k -> new ArrayList<>())
                    .add(new Pair<>(rate.getFrom(), 1 / rate.getRate()));
        }
    }

    /**
     * Aceasta metoda calculeaza cursul de schimb valutar
     *
     * @param from moneda pe care vrem sa o schimbam
     * @param to moneda in care vrem sa schimbam
     * @return cursul de schimb valutar
     */
    public double findRate(final String from, final String to) {

        if (!exchangeGraph.containsKey(from) || !exchangeGraph.containsKey(to)) {
            return -1;
        }

        Set<String> visited = new HashSet<>();

        return dfs(from, to, 1.0, visited);
    }

    private double dfs(final String current, final String target,
                       final double accumulatedRate, final Set<String> visited) {

        if (current.equals(target)) {
            return accumulatedRate;
        }

        visited.add(current);

        for (Pair<String, Double> neighbor : exchangeGraph.get(current)) {

            if (!visited.contains(neighbor.key())) {

                double newAcumulatedRate = accumulatedRate * neighbor.value();
                double rate = dfs(neighbor.key(), target, newAcumulatedRate, visited);

                if (rate != -1) {
                    return rate;
                }
            }
        }

        return -1;
    }
}
