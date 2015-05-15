/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package rapaio.ml.classifier.bayes;

import rapaio.WS;
import rapaio.core.distributions.Distribution;
import rapaio.core.distributions.Normal;
import rapaio.core.distributions.empirical.KDE;
import rapaio.core.distributions.empirical.KFunc;
import rapaio.core.distributions.empirical.KFuncGaussian;
import rapaio.core.stat.Mean;
import rapaio.core.stat.Variance;
import rapaio.core.tools.DVector;
import rapaio.data.Frame;
import rapaio.data.Var;
import rapaio.ml.classifier.AbstractClassifier;
import rapaio.ml.classifier.CFit;
import rapaio.ml.common.Capabilities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * Naive Bayes Classifier.
 * <p>
 * The base assumption for naive bayes is that all the variables are
 * independent. Thus, the joint distribution
 *
 * @author <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a>
 */
public class NaiveBayesClassifier extends AbstractClassifier {

    private static final long serialVersionUID = -7602854063045679683L;

    // algorithm parameters

    private boolean useLaplaceSmoother = true;
    private NumericEstimator numEstimator = new Gaussian();
    private NominalEstimator nomEstimator = new Multinomial();

    // prediction artifacts

    private Map<String, Double> priors;
    private Map<String, NumericEstimator> numericEstimatorMap;
    private Map<String, NominalEstimator> nominalEstimatorMap;

    @Override
    public NaiveBayesClassifier newInstance() {
        return new NaiveBayesClassifier().withNumEstimator(numEstimator).withNomEstimator(nomEstimator).withDebug(debug);
    }

    @Override
    public String name() {
        return "NaiveBayes";
    }

    @Override
    public String fullName() {
        return name() + "(numEstimator=" + numEstimator.name() + ",nomEstimator=" + nomEstimator.name() + ")";
    }

    @Override
    public Capabilities capabilities() {
        return new Capabilities().withTargetCount(Capabilities.TargetCount.MULTIPLE_TARGETS).withTargetType(Capabilities.TargetType.MULTICLASS_CLASSIFIER);
    }

    @Override
    public NaiveBayesClassifier withDebug(boolean debug) {
        return (NaiveBayesClassifier) super.withDebug(debug);
    }

    public NaiveBayesClassifier withNumEstimator(NumericEstimator numEstimator) {
        this.numEstimator = numEstimator;
        return this;
    }

    public NaiveBayesClassifier withNomEstimator(NominalEstimator nomEstimator) {
        this.nomEstimator = nomEstimator;
        return this;
    }

    @Override
    public void learn(Frame df, Var weights, String... targetVarNames) {

        prepareLearning(df, weights, targetVarNames);

        // build priors

        priors = new HashMap<>();
        DVector dv = DVector.newFromWeights(df.var(firstTargetName()), weights, firstDict());

        // laplace add-one smoothing
        for (int i = 0; i < firstDict().length; i++) {
            dv.increment(i, 1.0);
        }
        dv.normalize(false);
        for (int i = 1; i < firstDict().length; i++) {
            priors.put(firstDict()[i], dv.get(i));
        }

        // build conditional probabilities

        nominalEstimatorMap = new ConcurrentHashMap<>();
        numericEstimatorMap = new ConcurrentHashMap<>();

        if (debug) {
            WS.println("start learning...");
        }
        Arrays.stream(df.varNames()).parallel().forEach(
                testCol -> {
                    if (firstTargetName().equals(testCol)) {
                        return;
                    }
                    if (df.var(testCol).type().isNumeric()) {
                        NumericEstimator estimator = numEstimator.newInstance();
                        estimator.learn(df, firstTargetName(), testCol);
                        numericEstimatorMap.put(testCol, estimator);
                        WS.print(".");
                        return;
                    }
                    if (df.var(testCol).type().isNominal()) {
                        NominalEstimator estimator = nomEstimator.newInstance();
                        estimator.learn(df, firstTargetName(), testCol);
                        nominalEstimatorMap.put(testCol, estimator);
                        WS.print(".");
                    }
                });
        WS.println();
    }

    @Override
    public CFit fit(Frame df, final boolean withClasses, final boolean withDensities) {

        if (debug)
            WS.println("start fitting values...");

        CFit pred = CFit.newEmpty(this, df, withClasses, withDensities);
        pred.addTarget(firstTargetName(), firstDict());

        IntStream.range(0, df.rowCount()).parallel().forEach(
                i -> {
                    DVector dv = DVector.newEmpty(firstDict());
                    for (int j = 1; j < firstDict().length; j++) {
                        double sumLog = Math.log(priors.get(firstDictTerm(j)));
                        for (String testCol : numericEstimatorMap.keySet()) {
                            if (df.missing(i, testCol))
                                continue;
                            sumLog += numericEstimatorMap.get(testCol).cpValue(df.value(i, testCol), firstDictTerm(j));
                        }
                        for (String testCol : nominalEstimatorMap.keySet()) {
                            if (df.missing(i, testCol))
                                continue;
                            sumLog += nominalEstimatorMap.get(testCol).cpValue(df.label(i, testCol), firstDictTerm(j));
                        }
                        dv.increment(j, Math.exp(sumLog));
                    }
                    dv.normalize(false);

                    if (withClasses) {
                        pred.firstClasses().setIndex(i, dv.findBestIndex(false));
                    }
                    if (withDensities) {
                        for (int j = 0; j < firstDict().length; j++) {
                            pred.firstDensity().setValue(i, j, dv.get(j));
                        }
                    }
                });
        return pred;
    }

