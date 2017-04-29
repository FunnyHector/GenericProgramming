package classification;

import org.apache.log4j.PropertyConfigurator;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;

import java.util.List;

/**
 * The entry class (where main function is) for classification programme.
 */
public class ClassificationMain {

    private static final int DEFAULT_POPULATION = 1000;
    private static final int DEFAULT_NUM_EVOLUTIONS = 800;

    private static List<CancerInstance> trainingSet;
    private static List<CancerInstance> testSet;

    /**
     * Main function
     *
     * @param args
     */
    public static void main(String[] args) {
        // initialise log4j
        PropertyConfigurator.configure("log4j.properties");

        // read in all instances
        List<CancerInstance> instances = DataProcessor.readInData();

        // split the instances into a training set and a test set
        List<CancerInstance>[] twoLists = DataProcessor.analyseAndSplitData(instances);
        trainingSet = twoLists[0];
        testSet = twoLists[1];

        // initialise a configuration object
        GPConfiguration configuration = setConfiguration();

        try {
            // initialise the GP problem
            GPProblem problem = new ClassificationProblem(configuration);
            GPGenotype gp = problem.create();
            gp.setVerboseOutput(true);

            // evolve
            gp.evolve(DEFAULT_NUM_EVOLUTIONS);

            // show results to the console and generate the png image file
            IGPProgram bestProgramme = gp.getAllTimeBest();
            gp.outputSolution(bestProgramme);
            problem.showTree(bestProgramme, "classification_tree.png");

            // My own output
            System.out.println("====================================");
            System.out.println("The best 3 programmes:");

            @SuppressWarnings("unchecked")
            List<IGPProgram> threeBest = (List<IGPProgram>) gp.getGPPopulation().determineFittestChromosomes(3);

            threeBest.forEach(program -> {
                System.out.println(" - Programme: " + program.toStringNorm(0));
                System.out.println("   Fitness :" + program.getFitnessValue());
            });

        } catch (InvalidConfigurationException e) {
            abort(e, "Invalid Configuration Exception.");
        }
    }

    /**
     * Initialise a configuration object, and set parameters.
     *
     * @return
     */
    private static GPConfiguration setConfiguration() {
        GPConfiguration config = null;

        try {
            config = new GPConfiguration();

            // ================ PARAMETERS =======================
            // config.setCrossoverProb(0.9f);  // CrossoverProb + ReproductionProb = 1.0f
            // config.setReproductionProb(0.1f);
            // config.setMutationProb(0.1f);
            // config.setDynamizeArityProb(0.08f);  // The probability that the arity of a node is changed during growing a program.
            // config.setNewChromsPercent(0.3f);  // Percentage of the population that will be filled with new individuals during evolution. Must be between 0.0d and 1.0d.
            // config.setFunctionProb(0.9f);  // In crossover: If random number (0..1) < this value, then choose a function otherwise a terminal.
            // config.setMaxCrossoverDepth(17);  // The maximum depth of an individual resulting from crossover.
            // config.setMaxInitDepth(7);  // The maximum depth of an individual when the world is created.
            // config.setMinInitDepth(2);  // The minimum depth of an individual when the world is created.

            config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
            config.setPopulationSize(DEFAULT_POPULATION);
            config.setFitnessFunction(new ClassificationFitnessFunction(trainingSet));
            config.setStrictProgramCreation(true);
        } catch (InvalidConfigurationException e) {
            abort(e, "Invalid Configurations.");
        }

        return config;
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
