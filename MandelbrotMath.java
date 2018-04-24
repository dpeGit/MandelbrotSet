package mandelbrotSet;

import java.util.concurrent.Callable;

import org.apfloat.Apfloat;

public class MandelbrotMath implements Callable<double[]> {

	private static final double log2 = Math.log(2);

	private int max, col, row;
	private Apfloat real;
	private Apfloat imagine;

	public MandelbrotMath(int max, Apfloat real, Apfloat imagine, int row, int col) {
		this.max = max;
		this.col = col;
		this.row = row;
		this.real = real;
		this.imagine = imagine;
	}

	@Override
	public double[] call() throws Exception {
		double[] pixel = new double[3];
		pixel[0] = calcPixel();
		pixel[1] = row;
		pixel[2] = col;
		return pixel;
	}

	private double calcPixel() {
		Apfloat x = new Apfloat(0, 100);
		Apfloat y = new Apfloat(0, 100);
		Apfloat xSqr = new Apfloat(0, 100);
		Apfloat ySqr = new Apfloat(0, 100);
		int iteration = 0;

		while (xSqr.add(ySqr).compareTo(new Apfloat(4)) < 0 && iteration < max) {
			xSqr = x.multiply(x);
			ySqr = y.multiply(y);
			Apfloat xTemp = xSqr.subtract(ySqr).add(real);
			y = x.multiply(new Apfloat(2)).multiply(y).add(imagine);
			x = xTemp;
			iteration++;
		}
		if (iteration == max) {
			return iteration;
		} else {
			double logZn = Math.log(xSqr.doubleValue() + ySqr.doubleValue()) / 2;
			double nu = Math.log(logZn / log2) / log2;
			return iteration + 1 - nu;
		}
	}

}
