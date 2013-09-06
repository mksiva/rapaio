package rapaio.supervised;

import rapaio.data.Frame;
import rapaio.data.MappedFrame;

import java.util.ArrayList;
import java.util.List;

import static rapaio.core.BaseFilters.shuffle;
import static rapaio.explore.Workspace.code;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class CrossValidation {

    public void cv(Frame df, int classIndex, Classifier c, int folds) {
        StringBuilder sb = new StringBuilder();
        sb.append("CrossValidation with ").append(folds).append(" folds\n");
        Frame f = shuffle(df);
        ClassifierResult[] results = new ClassifierResult[folds];

        for (int i = 0; i < folds; i++) {
            List<Integer> trainMapping = new ArrayList<>();
            List<Integer> testMapping = new ArrayList<>();
            for (int j = 0; j < f.getRowCount(); j++) {
                if (j % folds == i) {
                    testMapping.add(j);
                } else {
                    trainMapping.add(j);
                }
            }
            Frame train = new MappedFrame(f, trainMapping);
            Frame test = new MappedFrame(f, testMapping);

            c.learn(train, classIndex);
            results[i] = c.predict(test);
        }

        double tacc = 0;
        for (int i = 0; i < folds; i++) {
            ClassifierResult cr = results[i];
            double acc = 0;
            for (int j = 0; j < cr.getClassification().getRowCount(); j++) {
                if (cr.getClassification().getIndex(j) == cr.getTestFrame().getCol(classIndex).getIndex(j)) {
                    acc++;
                }
            }
            acc /= (1. * cr.getClassification().getRowCount());
            tacc += acc;
            sb.append(String.format("CV %d, accuracy:%.6f\n", i + 1, acc));
        }
        tacc /= (1. * folds);
        sb.append(String.format("Mean accuracy:%.6f\n", tacc));
        code(sb.toString());
    }
}
