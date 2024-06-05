import java.util.ArrayList;

public class DataPoint {
    private ArrayList<Double> coordinate;
    private int cluster;
    private int humanJudgement;
    public static int UNDEFINED_CLUSTER = -1;
    public static int NOISE_CLUSTER = -2;

    public DataPoint(ArrayList<Double> coordinate, int humanJudgement) {
        this.coordinate = coordinate;
        this.cluster = UNDEFINED_CLUSTER;
        this.humanJudgement = humanJudgement;
    }

    public double distanceTo(DataPoint otherPoint) {
        double sumSquares = 0;
        for (int i = 0; i < coordinate.size(); i++) {
            sumSquares += Math.pow(coordinate.get(i) - otherPoint.coordinate.get(i), 2);
        }
        return Math.sqrt(sumSquares);
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }

    public int getCluster() {
        return cluster;
    }

    @Override
    public String toString() {
        String s = "";
        for (double point : coordinate) {
            s += point + ",";
        }
        s += humanJudgement + "," + cluster;
        return s;
    }
}
