package com.github.tteofili.nlputils;

import java.math.BigDecimal;

/**
 * Utility class for anomaly detection
 */
public class AnomalyDetectionUtils {

  /**
   * calculate Mu distribution parameters for a {@link TrainingSet}'s set of features
   *
   * @param inputs the {@link TrainingSet} to fit
   * @return the <code>double[]</code> containing the Mu parameters for each feature
   * @throws Exception
   */
  public static double[] fitMus(TrainingSet inputs) throws Exception {
    assert inputs != null && inputs.size() > 0 : "empty dataset";
    int size = inputs.iterator().next().getInputs().length;
    double[] result = new double[size];
    for (int i = 0; i < size; i++) {
      for (TrainingExample trainingExample : inputs) {
        result[i] += trainingExample.getInputs()[i];
      }
      result[i] /= inputs.size();
    }
    return result;
  }

  /**
   * calculates (squared) standard deviation parameters for the given {@link TrainingSet}
   *
   * @param mus    mean parameters
   * @param inputs the {@link TrainingSet} to fit
   * @return the <code>double[]</code> containing the standard deviations
   * @throws Exception
   */
  public static double[] fitSigmas(double[] mus, TrainingSet inputs) throws Exception {
    assert inputs != null && inputs.size() > 0 : "empty dataset";
    int size = inputs.iterator().next().getInputs().length;
    double[] result = new double[size];
    for (int i = 0; i < size; i++) {
      for (TrainingExample trainingExample : inputs) {
        result[i] += Math.pow(trainingExample.getInputs()[i] - mus[i], 2);
      }
      result[i] /= inputs.size();
    }
    return result;
  }

  /**
   * calculate the probability of a certain input
   *
   * @param x      the input
   * @param mus    the means for the modeled features
   * @param sigmas the standard deviations for the modeled features
   * @return the probability of the given input
   */
  public static double getGaussianProbability(TrainingExample x, double[] mus, double[] sigmas) {
    assert mus.length == sigmas.length : "parameters not aligned";
    BigDecimal px = new BigDecimal(1d);
    for (int i = 0; i < mus.length; i++) {
      BigDecimal firstTerm = BigDecimal.ONE.divide(BigDecimal.valueOf(Math.sqrt(2d * Math.PI * sigmas[i])), BigDecimal.ROUND_CEILING);
      BigDecimal secondTerm = BigDecimal.valueOf(Math.exp(-1 * (Math.pow(x.getInputs()[i] - mus[i], 2) / (2 * Math.pow(sigmas[i], 2)))));
      px = px.multiply(firstTerm.multiply(secondTerm));
    }
    return px.doubleValue();
  }

}
