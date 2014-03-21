/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
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
 */

package rapaio.classifier;

import rapaio.core.Printable;
import rapaio.data.Frame;
import rapaio.data.Nominal;
import rapaio.data.Numeric;

/**
 * Interface for all classification model algorithms.
 * A classifier is able to classify only one target columns.
 * If a classifier implements multiple target learning it has to implement
 * {@link rapaio.classifier.MultiClassifier}
 * If a classifier implements further learning it has to implement
 * {@link rapaio.classifier.FurtherClassifier}
 *
 * @author <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a>
 */
public interface Classifier extends Printable {

    /**
     * Creates a new classifier instance with the same parameters as the original.
     * The fitted model and other artifacts are not replicated.
     *
     * @return new parametrized instance
     */
    Classifier newInstance();

    /**
     * Fit a classifier on instances specified by frame, with row weights
     * and target as targetName.
     *
     * @param df         data set instances
     * @param weights    row weights
     * @param targetName target column name
     */
    void learn(Frame df, Numeric weights, String targetName);

    /**
     * Fit a classifier on instances specified by frame, with row weights
     * equal to 1 and target as classColName.
     *
     * @param df            data set instances
     * @param targetColName target column name
     */
    default void learn(Frame df, String targetColName) {
        Numeric weights = new Numeric(df.rowCount());
        weights.stream().transformValue(x -> 1.0);
        learn(df, weights, targetColName);
    }

    /**
     * Predict classes for new data set instances
     *
     * @param df data set instances
     */
    void predict(Frame df);

    /**
     * Returns predicted target classes as a nominal vector
     *
     * @return nominal vector with predicted classes
     */
    Nominal prediction();

    /**
     * Returns predicted class distribution if is computed,
     * otherwise returns null.
     *
     * @return predicted class distribution (frame with one
     * column for each target class, including missing getValue)
     */
    Frame distribution();
}