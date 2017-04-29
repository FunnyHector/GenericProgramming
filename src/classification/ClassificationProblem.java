package classification;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPProblem;
import org.jgap.gp.function.*;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

/**
 * This class represents a classification problem.
 */
public class ClassificationProblem extends GPProblem {

    public static final String FEATURE_1 = "CT";
    public static final String FEATURE_2 = "USz";
    public static final String FEATURE_3 = "UShp";
    public static final String FEATURE_4 = "MA";
    public static final String FEATURE_5 = "SESz";
    public static final String FEATURE_6 = "BN";
    public static final String FEATURE_7 = "BC";
    public static final String FEATURE_8 = "NN";
    public static final String FEATURE_9 = "M";

    private static final double MIN_TERMINAL = 0.0d;
    private static final double MAX_TERMINAL = 100.0d;

    /**
     * Constructor
     *
     * @param configuration
     * @throws InvalidConfigurationException
     */
    public ClassificationProblem(GPConfiguration configuration) throws InvalidConfigurationException {
        super(configuration);
    }

    @Override
    public GPGenotype create() throws InvalidConfigurationException {
        GPConfiguration configuration = getGPConfiguration();

        // define the return type of the GP program.
        Class[] types = { CommandGene.FloatClass };

        // define the arguments of the GP parts.
        Class[][] argTypes = { {} };

        // define the set of available GP commands and terminals to use.
        CommandGene[][] nodeSets = {
                {
                        // 9 variable nodes for 9 features
                        Variable.create(configuration, FEATURE_1, CommandGene.FloatClass),
                        Variable.create(configuration, FEATURE_2, CommandGene.FloatClass),
                        Variable.create(configuration, FEATURE_3, CommandGene.FloatClass),
                        Variable.create(configuration, FEATURE_4, CommandGene.FloatClass),
                        Variable.create(configuration, FEATURE_5, CommandGene.FloatClass),
                        Variable.create(configuration, FEATURE_6, CommandGene.FloatClass),
                        Variable.create(configuration, FEATURE_7, CommandGene.FloatClass),
                        Variable.create(configuration, FEATURE_8, CommandGene.FloatClass),
                        Variable.create(configuration, FEATURE_9, CommandGene.FloatClass),

                        // one or more constant
                        new Terminal(configuration, CommandGene.FloatClass, MIN_TERMINAL, MAX_TERMINAL, true),
                        new Terminal(configuration, CommandGene.FloatClass, MIN_TERMINAL, MAX_TERMINAL, true),
                        // new Terminal(configuration, CommandGene.FloatClass, MIN_TERMINAL, MAX_TERMINAL, true),

                        // functions. Here "+", "-", "*", "/" are used
                        new Add(configuration, CommandGene.FloatClass),
                        new Subtract(configuration, CommandGene.FloatClass),
                        new Multiply(configuration, CommandGene.FloatClass),
                        new Divide(configuration, CommandGene.FloatClass),

                        // more functions for experiment
                        // new Cosine(configuration, CommandGene.FloatClass),
                        // new Sine(configuration, CommandGene.FloatClass),
                        // new Exp(configuration, CommandGene.FloatClass),
                        // new Log(configuration, CommandGene.FloatClass),
                        // new Pow(configuration, CommandGene.FloatClass),
                        // new Tangent(configuration, CommandGene.FloatClass)
                }
        };

        // create genotype with initial population
        return GPGenotype.randomInitialGenotype(configuration, types, argTypes, nodeSets, 40, true);
    }
}
