/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
 *    Copyright 2016 Aurelian Tutuianu
 *    Copyright 2017 Aurelian Tutuianu
 *    Copyright 2018 Aurelian Tutuianu
 *    Copyright 2019 Aurelian Tutuianu
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

package rapaio.experiment.ml.eval;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import rapaio.core.*;
import rapaio.core.stat.*;
import rapaio.data.*;
import rapaio.data.filter.frame.*;
import rapaio.ml.classifier.*;
import rapaio.ml.eval.*;
import rapaio.printer.*;
import rapaio.printer.format.*;
import rapaio.printer.idea.*;
import rapaio.sys.*;
import rapaio.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static rapaio.graphics.Plotter.*;
import static rapaio.sys.WS.*;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
@Deprecated
public class CEvaluation {

    public static double cv(Frame df, String classColName, Classifier c, int folds) {
        print("\nCrossValidation with " + folds + " folds\n");

        List<IntList> strata = buildStrata(df, folds, classColName);
        VarDouble acc = VarDouble.empty();

        for (int i = 0; i < folds; i++) {
            Mapping trainMapping = Mapping.empty();
            Mapping testMapping = Mapping.empty();
            for (int j = 0; j < folds; j++) {
                if (j == i) {
                    testMapping.addAll(strata.get(j));
                } else {
                    trainMapping.addAll(strata.get(j));
                }
            }
            Frame train = MappedFrame.byRow(df, trainMapping);
            Frame test = MappedFrame.byRow(df, testMapping);

            Classifier cc = c.newInstance();
            cc.fit(train, classColName);
            CPrediction cp = cc.predict(test);

            Confusion conf = new Confusion(test.rvar(classColName), cp.firstClasses());
            acc.addDouble(conf.accuracy());
            print(String.format("CV %2d:  acc=%.6f, mean=%.6f, se=%.6f\n", i + 1,
                    conf.accuracy(),
                    Mean.of(acc).value(),
                    Variance.of(acc).sdValue()));
        }

        double correct = Mean.of(acc).value();
        print("==============\n");
        print(String.format("Mean accuracy:%.6f\n", correct));
        print(String.format("SE: %.6f     (Standard error)\n", Variance.of(acc).sdValue()));
        return correct;
    }

    private static List<IntList> buildStrata(Frame df, int folds, String classColName) {
        List<String> dict = df.rvar(classColName).levels();
        List<IntList> rows = new ArrayList<>();
        for (int i = 0; i < dict.size(); i++) {
            rows.add(new IntArrayList());
        }
        for (int i = 0; i < df.rowCount(); i++) {
            rows.get(df.getInt(i, classColName)).add(i);
        }
        IntList shuffle = new IntArrayList();
        for (int i = 0; i < dict.size(); i++) {
            Collections.shuffle(rows.get(i), RandomSource.getRandom());
            shuffle.addAll(rows.get(i));
        }
        List<IntList> strata = new ArrayList<>();
        for (int i = 0; i < folds; i++) {
            strata.add(new IntArrayList());
        }
        int fold = 0;
        for (int next : shuffle) {
            strata.get(fold).add(next);
            fold++;
            if (fold == folds) {
                fold = 0;
            }
        }
        return strata;
    }

    public static void multiCv(Frame df, String classColName, List<Classifier> classifiers, int folds) {
        print("CrossValidation with " + folds + " folds\n");
        df = df.fapply(FShuffle.filter());
        double[] tacc = new double[classifiers.size()];

        for (int i = 0; i < folds; i++) {
            IntArrayList trainMapping = new IntArrayList();
            IntArrayList testMapping = new IntArrayList();
            if (folds >= df.rowCount() - 1) {
                testMapping.add(i);
                for (int j = 0; j < df.rowCount(); j++) {
                    if (j != i) {
                        trainMapping.add(j);
                    }
                }

            } else {
                for (int j = 0; j < df.rowCount(); j++) {
                    if (j % folds == i) {
                        testMapping.add(j);
                    } else {
                        trainMapping.add(j);
                    }
                }
            }
            Frame train = MappedFrame.byRow(df, trainMapping.toIntArray());
            Frame test = MappedFrame.byRow(df, testMapping.toIntArray());

            for (int k = 0; k < classifiers.size(); k++) {
                Classifier c = classifiers.get(k).newInstance();
                c.fit(train, classColName);
                CPrediction cp = c.predict(test);
                Confusion cm = new Confusion(test.rvar(classColName), cp.firstClasses());
                double acc = cm.accuracy();
                tacc[k] += acc;
                print(String.format("CV %d, accuracy:%.6f, classifier:%s\n", i + 1, acc, c.name()));
            }
            print("-----------\n");

        }

        for (int k = 0; k < classifiers.size(); k++) {
            tacc[k] /= (1. * folds);
            print(String.format("Mean accuracy %.6f, for classifier: %s\n", tacc[k], classifiers.get(k).name()));
        }
    }

    public static void bootstrapValidation(Printer printer, Frame df, String classColName, Classifier c, int bootstraps) {
        Var weights = VarDouble.fill(df.rowCount(), 1.0);
        bootstrapValidation(printer, df, weights, classColName, c, bootstraps, 1.0);
    }

    public static void bootstrapValidation(Printer printer, Frame df, Var weights, String classColName, Classifier c, int bootstraps) {
        bootstrapValidation(printer, df, weights, classColName, c, bootstraps, 1.0);
    }

    public static void bootstrapValidation(Printer printer, Frame df, String classColName, Classifier c, int bootstraps, double p) {
        Var weights = VarDouble.fill(df.rowCount(), 1.0d);
        bootstrapValidation(printer, df, weights, classColName, c, bootstraps, p);
    }

