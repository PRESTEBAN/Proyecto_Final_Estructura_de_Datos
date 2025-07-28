package models;

public class AlgorithmResult {
    private final long executionDuration;
    private final int routeDistance;
    private final String processIdentifier;

    @Override
    public String toString() {
        return this.processIdentifier + "," + this.routeDistance + "," + this.executionDuration;
    }

    public long retrieveExecutionTime() {
        return this.executionDuration;
    }

    public int obtainRouteLength() {
        return this.routeDistance;
    }

    public String fetchProcessName() {
        return this.processIdentifier;
    }

    public AlgorithmResult(String methodName, int distanceValue, long durationValue) {
        this.processIdentifier = methodName;
        this.routeDistance = distanceValue;
        this.executionDuration = durationValue;
    }
}
