
package com.scx.movemove.data;

import Jama.Matrix;
import Jama.QRDecomposition;

import android.content.Context;

import com.scx.movemove.activities.WelcomeActivity;
import com.scx.movemove.tools.FileHandler;

import java.util.ArrayList;

/**
 * <h1>Polynomial Regression</h1> The PolinomialRegression class is used for personalized speed
 * detection
 * 
 * @author Chenxi
 * @version 3.0
 * @since 2014-08-15
 */
public class PolynomialRegression {
    public final static int DEGREE = 3; // degree of the polynomial regression
    private int mN; // number of observations
    private double mSse; // sum of squares due to error
    private double mSst; // total sum of squares
    private ArrayList<Double> mListX;
    private ArrayList<Double> mListY;
    private Matrix mBeta; // the polynomial regression coefficients
    private UserInfo mUser = WelcomeActivity.DEFAULT_USER;
    private FileHandler mFileHandler;

    /*
     * Get historical Accelerometer-Speed data of the mUser
     */
    public static double[] getXY(String str) {
        double[] xy = new double[2];
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) == ' ') {
                break;
            }
            i++;
        }
        xy[0] = Double.parseDouble(str.substring(0, i));
        xy[1] = Double.parseDouble(str.substring(i + 1, str.length()));
        return xy;
    }

    public void readFile(String type) {
        String FILENAME = "";
        if (type.equals("walking")) {
            FILENAME = new String(this.mUser.getName() + "_walking");

        } else {
            FILENAME = new String(this.mUser.getName() + "_running");
        }
        String line = this.mFileHandler.readFromFile(FILENAME);
        if (line == null || "".equals(line.trim())) {
            return;
        }
        String[] parts = line.split(", ");
        double[] xy = new double[2];
        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].equals("")) {
                xy = getXY(parts[i]);
            }
            this.mListX.add(xy[0]);
            this.mListY.add(xy[1]);
            System.out.println(i);
        }
    }

    public PolynomialRegression(Context context, int height_range,
            String type) {
        this.mFileHandler = new FileHandler(context);
        this.mListX = new ArrayList<Double>();
        this.mListY = new ArrayList<Double>();
        this.readFile(type);
        Double[] xD = this.mListX.toArray(new Double[this.mListX.size()]);
        Double[] yD = this.mListY.toArray(new Double[this.mListY.size()]);
        double[] x = new double[xD.length];
        int h = 0;
        for (Double d : xD) {
            x[h] = d;
            h++;
        }
        double[] y = new double[yD.length];
        h = 0;
        for (Double d : yD) {
            y[h] = d;
            h++;
        }
        this.mN = x.length;

        // build Vandermonde matrix
        double[][] vandermonde = new double[this.mN][PolynomialRegression.DEGREE + 1];
        for (int i = 0; i < this.mN; i++) {
            for (int j = 0; j <= PolynomialRegression.DEGREE; j++) {
                vandermonde[i][j] = Math.pow(x[i], j);
            }
        }
        Matrix X = new Matrix(vandermonde);

        // create matrix from vector
        Matrix Y = new Matrix(y, this.mN);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(X);
        this.mBeta = qr.solve(Y);

        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < this.mN; i++) {
            sum += y[i];
        }
        double mean = sum / this.mN;

        // total variation to be accounted for
        for (int i = 0; i < this.mN; i++) {
            double dev = y[i] - mean;
            this.mSst += dev * dev;
        }

        // variation not accounted for
        Matrix residuals = X.times(this.mBeta).minus(Y);
        this.mSse = residuals.norm2() * residuals.norm2();
    }

    /**
     * Performs a polynomial regression on the data points <tt>(y[i], x[i])</tt> .
     * 
     * @param x the values of the predictor variable
     * @param y the corresponding values of the response variable
     * @param degree the degree of the polynomial to fit
     * @throws java.lang.IllegalArgumentException if the lengths of the two arrays are not equal
     */
    /**
     * Add a new data point: Update the regression polynomial.
     * 
     * @param dataPoint the new data point
     */
    public void AdjustCoef(DataPoint dataPoint) {
        this.mListX.add(dataPoint.getX());
        this.mListY.add(dataPoint.getY());
        Double[] xD = this.mListX.toArray(new Double[this.mListX.size()]);
        Double[] yD = this.mListY.toArray(new Double[this.mListY.size()]);
        double[] x = new double[xD.length];
        int h = 0;
        for (Double d : xD) {
            x[h] = d;
            h++;
        }
        double[] y = new double[yD.length];
        h = 0;
        for (Double d : yD) {
            y[h] = d;
            h++;
        }
        this.mN = x.length;

        // build Vandermonde matrix
        double[][] vandermonde = new double[this.mN][PolynomialRegression.DEGREE + 1];
        for (int i = 0; i < this.mN; i++) {
            for (int j = 0; j <= PolynomialRegression.DEGREE; j++) {
                vandermonde[i][j] = Math.pow(x[i], j);
            }
        }
        Matrix X = new Matrix(vandermonde);

        // create matrix from vector
        Matrix Y = new Matrix(y, this.mN);

        // find least squares solution
        QRDecomposition qr = new QRDecomposition(X);
        this.mBeta = qr.solve(Y);

        // mean of y[] values
        double sum = 0.0;
        for (int i = 0; i < this.mN; i++) {
            sum += y[i];
        }
        double mean = sum / this.mN;

        // total variation to be accounted for
        for (int i = 0; i < this.mN; i++) {
            double dev = y[i] - mean;
            this.mSst += dev * dev;
        }

        // variation not accounted for
        Matrix residuals = X.times(this.mBeta).minus(Y);
        this.mSse = residuals.norm2() * residuals.norm2();
    }

    /**
     * Returns the <tt>j</tt>th regression coefficient
     * 
     * @return the <tt>j</tt>th regression coefficient
     */
    public double beta(int j) {
        return this.mBeta.get(j, 0);
    }

    /**
     * Returns the degree of the polynomial to fit
     * 
     * @return the degree of the polynomial to fit
     */
    public int degree() {
        return PolynomialRegression.DEGREE;
    }

    /**
     * Returns the coefficient of determination <em>R</em><sup>2</sup>.
     * 
     * @return the coefficient of determination <em>R</em><sup>2</sup>, which is a real number
     *         between 0 and 1
     */
    public double R2() {
        if (this.mSst == 0.0) {
            return 1.0; // constant function
        }
        return 1.0 - this.mSse / this.mSst;
    }

    /**
     * Returns the expected response <tt>y</tt> given the value of the predictor variable <tt>x</tt>
     * .
     * 
     * @param x the value of the predictor variable
     * @return the expected response <tt>y</tt> given the value of the predictor variable <tt>x</tt>
     */
    public double predict(double x) {
        // horner's method
        double y = 0.0;
        for (int j = PolynomialRegression.DEGREE; j >= 0; j--) {
            y = this.beta(j) + (x * y);
        }
        return y;
    }

    /**
     * Returns a string representation of the polynomial regression model.
     * 
     * @return a string representation of the polynomial regression model, including the best-fit
     *         polynomial and the coefficient of determination <em>R</em><sup>2</sup>
     */
    @Override
    public String toString() {
        String s = "";
        int j = PolynomialRegression.DEGREE;

        // ignoring leading zero coefficients
        while (j >= 0 && Math.abs(this.beta(j)) < 1E-5) {
            j--;
        }

        // create remaining terms
        for (; j >= 0; j--) {
            if (j == 0) {
                s += String.format("%.2f ", this.beta(j));
            } else if (j == 1) {
                s += String.format("%.2f mN + ", this.beta(j));
            } else {
                s += String.format("%.2f mN^%d + ", this.beta(j), j);
            }
        }
        return s + "  (R^2 = " + String.format("%.3f", this.R2()) + ")";
    }

}
