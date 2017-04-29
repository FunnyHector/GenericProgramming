package classification;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

import java.util.List;

/**
 * This class represents the fitness function used in classification problem.
 */
public class ClassificationFitnessFunction extends GPFitnessFunction {

    private List<CancerInstance> trainingSet;

    /**
     * Constructor
     *
     * @param trainingSet
     */
    public ClassificationFitnessFunction(List<CancerInstance> trainingSet) {
        this.trainingSet = trainingSet;
    }

    @Override
    protected double evaluate(IGPProgram gpProgram) {
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

        // F1 measure is used here
        float truePositive = 0;
        float falsePositive = 0;
        float falseNegative = 0;

        // check the performance against every instance from training set
        for (CancerInstance cancerInstance : trainingSet) {
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

            // if the programme produce NaN, just return it, as NaN programmes will be least favoured by the selector
            if (Double.isNaN(result)) {
                return result;
            }

            // here let's say being malignant is a true estimation
            if (result >= 0 && cancerInstance.isMalignant()) {
                truePositive++;
            } else if (result >= 0 && cancerInstance.isBenign()) {
                falsePositive++;
            } else if (result < 0 && cancerInstance.isMalignant()) {
                falseNegative++;
            }
        }

        // precision and recall rate
        float precision = truePositive / (truePositive + falsePositive);
        float recall = truePositive / (truePositive + falseNegative);

        // use 0 if it's NaN
        precision = Float.isNaN(precision) ? 0 : precision;
        recall = Float.isNaN(recall) ? 0 : recall;

        return 100 * (2.0f * precision * recall) / (precision + recall);
    }
}
