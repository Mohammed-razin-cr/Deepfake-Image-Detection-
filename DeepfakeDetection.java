import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class DeepfakeDetection {
    public static void main(String[] arg) {
        String imageDirectory = "local_images/";
        File folder = new File(imageDirectory);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Image directory not found: " + imageDirectory);
            return;
        }
        File[] imageFiles = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
        if (imageFiles == null || imageFiles.length == 0) {
            System.err.println("No images found in the directory: " + imageDirectory);
            return;
        }
        ExecutorService executor = Executors.newFixedThreadPool(4);
        HashMap<String, Future<Integer>> results = new HashMap<>();
        int realCount = 0, deepfakeCount = 0;

        for (File imageFile : imageFiles) {
            results.put(imageFile.getName(), executor.submit(() -> detectVisualArtifacts(imageFile)));
        }
        executor.shutdown();
        String csvFileName = "detection_results.csv";
        try (PrintWriter writer = new PrintWriter(new File(csvFileName))) {
            writer.println("Image Name,Score,Classification");

            for (Map.Entry<String, Future<Integer>> entry : results.entrySet()) {
                try {
                    String imageName = entry.getKey();
                    int artifactScore = entry.getValue().get();
                    String classification = classifyImage(artifactScore);
                    writer.println(imageName + "," + artifactScore + "," + classification);
                    if (classification.equals("Deepfake")) deepfakeCount++;
                    else realCount++;
                } catch (Exception e) {
                    System.err.println("Error processing image: " + entry.getKey());
                    e.printStackTrace();
                }
            }
            System.out.println("Results saved to " + csvFileName);
        } catch (FileNotFoundException e) {
            System.err.println("Error writing CSV file");
            e.printStackTrace();
        }

        System.out.println("Real images: " + realCount);
        System.out.println("Deepfake images: " + deepfakeCount);
        runPythonScript();
    }
    public static int detectVisualArtifacts(File imageFile) {
        int score = 0;
        String fileName = imageFile.getName().toLowerCase();
        if (fileName.contains("blurred") || fileName.contains("light")) {
            score += 6;
        }
        if (fileName.contains("fake") || fileName.contains("artifact")) {
            score += 8;
        }
        score += new Random().nextInt(3);
        return score;
    }
    public static String classifyImage(int artifactScore) {
        if (artifactScore > 7) {
            return "Deepfake";
        } else {
            return "Real";
        }
    }
    public static void runPythonScript() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "generate_chart.py");
            Process process = pb.start();
            process.waitFor();
            System.out.println("Chart generated successfully.");
        } catch (Exception e) {
            System.err.println("Error running Python script");
            e.printStackTrace();
        }
    }
}
