package symbolicRegression;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

import java.util.List;

/**
 * This class represents the fitness function used in Symbolic regression.
 */
public class SymbolicRegressionFitnessFunction extends GPFitnessFunction {

    private List<Float> inputs;
    private List<Float> outputs;

    /**
     * Constructor
     *
     * @param inputs
     * @param outputs
     */
    public SymbolicRegressionFitnessFunction(List<Float> inputs, List<Float> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    protected double evaluate(IGPProgram gpProgram) {
        Variable variable = gpProgram.getGPConfiguration().getVariable(SymbolicRegressionProblem.VARIABLE_NAME);
        double error = 0.0f;

        // Evaluate function for each input
        for (int i = 0; i < inputs.size(); i++) {
            // let the variable X be the input
            float input = inputs.get(i);
            variable.set(input);

            try {
                // Execute the GP program representing the function to be evolved
                double result = gpProgram.execute_float(0, null);
                // Sum up the absolute error between actual and expected result
                error += Math.abs(result - outputs.get(i));
                // If the error is too high, stop evaluation and return worst error possible.
                if (Double.isInfinite(error)) {
                    return Double.MAX_VALUE;
                }
            } catch (ArithmeticException e) {
                // This should not happen, some illegal operation was executed.
                // ------------------------------------------------------------
                System.out.println("x = " + input);
                System.out.println(gpProgram);
                throw e;
            }
        }

        // if the error is small enough, consider it perfect.
        if (error < 0.001) {
            error = 0.0d;
        }

        return error;
    }
}
