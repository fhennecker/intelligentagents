package helpers;

import java.util.ArrayList;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.special.Erf;

public class EM {

    public DescriptiveStatistics wonBids = new DescriptiveStatistics(5);
    public DescriptiveStatistics lostBids = new DescriptiveStatistics(5);

    public EM(){

    }

    public double getOneStdWinBid(){
        return wonBids.getMean() - wonBids.getStandardDeviation();
    }

    public double getOneStdLostBid(){
        return lostBids.getMean() - lostBids.getStandardDeviation();
    }

    public double getPessimisticBid(double reach, double factor){
        return reach * (lostBids.getMean() - lostBids.getStandardDeviation() * factor);
    }

    public double getOptimisticBid(double reach, double factor){
        return reach * (wonBids.getMean() + wonBids.getStandardDeviation() * factor);
    }
}