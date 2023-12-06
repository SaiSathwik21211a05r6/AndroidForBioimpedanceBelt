package com.example.eegreader.database;

class Reading1 {
    private int id;
    private double frequency;
    private double bioimpedance;
    private double phase;
    private String electrodeCombination1;
    private String electrodeCombination2;

    public Reading1(double frequency, double bioimpedance, double phase,
                    String electrodeCombination1, String electrodeCombination2) {
        this.frequency = frequency;
        this.bioimpedance = bioimpedance;
        this.phase = phase;
        this.electrodeCombination1 = electrodeCombination1;
        this.electrodeCombination2 = electrodeCombination2;
    }

    // Add getters and setters as needed
}