    public static void bootstrapValidation(Printer printer, Frame df, Var weights, String classColName, Classifier c, int bootstraps, double p) {
        print(bootstraps + " bootstrap evaluation\n");
        double total = 0;
        double count = 0;
        for (int i = 0; i < bootstraps; i++) {
//            System.out.println("get sample...");
            int[] rows = SamplingTools.sampleWR(df.rowCount(), (int) (df.rowCount() * p));
//            System.out.println("build predict set ...");
            Frame train = df.mapRows(rows);
//            System.out.println("build test set ...");
            Frame test = df.removeRows(rows);
//            System.out.println("learn predict set ...");
            Classifier cc = c.newInstance();
            cc.fit(train, weights.mapRows(rows), classColName);
//            System.out.println("predict test cases ...");
            Var classes = cc.predict(test).firstClasses();
//            System.out.println("build confusion matrix ...");
            Confusion cm = new Confusion(test.rvar(classColName), classes);
            printer.printSummary(cm);
            double acc = cm.accuracy();
            System.out.println(String.format("bootstrap(%d) : %.6f", i + 1, acc));
            total += acc;
            count++;
            System.out.flush();
        }
        System.out.println(String.format("Average accuracy: %.6f", total / count));
    }

    public static PlotRunResult plotRunsAcc(Frame train, Frame test, String targetVar, Classifier c, int runs, int step) {

        BiConsumer<Classifier, Integer> oldHook = c.runningHook();
        VarInt r = VarInt.empty().withName("runs");
        VarDouble testAcc = VarDouble.empty().withName("test");
        VarDouble trainAcc = VarDouble.empty().withName("predict");
        c.withRunningHook((cs, run) -> {

            if (run % step != 0) {
                return;
            }
            r.addInt(run);
            testAcc.addDouble(new Confusion(test.rvar(targetVar), c.predict(test).firstClasses()).accuracy());
            trainAcc.addDouble(new Confusion(train.rvar(targetVar), c.predict(train).firstClasses()).accuracy());

            WS.setPrinter(new IdeaPrinter());
            WS.draw(plot()
                    .lines(r, testAcc, color(1))
                    .lines(r, trainAcc, color(2))
            );
        });
        c.withRuns(runs);
        c.fit(train, targetVar);

        WS.println("Confusion matrix on training data set: ");
        Confusion trainConfusion = new Confusion(train.rvar(targetVar), c.predict(train).firstClasses());
        trainConfusion.printSummary();
        WS.println();
        WS.println("Confusion matrix on test data set: ");
        Confusion testConfusion = new Confusion(test.rvar(targetVar), c.predict(test).firstClasses());
        testConfusion.printSummary();

        return new PlotRunResult(r, trainAcc, testAcc, testConfusion, trainConfusion);
    }

    public static PlotRunResult plotRunsRoc(
            Frame train,
            Frame test,
            String targetVar,
            String label,
            Classifier cc,
            int runs,
            int step,
            boolean alterClassifier) {

        Classifier c = alterClassifier ? cc : cc.newInstance();
        BiConsumer<Classifier, Integer> oldHook = c.runningHook();
        VarInt r = VarInt.empty().withName("runs");
        VarDouble testAuc = VarDouble.empty().withName("test");
        VarDouble trainAuc = VarDouble.empty().withName("predict");
        Pin<Double> prevAuc = new Pin<>(0.0);
        c.withRunningHook((cs, run) -> {

            if ((run % step != 0) && run != 1) {
                return;
            }
            r.addInt(run);
            ROC roc = ROC.from(c.predict(test).firstDensity().rvar(label), test.rvar(targetVar), label);
            WS.draw(rocCurve(roc).title("testAuc: " + Format.floatFlex(roc.auc()) + ", run: " + run));
            testAuc.addDouble(roc.auc());
            WS.println("testAuc: " + Format.floatLong(roc.auc()) + ", run: " + run + ", auc gain: " + Format.floatLong(roc.auc()-prevAuc.get()));
            prevAuc.set(roc.auc());
//            trainAuc.addValue(new ROC(c.predict(predict).firstDensity().rvar(label), predict.rvar(targetVar), label).auc());

//            WS.draw(plot()
//                            .lines(r, testAuc, color(1))
//                            .title("testAuc: " + Format.floatFlex(testAuc.value(testAuc.rowCount() - 1)))
//            );
        });
        c.withRuns(runs);
        c.fit(train, targetVar);

//        WS.println("Confusion matrix on training data set: ");
        Confusion trainConfusion = new Confusion(train.rvar(targetVar), c.predict(train).firstClasses());
        trainConfusion.printSummary();
//        WS.println();
        WS.println("Confusion matrix on test data set: ");
        Confusion testConfusion = new Confusion(test.rvar(targetVar), c.predict(test).firstClasses());
        testConfusion.printSummary();


        return new PlotRunResult(r, trainAuc, testAuc, testConfusion, trainConfusion);
    }

    public static class PlotRunResult {
        public final Var runs;
        public final Var trainAcc;
        public final Var testAcc;
        public final Confusion testConfusion;
        public final Confusion trainConfusion;

        public PlotRunResult(Var runs, Var trainAcc, Var testAcc, Confusion testConfusion, Confusion trainConfusion) {
            this.runs = runs;
            this.trainAcc = trainAcc;
            this.testAcc = testAcc;
            this.testConfusion = testConfusion;
            this.trainConfusion = trainConfusion;
        }
    }
}
