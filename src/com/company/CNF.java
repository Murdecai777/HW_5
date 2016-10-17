package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class CNF {

    private ArrayList<String[]> rules;

    CNF(String inputFile) {
        this.rules = startAlgorithm(readFile(inputFile));
    }

    int size() {
        return this.rules.size();
    }

    String[] getRules(int i) {
        return this.rules.get(i);
    }

    private static ArrayList<String[]> startAlgorithm(ArrayList<String[]> rules) {
        stepOne(rules);
        stepTwo(rules);
        stepThree(rules);
        stepFour(rules);
        stepFive(rules);

        return rules;

    }

    private static void stepFive(ArrayList<String[]> rules) {
        ArrayList<String[]> unitProductions = findUnitProductions(rules);
        for (int i = 0; i < unitProductions.size(); i++) {
            String[] production = unitProductions.get(i);
            for (String[] tempProduction : unitProductions) {
                if (Objects.equals(production[0], tempProduction[1])
                        && Objects.equals(production[1], tempProduction[0])) {
                    for (int k = 0; k < rules.size(); k++) {
                        String[] rule = rules.get(k);
                        for (int l = 0; l < rule.length; l++) {
                            if (rule[l].equals(production[1])) {
                                rule[l] = production[0];
                            }
                        }
                        rules.set(k, rule);
                    }
                }
            }
        }

        for (int i = 0; i < rules.size(); i++) {
            String[] rule = rules.get(i);
            if (rule[0].equals(rule[1]) && rule.length == 2) {
                rules.remove(i);
                i--;
            }
        }

        unitProductions = findUnitProductions(rules);
        for (int i = 0; i < unitProductions.size(); i++) {
            String[] production = unitProductions.get(i);
            stepFiveRecursion(rules, production, i);
        }
        // remove duplicate rules
        for (int i = 0; i < rules.size(); i++) {
            String[] rule = rules.get(i);
            for (int j = 0; j < rules.size(); j++) {
                if (rules.get(j).length == rules.get(i).length) {
                    boolean isEqual = true;

                    for (int k = 0; k < rule.length; k++) {
                        if (!rules.get(j)[k].equals(rule[k])) {
                            isEqual = false;
                        }
                    }
                    if (i != j && isEqual) {
                        rules.remove(j);
                        j--;
                    }
                }
            }
        }

    }

    private static void stepFiveRecursion(ArrayList<String[]> rules,
                                          String[] production, int oldProductionIndex) {
        for (int j = 0; j < rules.size(); j++) {
            if (rules.get(j)[0].equals(production[1])) {
                if (rules.get(j).length == 2
                        && Character.isLowerCase(rules.get(j)[1].charAt(0))) {
                    String[] rule = {production[0], rules.get(j)[1]};
                    rules.add(rule);
                    int ruleCount = 0;
                    for (String[] rule1 : rules) {
                        if (rule1[0].equals(production[1])) {
                            ruleCount++;
                        }
                    }
                    if (ruleCount > 1) {
                        rules.remove(j);
                        j--;
                    }

                } else if (rules.get(j).length == 2 && Character.isUpperCase(rules.get(j)[1].charAt(0))) {
                    String[] newProduction = {production[0], rules.get(j)[1]};
                    rules.remove(oldProductionIndex);
                    if (oldProductionIndex < j) {
                        j--;
                    }
                    stepFiveRecursion(rules, newProduction, j);
                    j--;
                } else if (rules.get(j).length == 3) {

                    String[] rule = {production[0], rules.get(j)[1],
                            rules.get(j)[2]};
                    rules.add(rule);
                }
            }
        }
    }

    private static ArrayList<String[]> findUnitProductions(
            ArrayList<String[]> rules) {

        return rules.stream().filter(rule -> rule.length == 2 && Character.isUpperCase(rule[1].charAt(0))).collect(Collectors.toCollection(ArrayList::new));
    }

    private static void stepFour(ArrayList<String[]> rules) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i)[1].equals("e")) {
                String nullNonTerminal = rules.get(i)[0];
                rules.remove(i);

                removeEpsilon(rules, nullNonTerminal);
            }
        }
    }

    private static void removeEpsilon(ArrayList<String[]> rules,
                                      String nullNonTerminal) {

        for (int j = 0; j < rules.size(); j++) {

            if (rules.get(j)[1].equals(nullNonTerminal)) {
                if (rules.get(j).length == 3) {
                    if (rules.get(j)[2].equals(nullNonTerminal)) {

                        String newNullNonTerminal = rules.get(j)[0];

                        if (!isDoubleNonTerminal(rules, nullNonTerminal)) {
                            rules.remove(j);

                            removeEpsilon(rules, newNullNonTerminal);
                        }
                    } else {
                        String[] newRule = {rules.get(j)[0], rules.get(j)[2]};
                        if (isDoubleNonTerminal(rules, nullNonTerminal)) {
                            rules.add(j, newRule);
                            j++;
                        } else
                            rules.set(j, newRule);
                    }
                } else {
                    String newNullNonTerminal = rules.get(j)[0];
                    if (!isDoubleNonTerminal(rules, nullNonTerminal)) {
                        rules.remove(j);
                        removeEpsilon(rules, newNullNonTerminal);
                    }
                }
            } else if (rules.get(j).length == 3) {

                if (rules.get(j)[2].equals(nullNonTerminal)) {
                    String[] newRule = {rules.get(j)[0], rules.get(j)[1]};
                    if (isDoubleNonTerminal(rules, nullNonTerminal)) {
                        rules.add(j, newRule);
                        j++;
                    } else
                        rules.set(j, newRule);
                }
            }
        }

    }

    private static boolean isDoubleNonTerminal(ArrayList<String[]> rules,
                                               String NonTerminal) {
        int count = 1;
        for (String[] rule : rules) {
            if (rule[0].equals(NonTerminal))
                count++;
        }

        return count > 1;
    }

    private static void stepThree(ArrayList<String[]> rules) {
        boolean thereIsS = false;
        for (String[] rule : rules) {
            for (int j = 1; j < rule.length; j++) {
                if (rule[j].equals("S")) {
                    thereIsS = true;
                    break;
                }
            }
        }
        if (thereIsS) {
            for (String[] rule : rules) {
                for (int j = 0; j < rule.length; j++) {
                    if (rule[j].equals("S")) {
                        rule[j] = "S_0";
                    }
                }
            }
            String[] SigmaRule = {"S", "S_0"};
            rules.add(SigmaRule);
        }
    }

    private static void stepTwo(ArrayList<String[]> rules) {

        int count = 0;

        for (int i = 0; i < rules.size(); i++) {

            while (rules.get(i).length > 3) {
                int n = rules.get(i).length;
                String[] g = new String[3];
                g[0] = "P" + count;
                g[1] = rules.get(i)[n - 2];
                g[2] = rules.get(i)[n - 1];
                rules.add(g);
                String[] h = new String[n - 1];
                for (int j = 0; j < n - 2; j++) {
                    h[j] = rules.get(i)[j];
                }

                h[n - 2] = "P" + count;
                count++;
                rules.remove(i);
                rules.add(h);
            }
        }
    }

    private static void stepOne(ArrayList<String[]> rules) {
        int s = rules.size();
        for (int i = 0; i < s; i++) {
            if (rules.get(i).length > 2) {
                for (int j = 0; j < rules.get(i).length; j++) {
                    char n = rules.get(i)[j].charAt(0);
                    if (Character.isLowerCase(n)) {
                        String[] g = new String[2];
                        g[0] = rules.get(i)[j].toUpperCase() + "_0";
                        g[1] = rules.get(i)[j];
                        boolean isAlreadyDefined = false;
                        for (String[] rule : rules) {
                            if (g[0].equals(rule[0]))
                                isAlreadyDefined = true;
                        }
                        if (!isAlreadyDefined) {
                            rules.add(g);
                        }
                        rules.get(i)[j] = rules.get(i)[j].toUpperCase() + "_0";
                    }
                }
            }
        }
    }

    boolean isRead() {
        return this.rules != null;
    }

    private ArrayList<String[]> readFile(String inputFile) {
        ArrayList<String[]> rules = new ArrayList<String[]>();
        try {
            FileReader fr = new FileReader(inputFile);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                String[] rule = line.toString().split(" ");
                rules.add(rule);
                line = br.readLine();
            }

        } catch (Exception e) {
            System.out.println("Can't find file");
            return null;
        }
        return rules;
    }

    public static void main(String[] args) {
        CNF cnf;

        if(args.length != 0){
            cnf = new CNF(args[0]);
        }
        else {
            cnf = new CNF("input");
        }
        for (int i = 0; i < cnf.size(); i++) {
            String[] rule = cnf.getRules(i);
            for (String aRule : rule) {
                System.out.print(aRule + " ");
            }
            System.out.println();
        }
    }
}