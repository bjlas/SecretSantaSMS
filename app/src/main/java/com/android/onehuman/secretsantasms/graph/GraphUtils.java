package com.android.onehuman.secretsantasms.graph;

import com.android.onehuman.secretsantasms.model.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GraphUtils {


    public static List<Person> draw(HashMap<Integer, Person> allPersonsMaps, Person startPerson){
        Graph graph = createGraph(allPersonsMaps);
        List<List<Person>> allPossibleSolutions=graph.findHamiltonianCycles(startPerson);
        return selectSolution(allPossibleSolutions);
    }

    public static List<Person> selectSolution(List<List<Person>> cycles) {
        Random random = new Random();
        if(cycles.size() > 0) {
            int randomSolution = random.nextInt(cycles.size());
            return cycles.get(randomSolution);
        } else {
            return new ArrayList<Person>();
        }
    }

    public static Graph createGraph(HashMap<Integer, Person> map) {
        Graph graph = new Graph();
        for(HashMap.Entry<Integer, Person> entry : map.entrySet()) {
            graph.addPerson(entry.getValue());
        }
        return graph;
    }

}
