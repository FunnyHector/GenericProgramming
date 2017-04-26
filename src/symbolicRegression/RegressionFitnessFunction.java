package symbolicRegression;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;

import java.util.List;


/*
 * TODO: need to figure out how to evaluate the fitness.
 *    vx.set(inputs.get(i));
 *
 *
 *
 */


public class RegressionFitnessFunction extends GPFitnessFunction {

    private List<Double> inputs;
    private List<Double> outputs;

    /**
     * Constructor
     *
     * @param inputs
     * @param outputs
     */
    public RegressionFitnessFunction(List<Double> inputs, List<Double> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    protected double evaluate(IGPProgram gpProgram) {
        double error = 0.0f;
        Object[] noargs = new Object[0];
        // Evaluate function for input numbers 0 to 20.
        // --------------------------------------------
        for (int i = 0; i < 20; i++) {
            // Provide the variable X with the input number.
            // See method create(), declaration of "nodeSets" for where X is
            // defined.
            // -------------------------------------------------------------
            // vx.set(inputs.get(i));
            try {
                // Execute the GP program representing the function to be evolved.
                // As in method create(), the return type is declared as float (see
                // declaration of array "types").
                // ----------------------------------------------------------------
                double result = gpProgram.execute_float(0, noargs);
                // Sum up the error between actual and expected result to get a defect
                // rate.
                // -------------------------------------------------------------------
                error += Math.abs(result - outputs.get(i));
                // If the error is too high, stop evlauation and return worst error
                // possible.
                // ----------------------------------------------------------------
                if (Double.isInfinite(error)) {
                    return Double.MAX_VALUE;
                }
            } catch (ArithmeticException ex) {
                // This should not happen, some illegal operation was executed.
                // ------------------------------------------------------------
                System.out.println("x = " + inputs.get(i).floatValue());
                System.out.println(gpProgram);
                throw ex;
            }
        }
        // In case the error is small enough, consider it perfect.
        // -------------------------------------------------------
        if (error < 0.0001) {
            error = 0.0d;
        }
        return error;



    }
}
