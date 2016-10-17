package com.company;

import java.util.ArrayList;

public class CYK {

    private static void initP(boolean P[][][], int n, int r) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < r; k++) {
                    P[i][j][k] = false;
                }
            }
        }
    }

    private int findIndex(CNF cnf, String symbol) {
        for (int i = 0; i < cnf.size(); i++) {
            String[] rule = cnf.getRules(i);
            if (rule.length == 2) {
                if (symbol.equals(cnf.getRules(i)[0]))
                    return i;
            }
        }
        for (int i = 0; i < cnf.size(); i++) {
            if (symbol.equals(cnf.getRules(i)[0]))
                return i;
        }
        return -1;

    }

    private  Integer[] findStartSymbols(CNF cnf) {
        ArrayList<Integer> startSymbols = new ArrayList<Integer>();
        for (int i = 0; i < cnf.size(); i++) {
            String[] rule = cnf.getRules(i);
            if (rule[0].equals("S")) {
                startSymbols.add(i);
                if (rule.length == 2) {
                    startSymbols.add(findIndex(cnf, rule[1]));
                }
            }
        }

        Integer[] output = new Integer[startSymbols.size()];
        output = startSymbols.toArray(output);
        return output;
    }

    private  boolean algorithm(String S, CNF cnf) {
        int sLength = S.length();
        int cnfSize = cnf.size();
        Integer[] startSymbols = findStartSymbols(cnf);
        boolean P[][][] = new boolean[sLength][sLength][cnfSize];
        initP(P, sLength, cnfSize);

        for (int i = 0; i < sLength; i++) {
            for (int j = 0; j < cnfSize; j++) {
                String[] rule = (String[]) cnf.getRules(j);
                if (rule.length == 2) {
                    if (rule[1].equals(String.valueOf(S.charAt(i)))) {
                        int A = findIndex(cnf, rule[0]);
                        P[i][0][A] = true;
                    }
                }
            }
        }

        for (int i = 1; i < sLength; i++) {
            for (int j = 0; j < sLength - i; j++) {
                for (int k = 0; k < i; k++) {
                    for (int m = 0; m < cnfSize; m++) {
                        String[] rule = cnf.getRules(m);
                        if (rule.length > 2) {
                            int index1 = findIndex(cnf, rule[0]);
                            int index2 = findIndex(cnf, rule[1]);
                            int index3 = findIndex(cnf, rule[2]);

                            if (P[j][k][index2] && P[j + k + 1][i - k - 1][index3])
                                P[j][i][index1] = true;
                        }
                    }
                }
            }
        }

        System.out.println("TABLE");
        for (int i = 0; i < S.length(); i++) {
            System.out.print(S.charAt(i) + " ");
        }

        System.out.println();
        printP(P, sLength, cnfSize, cnf);

        for (Integer x : startSymbols) {
            if (x >= 0) {
                if (P[0][sLength - 1][x]) {
                    return true;
                }
            }
        }
        return false;
    }

    private void printP(boolean P[][][], int n, int r, CNF cnf) {
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n - i; k++) {
                System.out.print("{");
                for (int m = 0; m < r; m++) {
                    if (P[k][i][m]) {
                        System.out.print(cnf.getRules(m)[0] + " ");
                    }

                }
                System.out.print("} ");
            }
            System.out.println();
        }
    }

    public static void main(String args[]) {

        CYK cyk = new CYK();
        CNF cnf;
        String input;
        if(args.length > 1){
            cnf = new CNF(args[0]);
            input = args[1];
        }
        else {
            cnf = new CNF("input");
            input = "abab";
        }
        if (!cnf.isRead()) {
            System.out.println("cnf could not be read.");
            return;
        }

            if (cyk.algorithm(input, cnf)) {
                System.out.println("String belongs to the language");
            }
            else {
                System.out.println("String is not belongs to the language");
            }
    }
}