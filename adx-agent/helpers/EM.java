package helpers;

import java.util.ArrayList;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.special.Erf;

public class EM {

    public ArrayList<Double> pastQBWon= new ArrayList<Double>();
    public ArrayList<Double> pastQBLost= new ArrayList<Double>();

    public EM(){

    }

    public Double getLikelihood(Double m, Double v){
        Double g=1.0;
        for(int i=0; i<pastQBWon.size(); i++){
            Double x=pastQBWon.get(i);
            g*=(Math.exp(-(x-m)*(x-m)/2*v*v)/(Math.sqrt(2*Math.PI)*v));
        }
        
        for(int i=0; i<pastQBLost.size(); i++){
            Double x=pastQBLost.get(i);
            g*=(1-(1+Erf.erf((x-m)/(2*Math.sqrt(v))))*0.5);
        }
        return g;
    }
    
    public Double[] maxLikelihood(){
        Double maxLike=0.0;
        Double like=0.0;
        Double m=0.5;
        Double v=0.2*0.2;
        for (double i=0.1; i<1.0; i+=((1.0-0.1)/100)){
            for (double j=0.0; j<0.2; j+=(0.2/100)){
                like=getLikelihood(i,j*j);
                if(like>maxLike){
                    maxLike=like;
                    m=i;
                    v=j;
                }       
            }
        }
        Double returnData[]=new Double[2];
        returnData[0]=m;
        returnData[1]=v;
        return returnData;
    }
}