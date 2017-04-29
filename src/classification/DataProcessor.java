package classification;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A utility class to process data
 */
public class DataProcessor {

    private static final String DATA_FILE = "breast-cancer-wisconsin.data";
    private static final String TRAINING_FILE = "training_set.data";
    private static final String TEST_FILE = "test_set.data";
    private static final float SPLIT_PORTION = 0.8f;

    /**
     * Read in data from the file
     *
     * @return
     */
    public static List<CancerInstance> readInData() {
        String filePath = ClassLoader.getSystemResource(DATA_FILE).getPath();
        List<CancerInstance> instances = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                List<String> values = new ArrayList<>(Arrays.asList(scanner.nextLine().split(",")));

                int id = Integer.parseInt(values.remove(0));
                int label = Integer.parseInt(values.remove(values.size() - 1));
                int[] features = values.stream()
                        .mapToInt(value -> Integer.parseInt(value.equals("?") ? "-1" : value))
                        .toArray();

                instances.add(new CancerInstance(id, features, label));
            }
        } catch (FileNotFoundException e) {
            abort(e, "File Not Found.");
        }

        return instances;
    }

    /**
     * Analyse the data, shuffle them, split them by the Pareto Ratio (80% training : 20% test), and write them into
     * two separate files.
     *
     * @param instances
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<CancerInstance>[] analyseAndSplitData(List<CancerInstance> instances) {
        List<CancerInstance> benignInstances = new ArrayList<>();
        List<CancerInstance> malignantInstances = new ArrayList<>();
        List<CancerInstance> trainingSet = new ArrayList<>();
        List<CancerInstance> testSet = new ArrayList<>();

        // divide them into benign set and malignant set
        instances.forEach(cancerInstance -> {
            if (cancerInstance.isBenign()) {
                benignInstances.add(cancerInstance);
            } else {
                malignantInstances.add(cancerInstance);
            }
        });

        // shuffle
        Collections.shuffle(benignInstances);
        Collections.shuffle(malignantInstances);

        // calculating ratios
        int totalNum = instances.size();
        int trainingNum = (int) (totalNum * SPLIT_PORTION);
        double benignRatio = ((double) benignInstances.size()) / totalNum;
        double malignantRatio = 1.0d - benignRatio;
        int numBenignToTraining = (int) (trainingNum * benignRatio);
        int numMalignantToTraining = (int) (trainingNum * malignantRatio);

        // divide into training set and test set according to the ratio calculated
        trainingSet.addAll(benignInstances.subList(0, numBenignToTraining));
        trainingSet.addAll(malignantInstances.subList(0, numMalignantToTraining));
        testSet.addAll(benignInstances.subList(numBenignToTraining, benignInstances.size()));
        testSet.addAll(malignantInstances.subList(numMalignantToTraining, malignantInstances.size()));

        // shuffle the training and test set
        Collections.shuffle(trainingSet);
        Collections.shuffle(testSet);

        // write them into separate files
        writeToFile(trainingSet, TRAINING_FILE);
        writeToFile(testSet, TEST_FILE);

        return (List<CancerInstance>[]) new List[]{ trainingSet, testSet };
    }

    /**
     * Write the given collection of instances into file using the given file name.
     *
     * @param instances
     * @param fileName
     */
    private static void writeToFile(List<CancerInstance> instances, String fileName) {
        String content = instances.stream().map(CancerInstance::toStringOnFile).collect(Collectors.joining("\n"));

        try {
            Files.write(Paths.get(fileName), content.getBytes());
        } catch (IOException e) {
            abort(e, "Writing to file failed.");
        }
    }

    /**
     * Abort the programme, print error messages.
     *
     * @param e
     * @param message
     */
    private static void abort(Exception e, String message) {
        System.err.println(message);
        e.printStackTrace();
        System.exit(-1);
    }
}
