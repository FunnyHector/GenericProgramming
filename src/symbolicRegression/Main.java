package symbolicRegression;

import org.apache.log4j.BasicConfigurator;
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
 * The entry class (where main function is) for Symbolic Regression programme.
 */
public class Main {
    private static final String DEFAULT_FILE = "regression.txt";
    private static final int DEFAULT_POPULATION = 1000;
    private static final int DEFAULT_NUM_EVOLUTIONS = 800;

    /**
     * Main function
     *
     * @param args
     */
    public static void main(String[] args) {
        // initialise log4j
        BasicConfigurator.configure();

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
            abort(e, "Invalid Configuration Exception.");
        }
    }

    /**
     * Initialise a configuration object, and set parameters.
     */
    private static GPConfiguration setConfiguration() {
        GPConfiguration config = null;

        try {
            config = new GPConfiguration();

            // TODO: this is where extra configurations come into play
            // the number used here are all default value.
            // config.setCrossoverProb(0.9f);  // CrossoverProb + ReproductionProb = 1.0f
            // config.setReproductionProb(0.1f);
            // config.setMutationProb(0.1f);
            // config.setDynamizeArityProb(0.08f);  // The probability that the arity of a node is changed during growing a program.
            // config.setNewChromsPercent(0.3f);  // Percentage of the population that will be filled with new individuals during evolution. Must be between 0.0d and 1.0d.
            // config.setFunctionProb(0.9f);  // In crossover: If random number (0..1) < this value, then choose a function otherwise a terminal.
            // config.setMaxCrossoverDepth(17);  // The maximum depth of an individual resulting from crossover
            // config.setMaxInitDepth(7);  // The maximum depth of an individual when the world is created.
            // config.setMinInitDepth(2);  // The minimum depth of an individual when the world is created.

            // use a delta fitness evaluator because we compute a defect rate, not a point score.
            config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
            config.setPopulationSize(DEFAULT_POPULATION);
            config.setFitnessFunction(generateFitnessFunction());
            config.setStrictProgramCreation(true);
        } catch (InvalidConfigurationException e) {
            abort(e, "Invalid Configurations.");
        }

        return config;
    }

    /**
     * Generate the fitness function from the file that contains input and output values.
     */
    private static GPFitnessFunction generateFitnessFunction() {
        String trainingFilePath = ClassLoader.getSystemResource(DEFAULT_FILE).getPath();
        // parse the file into two sets of numbers as inputs and outputs
        List<Float> inputs = new ArrayList<>();
        List<Float> outputs = new ArrayList<>();

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
                inputs.add(Float.parseFloat(values[0]));
                outputs.add(Float.parseFloat(values[1]));
            }

        } catch (FileNotFoundException e) {
            abort(e, "File Not Found.");
        }

        return new SymbolicRegressionFitnessFunction(inputs, outputs);
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
