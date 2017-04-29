package symbolicRegression;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.function.*;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

/**
 * This class represents a symbolic regression problem.
 */
public class SymbolicRegressionProblem extends GPProblem {

    /**
     * The variable name used in the genetic programme
     */
    public static final String VARIABLE_NAME = "X";

    private static final double MIN_TERMINAL = 0.0d;
    private static final double MAX_TERMINAL = 100.0d;

    /**
     * Constructor
     *
     * @param configuration
     * @throws InvalidConfigurationException
     */
    public SymbolicRegressionProblem(GPConfiguration configuration) throws InvalidConfigurationException {
        super(configuration);
    }

    @Override
    public GPGenotype create() throws InvalidConfigurationException {
        GPConfiguration configuration = getGPConfiguration();

        // define the return type of the GP program.
        Class[] types = { CommandGene.FloatClass };

        // define the arguments of the GP parts.
        Class[][] argTypes = { { } };

        // define the set of available GP commands and terminals to use.
        CommandGene[][] nodeSets = {
                {
                        // the variable node
                        Variable.create(configuration, VARIABLE_NAME, CommandGene.FloatClass),

                        // a constant
                        new Terminal(configuration, CommandGene.FloatClass, MIN_TERMINAL, MAX_TERMINAL, true),

                        // functions. Here "+", "-", "*", "/" are used
                        new Add(configuration, CommandGene.FloatClass),
                        new Subtract(configuration, CommandGene.FloatClass),
                        new Multiply(configuration, CommandGene.FloatClass),
                        new Divide(configuration, CommandGene.FloatClass)
                }
        };

        // create genotype with initial population
        return GPGenotype.randomInitialGenotype(configuration, types, argTypes, nodeSets, 40, true);
    }
}
