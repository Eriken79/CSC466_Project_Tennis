import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class DBSCANDriver {
    public static void main(String args[]) {
        ArrayList<DataPoint> points = processFile("processed_credit_dataset.csv");
        if (points == null) return;

        DBSCAN dbscan = new DBSCAN(1.8, 145);
        dbscan.run(points);

        ArrayList<DataPoint> clusteredPoints = points.stream()
                .filter(point -> point.getCluster() != DataPoint.NOISE_CLUSTER)
                .collect(Collectors.toCollection(ArrayList::new));

        HashSet<Integer> clusters = new HashSet<>();

        for (DataPoint P : clusteredPoints) {
            clusters.add(P.getCluster());
        }

        System.out.println(clusteredPoints.size() + " clustered (non-noise) points");
        System.out.println(clusters.size() + " clusters assigned");

        // Create csv file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("dbscan_cc_clustered.csv"));
            writer.write("Gender,Own_car,Own_property,Unemployed,Num_children,Num_family,Account_length,Total_income,Age,Years_employed,Income_type_Commercial associate,Income_type_Pensioner,Income_type_State servant,Income_type_Student,Income_type_Working,Education_type_Academic degree,Education_type_Higher education,Education_type_Incomplete higher,Education_type_Lower secondary,Education_type_Secondary / secondary special,Family_status_Civil marriage,Family_status_Married,Family_status_Separated,Family_status_Single / not married,Family_status_Widow,Housing_type_Co-op apartment,Housing_type_House / apartment,Housing_type_Municipal apartment,Housing_type_Office apartment,Housing_type_Rented apartment,Housing_type_With parents,Occupation_type_Accountants,Occupation_type_Cleaning staff,Occupation_type_Cooking staff,Occupation_type_Core staff,Occupation_type_Drivers,Occupation_type_HR staff,Occupation_type_High skill tech staff,Occupation_type_IT staff,Occupation_type_Laborers,Occupation_type_Low-skill Laborers,Occupation_type_Managers,Occupation_type_Medicine staff,Occupation_type_Other,Occupation_type_Private service staff,Occupation_type_Realty agents,Occupation_type_Sales staff,Occupation_type_Secretaries,Occupation_type_Security staff,Occupation_type_Waiters/barmen staff,Target,Cluster\n");

            for (DataPoint P : clusteredPoints) {
                writer.write(P.toString() + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<DataPoint> processFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String curLine;
            ArrayList<DataPoint> points = new ArrayList<>();
            int j = 0;

            while ((curLine = reader.readLine()) != null) {
                j++;
                if (j == 1) continue; // Ignore column name line

                String[] features = curLine.split(",");
                ArrayList<Double> coordinate = new ArrayList<>();
                int humanJudgement = -1;
                for (int i = 0; i < features.length; i++) {
                    if (i == 11) {
                        humanJudgement = Integer.parseInt(features[i]);
                        continue;
                    }
                    if (i == 0) continue; // Don't include ID
                    double featureDecimal = Double.parseDouble(features[i]);
                    coordinate.add(featureDecimal);
                }

                points.add(new DataPoint(coordinate, humanJudgement));
            }

            return points;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
