package utilities;

public class TrapezoidalCurve {
	private double slopeUp, slopeDown, maxX, maxY, startY, endY;
	private double x1, x2;
	
	public TrapezoidalCurve(double slope, double maxX, double maxY, double minY){
		this.slopeUp = slope;
		this.slopeDown = slope;
		this.maxX = maxX;
		this.maxY = maxY;
		this.startY = minY;
		this.endY = minY;
		
		init(slope, slope, maxX, maxY, minY, minY);
	}
	
	public TrapezoidalCurve(double slopeUp, double slopeDown, double maxX, double maxY, double startY, double endY){
		this.slopeUp = Math.abs(slopeUp);
		this.slopeDown = Math.abs(slopeDown);
		this.maxX = maxX;
		this.maxY = maxY;
		this.startY = startY;
		this.endY = endY;
	
		init(Math.abs(slopeUp), Math.abs(slopeDown), maxX, maxY, startY, endY);
	}
	
	private void init(double slopeUp, double slopeDown, double maxX, double maxY, double startY, double endY){
		x1 = (maxY - startY) / slopeUp;
		x2 = maxX - (maxY - endY) / slopeDown;
		if(x1 + (maxX - x2) > maxX){ //if triangle instead of trapezoid
			x1 = (slopeDown * maxX + endY - startY) / (slopeUp + slopeDown);
			x2 = x1;
		}
	}

	public double getY(double x) {
		if(x <= x1){
			return slopeUp * x + startY;
		}
		else if(x < x2){
			return maxY;
		}
		else if(x <maxX){
			return slopeDown * (maxX - x) + endY;
		}
		else{
			return endY;
		}
	}
}