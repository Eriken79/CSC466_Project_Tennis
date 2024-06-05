import java.util.ArrayList;
import java.util.HashSet;

public class DBSCAN {
    private double epsilon;
    private int minPts;

    public DBSCAN(double epsilon, int minPts) {
        this.epsilon = epsilon;
        this.minPts = minPts;
    }

    public void run(ArrayList<DataPoint> points) {
        int curCluster = -1; // Current cluster number

        for (DataPoint P : points) {
            // Previously processed in inner loop
            if (P.getCluster() != DataPoint.UNDEFINED_CLUSTER) continue;

            // Check for minPts parameter
            ArrayList<DataPoint> neighbors = rangeQuery(points, P);
            if (neighbors.size() < minPts) {
                P.setCluster(DataPoint.NOISE_CLUSTER);
                continue;
            }

            curCluster++;
            P.setCluster(curCluster);

            HashSet<DataPoint> seedSet = new HashSet<>(neighbors);
            seedSet.remove(P);

            // Keep walking through neighboring core points until no new
            // core points are found. All of these will be in the same cluster.
            while (!seedSet.isEmpty()) {
                HashSet<DataPoint> newSet = new HashSet<>();

                for (DataPoint Q : seedSet) {
                    if (Q.getCluster() == DataPoint.NOISE_CLUSTER) {
                        Q.setCluster(curCluster); // Change noise point to border point
                    }
                    if (Q.getCluster() != DataPoint.UNDEFINED_CLUSTER) {
                        continue; // Already processed
                    }

                    Q.setCluster(curCluster); // Label neighbor with same cluster

                    neighbors = rangeQuery(points, Q); // Find neighbors of neighbor
                    if (neighbors.size() >= minPts) {
                        newSet.addAll(neighbors); // Q is a core point
                    }
                }

                seedSet.clear();
                seedSet.addAll(newSet);
            }
        }
    }

    // Return a list of neighbors within the epsilon value
    private ArrayList<DataPoint> rangeQuery(ArrayList<DataPoint> points, DataPoint Q) {
        ArrayList<DataPoint> neighbors = new ArrayList<>();
        for (DataPoint point : points) {
            if (Q.distanceTo(point) <= epsilon) {
                neighbors.add(point);
            }
        }

        return neighbors;
    }
}
