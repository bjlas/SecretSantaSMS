package com.android.onehuman.secretsantasms.graph;

import com.android.onehuman.secretsantasms.model.Person;

import java.util.ArrayList;
import java.util.List;

/*
Thanks to https://github.com/gnbaron/hamiltonian-cycles
 */
public class Graph {

    private List<Person> personsLit = new ArrayList<>();

    public List<List<Person>> findHamiltonianCycles(Person start){
        List<List<Person>> empty = new ArrayList<>();
        List<Person> visited = new ArrayList<>();
        List<List<Person>> cycles = findHamiltonianCycles(start, visited, empty);
        return cycles;
    }

    private List<List<Person>> findHamiltonianCycles(Person current, List<Person> visited, List<List<Person>> cycles){
        if(!visited.contains(current)){
            visited.add(current);

            if(visited.size() == personsLit.size() && current.getCandidates().contains(visited.get(0))){
                List<Person> cycle = new ArrayList<>();
                cycle.addAll(visited);
                cycle.add(visited.get(0));
                cycles.add(cycle);
            } else {
                for(Person adjacent: current.getCandidates()) {
                    findHamiltonianCycles(adjacent, visited, cycles);
                }
            }

            visited.remove(current);
        }
        return cycles;
    }

    public void addPerson(Person person){
        personsLit.add(person);
    }
}

