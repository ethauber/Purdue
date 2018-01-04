/*************************************************************************
 * Compilation:  javac Point.java
 *
 * Description: An immutable data type for points in the plane.
 *
 *************************************************************************/

import java.lang.*;
import java.util.Comparator;

public class Point implements Comparable<Point>{
 	public final Comparator<Point> BY_SLOPE_ORDER = new Comparator<Point>(){
     
     @Override
     public int compare(Point a, Point b) {
      	double slopeA = getSlope(a);
      	double slopeB = getSlope(b);
         //System.out.format("This:(%d,%d)->", x, y);
         //System.out.format("[point:angle]_A=[(%d,%d):%f]_B=[(%d,%d):%f]", a.x, a.y, angleA, b.x, b.y, angleB);
      	if(slopeA == slopeB) {
      	    //StdOut.println("-> 1");
            return 0;
      	}
      	else if(slopeA < slopeB) {
            //StdOut.println("-> -1");
      		return -1;
      	}
      	else {
            //StdOut.println("-> 0");
            return 1;
        }
     }
    };    // YOUR DEFINITION HERE

    private final int x;                              // x coordinate
    private final int y;                              // y coordinate
 
    // constructor
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
 
 	//getter x
 	public int getX(){
 		return this.x;
 	}
 
 	//getter y
 	public int getY(){
 		return this.y; 
 	}
 
 	//get P_oint A_s S_tring in form as specified in handout
 	public String getPAS(){
  		return "(" + x + ", " + y + ")";
 	}

 	public double getSlope(Point that) {
 	    if(that.x == this.x) {
 	        if(that.y == this.y) {
                return Double.NEGATIVE_INFINITY;
            }
            else {
                return Double.POSITIVE_INFINITY;
            }
        }
        else if(that.y == this.y) {
            return 0;
        }
        else {
            return (that.y - this.y)/(double)(that.x - this.x);
        }
    }

 	//return the angle for two points zero and one
 	//public static double getAngle(Point zero, Point one) {
  	//	return Math.atan2(one.y - zero.y, one.x - zero.x);
 	//}
    //was a nice idea...

    // are the 3 points p, q, and r collinear?
    public static boolean areCollinear(Point p, Point q, Point r) {
        /* YOUR CODE HERE */
     double slopeQ = p.getSlope(q);
     double slopeR = p.getSlope(r);
        return (slopeQ == slopeR);
    }

    // are the 4 points p, q, r, and s collinear?
    public static boolean areCollinear(Point p, Point q, Point r, Point s) {
        /* YOUR CODE HERE */
        return (areCollinear(p,q,r)) && (areCollinear(q,r,s));
    }

    // is this point lexicographically smaller than that one?
    public int compareTo(Point that) {
        /* YOUR CODE HERE */
     if(this.x < that.x) {
      return -1;
     }
     else if(this.x > that.x) {
      return 1;
     }
     else if(this.x == that.x) {
      if(this.y < that.y) {
       return -1;
      }
      else if(this.y > that.y) {
       return 1;
      }
     }
     return 0;
    }

}
