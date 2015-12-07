package helpers;

import java.util.ArrayList;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.special.Erf;

public class EM {

    public DescriptiveStatistics wonBids = new DescriptiveStatistics(7);
    public DescriptiveStatistics lostBids = new DescriptiveStatistics(7);

    public EM(){

    }

    public double getOneStdWinBid(){
        return wonBids.getMean() - wonBids.getStandardDeviation();
    }

    public double getOneStdLostBid(){
        return lostBids.getMean() - lostBids.getStandardDeviation();
    }

    public double getPessimisticBid(double reach){
        return reach * (lostBids.getMean() - lostBids.getStandardDeviation()/2);
    }

    public double getOptimisticBid(double reach){
        System.out.print("Mean ");
        System.out.print(wonBids.getMean());
        System.out.print(", Variance ");
        System.out.print(wonBids.getStandardDeviation());
        return reach * (wonBids.getMean() + wonBids.getStandardDeviation()/2);
    }
}