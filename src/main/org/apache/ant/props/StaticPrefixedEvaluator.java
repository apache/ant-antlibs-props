package org.apache.ant.props;

/**
 * PrefixedPropertyEvaluator that always uses the same prefix.
 */
public abstract class StaticPrefixedEvaluator extends PrefixedEvaluator {
    private String prefix;

    /**
     * Create a new StaticPrefixedEvaluator.
     */
    protected StaticPrefixedEvaluator() {
    }

    /**
     * Create a new StaticPrefixedEvaluator.
     * @param prefix
     */
    protected StaticPrefixedEvaluator(String prefix) {
        setPrefix(prefix);
    }

    /**
     * Create a new StaticPrefixedEvaluator.
     * @param prefix
     * @param delimiter
     */
    protected StaticPrefixedEvaluator(String prefix, String delimiter) {
        super(delimiter);
        setPrefix(prefix);
    }

    /**
     * {@inheritDoc}
     * @see org.apache.ant.props.PrefixedEvaluator#canInterpret(java.lang.String)
     */
    protected final boolean canInterpret(String prefix) {
        return getRequiredPrefix().equals(prefix);
    }

    /**
     * Get the non-null prefix.
     * @return String
     */
    protected String getRequiredPrefix() {
        String result = getPrefix();
        if (result == null) {
            throw new IllegalStateException("prefix unset");
        }
        return result;
    }

    /**
     * Get the String prefix.
     * @return String
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set the String prefix.
     * @param prefix String
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
