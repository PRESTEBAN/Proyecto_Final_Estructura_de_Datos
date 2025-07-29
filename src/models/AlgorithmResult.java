package models;


public class AlgorithmResult {
    private final String algorithmName;
    private final int pathLength;
    private final long executionTime;

    public AlgorithmResult(String name, int length, long time) {
        this.algorithmName = name;
        this.pathLength = length;
        this.executionTime = time;
    }

    public String fetchProcessName() {
        return algorithmName;
    }

    public int obtainRouteLength() {
        return pathLength;
    }

    public long retrieveExecutionTime() {
        return executionTime;
    }


    @Override
    public String toString() {
        return algorithmName + "," + pathLength + "," + executionTime;
    }
}