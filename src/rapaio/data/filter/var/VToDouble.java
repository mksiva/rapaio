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

package rapaio.data.filter.var;

import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import rapaio.data.Var;
import rapaio.data.VarDouble;
import rapaio.data.filter.VFilter;
import rapaio.data.stream.VSpot;

import java.util.function.Function;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> at 12/4/14.
 */
public class VToDouble implements VFilter {

    /**
     * Transforms a given variable into a double variable using as source
     * the default representation of the variable.
     *
     * @return new instance of double variable with transformed values
     */
    public static VToDouble byDefault() {
        return new VToDouble(new DefaultByType());
    }

    /**
     * Transforms a given variable into a double variable using the spot transformation
     * function given as parameter.
     *
     * @return new instance of double variable with transformed values
     */
    public static VToDouble bySpot(Function<VSpot, Double> fun) {
        return new VToDouble(new TransformSpot(fun));
    }

    /**
     * Transforms a given variable into a double variable using the double
     * value transformation function.
     *
     * @return new instance of double variable with transformed values
     */
    public static VToDouble byValue(Double2DoubleFunction fun) {
        return new VToDouble(new TransformDouble(fun));
    }

    /**
     * Transforms a given variable into a double variable using the integer
     * value transformation function.
     *
     * @return new instance of double variable with transformed values
     */
    public static VToDouble byInt(Int2DoubleFunction fun) {
        return new VToDouble(new TransformInt(fun));
    }

    /**
     * Transforms a given variable into a double variable using the String
     * value transformation function.
     *
     * @return new instance of double variable with transformed values
     */
    public static VToDouble byLabel(Function<String, Double> fun) {
        return new VToDouble(new TransformLabel(fun));
    }

    private static final long serialVersionUID = -6471901421507667237L;
    private final Function<Var, Var> fun;

    private VToDouble(Function<Var, Var> fun) {
        this.fun = fun;
    }

    @Override
    public Var apply(Var var) {
        return fun.apply(var);
    }

    /* Implements default transformations */
    static class DefaultByType implements Function<Var, Var> {

        @Override
        public Var apply(Var var) {
            switch (var.type()) {
                case DOUBLE:
                    return new TransformDouble(x -> x).apply(var);
                case INT:
                case BINARY:
                    return new TransformInt(x -> x).apply(var);
                case NOMINAL:
                case TEXT:
                    return new TransformLabel(x -> {
                        try {
                            return Double.parseDouble(x);
                        } catch (NumberFormatException ex) {
                            return Double.NaN;
                        }
                    }).apply(var);
                default:
                    throw new IllegalArgumentException("Variable type: " + var.type().code() + " is not supported.");
            }
        }
    }


    static class TransformSpot implements Function<Var, Var> {
        private final Function<VSpot, Double> function;

        TransformSpot(Function<VSpot, Double> function) {
            this.function = function;
        }

        @Override
        public Var apply(Var var) {
            double[] value = new double[var.rowCount()];
            for (int i = 0; i < var.rowCount(); i++) {
                value[i] = var.isMissing(i) ? Double.NaN : function.apply(new VSpot(i, var));
            }
            return VarDouble.wrap(value).withName(var.name());
        }
    }

    static class TransformDouble implements Function<Var, Var> {
        private final Double2DoubleFunction function;

        TransformDouble(Double2DoubleFunction function) {
            this.function = function;
        }

        @Override
        public Var apply(Var var) {
            double[] value = new double[var.rowCount()];
            for (int i = 0; i < var.rowCount(); i++) {
                value[i] = var.isMissing(i) ? Double.NaN : function.applyAsDouble(var.getDouble(i));
            }
            return VarDouble.wrap(value).withName(var.name());
        }
    }

    static class TransformInt implements Function<Var, Var> {
        private final Int2DoubleFunction function;

        TransformInt(Int2DoubleFunction function) {
            this.function = function;
        }

        @Override
        public Var apply(Var var) {
            double[] value = new double[var.rowCount()];
            for (int i = 0; i < var.rowCount(); i++) {
                value[i] = var.isMissing(i) ? Double.NaN : function.applyAsDouble(var.getInt(i));
            }
            return VarDouble.wrap(value).withName(var.name());
        }
    }

    static class TransformLabel implements Function<Var, Var> {
        private final Function<String, Double> function;

        TransformLabel(Function<String, Double> function) {
            this.function = function;
        }

        @Override
        public Var apply(Var var) {
            double[] values = new double[var.rowCount()];
            for (int i = 0; i < var.rowCount(); i++) {
                values[i] = function.apply(var.getLabel(i));
            }
            return VarDouble.wrap(values).withName(var.name());
        }
    }

    @Override
    public String content() {
        return "VToDouble";
    }

    @Override
    public String toString() {
        return content();
    }

}