    @Override
    public void buildPrintSummary(StringBuilder sb) {
        // TODO not implemented yet
        throw new IllegalArgumentException("not implemented yet");
    }

    public interface NominalEstimator extends Serializable {

        String name();

        void learn(Frame df, String targetCol, String testCol);

        double cpValue(String testLabel, String classLabel);

        NominalEstimator newInstance();
    }

    public interface NumericEstimator extends Serializable {

        String name();

        void learn(Frame df, String targetCol, String testCol);

        double cpValue(double testValue, String classLabel);

        NumericEstimator newInstance();
    }

    public static class Gaussian implements NumericEstimator {

        private static final long serialVersionUID = -5974296887792054267L;

        private final Map<String, Normal> normals = new HashMap<>();

        @Override
        public String name() {
            return "Gaussian";
        }

        @Override
        public void learn(Frame df, String targetCol, String testCol) {
            normals.clear();
            for (String label : df.var(targetCol).dictionary()) {
                if ("?".equals(label)) {
                    continue;
                }
                Var v = df.stream().filter(s -> label.equals(s.label(targetCol))).toMappedFrame().var(testCol);
                double mu = new Mean(v).value();
                double sd = new Variance(v).sdValue();
                normals.put(label, new Normal(mu, sd));
            }
        }

        @Override
        public double cpValue(double testValue, String classLabel) {
            Distribution normal = normals.get(classLabel);
            if (Math.abs(normal.var()) < 1e-20) {
                if (Math.abs(normal.mean() - testValue) < 1e-20) {
                    return Double.MAX_VALUE;
                } else {
                    return 0;
                }
            }
            return normals.get(classLabel).pdf(testValue);
        }

        @Override
        public NumericEstimator newInstance() {
            return new Gaussian();
        }
    }

    public static class EmpiricKDE implements NumericEstimator {

        private static final long serialVersionUID = 7974390604811353859L;

        private Map<String, KDE> kde = new ConcurrentHashMap<>();
        private KFunc kfunc = new KFuncGaussian();
        private double bandwidth = 0;

        public EmpiricKDE() {
        }

        public EmpiricKDE(KFunc kfunc) {
            this.kfunc = kfunc;
        }


        public EmpiricKDE(KFunc kfunc, double bandwidth) {
            this.kfunc = kfunc;
            this.bandwidth = bandwidth;
        }

        @Override
        public String name() {
            return "EmpiricKDE";
        }

        @Override
        public void learn(Frame df, String targetCol, String testCol) {
            kde.clear();
            Arrays.stream(df.var(targetCol).dictionary()).forEach(
                    classLabel -> {
                        if ("?".equals(classLabel))
                            return;
                        Frame cond = df.stream().filter(s -> classLabel.equals(s.label(targetCol))).toMappedFrame();
                        Var v = cond.var(testCol);
                        KDE k = new KDE(v, kfunc, (bandwidth == 0) ? KDE.getSilvermanBandwidth(v) : bandwidth);
                        kde.put(classLabel, k);
                    });
        }

        @Override
        public double cpValue(double testValue, String classLabel) {
            return kde.get(classLabel).pdf(testValue);
        }

        @Override
        public NumericEstimator newInstance() {
            return new EmpiricKDE(kfunc, bandwidth);
        }
    }

    public static class Multinomial implements NominalEstimator {

        private static final long serialVersionUID = 3019563706421891472L;
        private double[][] density;
        private Map<String, Integer> invTreeTarget;
        private Map<String, Integer> invTreeTest;

        @Override
        public String name() {
            return "Multinomial";
        }

        @Override
        public void learn(Frame df, String targetCol, String testCol) {

            String[] targetDict = df.var(targetCol).dictionary();
            String[] testDict = df.var(testCol).dictionary();

            invTreeTarget = new HashMap<>();
            invTreeTest = new HashMap<>();

            for (int i = 0; i < targetDict.length; i++) {
                invTreeTarget.put(targetDict[i], i);
            }
            for (int i = 0; i < testDict.length; i++) {
                invTreeTest.put(testDict[i], i);
            }

            density = new double[targetDict.length][testDict.length];
            for (int i = 0; i < targetDict.length; i++) {
                for (int j = 0; j < testDict.length; j++) {
                    density[i][j] = 1.0;
                }
            }
            df.stream().forEach(s -> density[s.index(targetCol)][s.index(testCol)]++);
            for (int i = 0; i < targetDict.length; i++) {
                double t = 0;
                for (int j = 0; j < testDict.length; j++) {
                    t += density[i][j];
                }
                for (int j = 0; j < testDict.length; j++) {
                    density[i][j] /= t;
                }
            }
        }

        @Override
        public double cpValue(String testLabel, String classLabel) {
            if (!invTreeTarget.containsKey(classLabel)) {
                return 1e-10;
            }
            if (!invTreeTest.containsKey(testLabel)) {
                return 1e-10;
            }
            return density[invTreeTarget.get(classLabel)][invTreeTest.get(testLabel)];
        }

        @Override
        public NominalEstimator newInstance() {
            return new Multinomial();
        }
    }
}
