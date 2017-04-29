package classification;

import java.util.Arrays;

/**
 * This class represents a cancer instance
 */
public class CancerInstance {

    public static final int BENIGN_VALUE = 2;
    public static final int MALIGNANT_VALUE = 4;

    public final int id;
    public final int[] features;
    public final int label;

    /**
     * Constructor
     *
     * @param features
     * @param label
     */
    public CancerInstance(int id, int[] features, int label) {
        if (isNotSanity(features, label)) {
            throw new IllegalArgumentException("Wrong values. Label: " + label + ", Features: " + Arrays.toString(features));
        }

        this.id = id;
        this.features = features;
        this.label = label;
    }

    /**
     * Sanity check of the passed-in values
     *
     * @param features
     * @param label
     * @return
     */
    private boolean isNotSanity(int[] features, int label) {
        return features.length != 9
                || Arrays.stream(features).anyMatch(value -> value == 0 || value < -1 || value > 10)
                || (label != BENIGN_VALUE && label != MALIGNANT_VALUE);
    }

    /**
     * Whether this instance is a benign one
     *
     * @return
     */
    public boolean isBenign() {
        return label == BENIGN_VALUE;
    }

    /**
     * Whether this instance is a malignant one
     *
     * @return
     */
    public boolean isMalignant() {
        return label == MALIGNANT_VALUE;
    }

    /**
     * Returns an alternative string representation of this instance, which is used to write the instance to file
     *
     * @return
     */
    public String toStringOnFile() {
        return Arrays.stream(features)
                .mapToObj(String::valueOf)
                .reduce(String.valueOf(id), (a, b) -> (a + ",").concat(b))
                + "," + String.valueOf(label);
    }

    @Override
    public String toString() {
        return (isBenign() ? "<#Benign: " : "<#Malignant: ") + Arrays.toString(features) + ">";
    }
}
