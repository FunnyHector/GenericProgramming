package classification;

import org.apache.log4j.PropertyConfigurator;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Variable;

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
        // 0. initialise log4j
        PropertyConfigurator.configure("log4j.properties");

        // 1. read in all instances
        List<CancerInstance> instances = DataProcessor.readInData();

        // 2. split the instances into a training set and a test set
        List<CancerInstance>[] twoLists = DataProcessor.analyseAndSplitData(instances);
        trainingSet = twoLists[0];
        testSet = twoLists[1];

        // 3. initialise a configuration object
        GPConfiguration configuration = setConfiguration();

        try {
            // 4. initialise the GP problem
            GPProblem problem = new ClassificationProblem(configuration);
            GPGenotype gp = problem.create();
            gp.setVerboseOutput(true);

            // 5. evolve
            gp.evolve(DEFAULT_NUM_EVOLUTIONS);

            // 6. show results to the console and generate the png image file
            IGPProgram bestProgramme = gp.getAllTimeBest();
            gp.outputSolution(bestProgramme);
            problem.showTree(bestProgramme, "classification_tree.png");

            // 7. My own output
            // System.out.println("====================================");
            // System.out.println("The best 3 programmes:");

            // @SuppressWarnings("unchecked")
            // List<IGPProgram> threeBest = (List<IGPProgram>) gp.getGPPopulation().determineFittestChromosomes(3);

            // threeBest.forEach(program -> {
            //     System.out.println(" - Programme: " + program.toStringNorm(0));
            //     System.out.println("   Fitness :" + program.getFitnessValue());
            // });

            // 8. Test the accuracy with the best solution
            float accuracyTraining = checkAccuracy(bestProgramme, trainingSet);
            float accuracyTest = checkAccuracy(bestProgramme, testSet);

            System.out.println("====================================");
            System.out.println(String.format("The accuracy on training set: %.2f", accuracyTraining * 100));
            System.out.println(String.format("The accuracy on test set: %.2f", accuracyTest * 100));

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
     * Check the accuracy of the given programme on given instance set. The accuracy is measured by the
     * percentage of correctly classified instances
     *
     * @param gpProgram
     * @param instances
     * @return
     */
    private static float checkAccuracy(IGPProgram gpProgram, List<CancerInstance> instances) {
        // 9 variables
        Variable feature1 = gpProgram.getGPConfiguration().getVariable(ClassificationProblem.FEATURE_1);
        Variable feature2 = gpProgram.getGPConfiguration().getVariable(ClassificationProblem.FEATURE_2);
        Variable feature3 = gpProgram.getGPConfiguration().getVariable(ClassificationProblem.FEATURE_3);
        Variable feature4 = gpProgram.getGPConfiguration().getVariable(ClassificationProblem.FEATURE_4);
        Variable feature5 = gpProgram.getGPConfiguration().getVariable(ClassificationProblem.FEATURE_5);
        Variable feature6 = gpProgram.getGPConfiguration().getVariable(ClassificationProblem.FEATURE_6);
        Variable feature7 = gpProgram.getGPConfiguration().getVariable(ClassificationProblem.FEATURE_7);
        Variable feature8 = gpProgram.getGPConfiguration().getVariable(ClassificationProblem.FEATURE_8);
        Variable feature9 = gpProgram.getGPConfiguration().getVariable(ClassificationProblem.FEATURE_9);

        // accuracy is measured by the percentage of correctly classified instances
        int totalNum = instances.size();
        float numCorrect = 0.0f;

        // check the performance against every instance from training set
        for (CancerInstance cancerInstance : instances) {
            feature1.set((float) cancerInstance.features[0]);
            feature2.set((float) cancerInstance.features[1]);
            feature3.set((float) cancerInstance.features[2]);
            feature4.set((float) cancerInstance.features[3]);
            feature5.set((float) cancerInstance.features[4]);
            feature6.set((float) cancerInstance.features[5]);
            feature7.set((float) cancerInstance.features[6]);
            feature8.set((float) cancerInstance.features[7]);
            feature9.set((float) cancerInstance.features[8]);

            double result = gpProgram.execute_float(0, null);

            // count how many correctly classified instances
            if ((result >= 0 && cancerInstance.isMalignant())
                    || (result < 0 && cancerInstance.isBenign())) {
                numCorrect++;
            }
        }

        return numCorrect / totalNum;
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
