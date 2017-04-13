package mandelbrotSet;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

public class MandelbrotMath implements Callable<int[]> {

	private int m, col, row;
	private BigDecimal r, i;

	public MandelbrotMath(int m, BigDecimal r, BigDecimal i, int col, int row) {
		this.m = m;
		this.r = r;
		this.i = i;
		this.col = col;
		this.row = row;
	}

	@Override
	public int[] call() throws Exception {
		int[] pixel = new int[3];
		pixel[0] = calcPixel();
		pixel[1] = col;
		pixel[2] = row;
		return pixel;
	}

	private int calcPixel() {
		BigDecimal x = new BigDecimal(0), y = new BigDecimal(0);
		int iteration = 0;
		BigDecimal xSqr = x.pow(2);
		BigDecimal ySqr = y.pow(2);
		while (xSqr.add(ySqr).compareTo(new BigDecimal(4)) < 0 && iteration < m) {
			BigDecimal xNew = xSqr.subtract(ySqr).add(r);
			y = x.multiply(new BigDecimal(2)).multiply(y).add(i);
			x = xNew;
			xSqr = x.pow(2);
			ySqr = y.pow(2);
			iteration++;
		}
		if (iteration < m) {
			return iteration;
		} else {
			return 0;
		}
	}

}
