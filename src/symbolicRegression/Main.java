package symbolicRegression;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.GPProblem;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class Main {
    private static String DEFAULT_FILE = "regression.txt";
    private static int DEFAULT_POPULATION = 1000;
    private static int DEFAULT_NUM_EVOLUTIONS = 1000;

    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {
        // initialise a configuration object
        GPConfiguration configuration = setConfiguration();

        try {
            // initialise the GP problem
            GPProblem problem = new SymbolicRegressionProblem(configuration);
            GPGenotype gp = problem.create();
            gp.setVerboseOutput(true);

            // evolve
            gp.evolve(DEFAULT_NUM_EVOLUTIONS);

            // show results to the console and generate the png image file
            gp.outputSolution(gp.getAllTimeBest());
            problem.showTree(gp.getAllTimeBest(), "symbolic_regression_tree.png");

        } catch (InvalidConfigurationException e) {
            System.err.println("Invalid Configuration Exception.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Set parameters.
     */
    private static GPConfiguration setConfiguration() {
        GPConfiguration config = null;

        try {
            config = new GPConfiguration();
            // We use a delta fitness evaluator because we compute a defect rate, not a point score!
            config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
            config.setPopulationSize(DEFAULT_POPULATION);
            config.setFitnessFunction(generateFitnessFunction());
            config.setStrictProgramCreation(true);
        } catch (InvalidConfigurationException e) {
            System.err.println("Invalid Configurations.");
            e.printStackTrace();
            System.exit(-1);
        }

        return config;
    }

    /**
     * Generate the fitness function from the file that contains input and output values.
     */
    private static GPFitnessFunction generateFitnessFunction() {
        String trainingFilePath = ClassLoader.getSystemResource(DEFAULT_FILE).getPath();
        // parse the file into two sets of numbers as inputs and outputs
        List<Double> inputs = new ArrayList<>();
        List<Double> outputs = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(trainingFilePath))) {
            // skip empty lines
            scanner.nextLine();
            scanner.nextLine();

            // read in the file
            ArrayList<String> lines = new ArrayList<>();
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }

            for (String line : lines) {
                String[] values = line.trim().split("\\s+");
                inputs.add(Double.parseDouble(values[0]));
                outputs.add(Double.parseDouble(values[1]));
            }

        } catch (FileNotFoundException e) {
            System.err.println("File Not Found.");
            e.printStackTrace();
            System.exit(-1);
        }

        return new RegressionFitnessFunction(inputs, outputs);
    }
}
