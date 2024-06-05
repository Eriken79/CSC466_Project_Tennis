import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class KMeanClustering {
    private List<Double[]> featuresData;
    private HashMap<String, List<Double[]>> clusters;
    private HashMap<String, Double[]> clusterCentroidValues;
    private int numberOfClusters;
    private int numberEpochs = 1000;

    public static void main(String[] args) {
        String filePath = "processed_atp_tennis_encoded.csv";
        List<Double[]> dataSet = new ArrayList<>();
        List<String[]> originalData = new ArrayList<>();
        List<String> selectedColumns = Arrays.asList("Tournament", "Surface", "Rank_1", "Rank_2", "Pts_1", "Pts_2", "Odd_1", "Odd_2", "Score");
        String headerLine = null;
        try {
            Scanner scanner = new Scanner(new File(filePath));
            headerLine = scanner.nextLine();
            String[] headers = headerLine.split(",");
            List<Integer> columnIndicesToKeep = new ArrayList<>();
            for (int i = 0; i < headers.length; i++) {
                if (selectedColumns.contains(headers[i])) {
                    columnIndicesToKeep.add(i);
                }
            }


            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");
                originalData.add(values); 
                Double[] data = new Double[columnIndicesToKeep.size()];
                int dataIndex = 0;
                for (int i : columnIndicesToKeep) {
                    data[dataIndex++] = Double.parseDouble(values[i]);
                }
                dataSet.add(data);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        KMeanClustering clustering = new KMeanClustering(dataSet, 3);
        HashMap<String, Double[]> clusterCentroidValues = clustering.buildCluster();

        clusterCentroidValues.forEach((k, val) ->
            System.out.println("ClusterId/CategoryId: " + k + " Centroid values: " + Arrays.toString(val))
        );


        // Need to code a way to get the cluster ids back to the original dataset

        List<String[]> dataWithClusters = new ArrayList<>();
        for (int i = 0; i < dataSet.size(); i++) {
            Double[] dataPoint = dataSet.get(i);
            String clusterId = clustering.findClosestCluster(dataPoint);
            String[] originalRow = originalData.get(i);
            String[] rowWithCluster = Arrays.copyOf(originalRow, originalRow.length + 1);
            rowWithCluster[rowWithCluster.length - 1] = clusterId;
            dataWithClusters.add(rowWithCluster);
        }
    
        // Save the data with cluster information
        try (PrintWriter writer = new PrintWriter(new File("processed_with_clusters.csv"))) {
            writer.println(headerLine + ",Cluster"); // Write the header with the new Cluster column
            for (String[] row : dataWithClusters) {
                writer.println(String.join(",", row));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    public KMeanClustering(List<Double[]> featuresData, int numberOfClusters) {
        this.featuresData = featuresData;
        this.numberOfClusters = numberOfClusters;
        this.initializeModel();
    }

    public HashMap<String, Double[]> buildCluster() {
        classifyPoint(featuresData);
        return repeatCycle();
    }

    private void initializeModel() {
        clusters = new HashMap<>();
        clusterCentroidValues = new HashMap<>();
        for (int i = 0; i < numberOfClusters; i++) {
            int randomIndex = new Random().nextInt(featuresData.size());
            Double[] initialValues = featuresData.get(randomIndex);
            String clusterId = String.valueOf(i);
            clusterCentroidValues.put(clusterId, initialValues);
            clusters.put(clusterId, new ArrayList<>());
            System.out.println("Initial centroid value for Cluster " + clusterId + ": " + Arrays.toString(initialValues));
        }
    }

    private HashMap<String, Double[]> repeatCycle() {
        while (numberEpochs-- > 0) {
            boolean centroidsChanged = false;
            for (String key : clusters.keySet()) {
                List<Double[]> clusterData = clusters.get(key);
                Double[] newCentroid = calculateMean(clusterData);
                centroidsChanged = centroidsChanged || !Arrays.equals(clusterCentroidValues.put(key, newCentroid), newCentroid);
            }
            if (!centroidsChanged){
                break;
            }
            reassignClusters();
        }
        return clusterCentroidValues;
    }

    private void reassignClusters() {
        for (List<Double[]> cluster : clusters.values()) {
            cluster.clear();
        }
        classifyPoint(featuresData);
    }

    private void classifyPoint(List<Double[]> data) {
        for (Double[] point : data) {
            String closestCluster = findClosestCluster(point);
            clusters.get(closestCluster).add(point);
        }
    }

    private String findClosestCluster(Double[] point) {
        double minDistance = Double.MAX_VALUE;
        String closestCluster = null;
        for (String clusterId : clusterCentroidValues.keySet()) {
            double distance = euclideanDistance(clusterCentroidValues.get(clusterId), point);
            if (distance < minDistance) {
                minDistance = distance;
                closestCluster = clusterId;
            }
        }
        return closestCluster;
    }

    private double euclideanDistance(Double[] a, Double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    private Double[] calculateMean(List<Double[]> data) {
        int length = data.get(0).length;
        Double[] mean = new Double[length];
        Arrays.fill(mean, 0.0);
        for (Double[] point : data) {
            for (int i = 0; i < length; i++) {
                mean[i] += point[i];
            }
        }
        for (int i = 0; i < length; i++) {
            mean[i] /= data.size();
        }
        return mean;
    }

    public String predictCategory(Double[] data) {
        return findClosestCluster(data);
    }


}
