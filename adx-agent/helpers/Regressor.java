package helpers;

import libsvm.*;


class Regressor{
    double[][] training_data;
    svm_model model;
    
    /**
     * Linear regressor class.
     * @param training_data array of points to be trained on.
     *                      the last value of the point is the target value
     */
    public Regressor(double[][] training_data){
        this.training_data = training_data;
        // Disable svm output
        svm.svm_set_print_string_function(new libsvm.svm_print_interface(){
            @Override public void print(String s) {}
        });
        this.train();
    }
    
    private void train() {
        svm_problem problem = new svm_problem();
        int points_nbr = training_data.length;
        
        // svm_nodes are the input value (x), y is the target value
        problem.x = new svm_node[points_nbr][];
        problem.y = new double[points_nbr];
        problem.l = points_nbr;
        
        // feed every point in the training algorithm
        for (int i=0; i< points_nbr; i++){
            
            // feeding inputs of this point
            int inputs_nbr = training_data[0].length-1;
            problem.x[i] = new svm_node[inputs_nbr];
            for (int j=0; j<inputs_nbr; j++) {
                problem.x[i][j] = new svm_node();
                problem.x[i][j].value = training_data[i][j];
            }
            
            // feeding target value of this point
            problem.y[i] = training_data[i][inputs_nbr];
        }
        
        svm_parameter p = new svm_parameter();
        p.probability = 1;
        p.gamma = 1;
        p.C = 1;
        p.svm_type = svm_parameter.EPSILON_SVR;
        p.kernel_type = svm_parameter.LINEAR;
        p.eps = 0.5;
        
        this.model = svm.svm_train(problem, p);
    }
    
    /**
     * Gives predicted value for a set of inputs.
     */
    public double predict(double[] point){
        svm_node[] test_vector = new svm_node[point.length];
        for (int i=0; i<point.length; i++) {
            test_vector[i] = new svm_node();
            test_vector[i].value = point[i];
        }
        return svm.svm_predict(this.model, test_vector);
    }
    
    public static void main(String[] args) {
        // example contains 3 points who belong to the plane z=1.34
        // try changing one of the Z values to see the predicted values change
        
        // training our regressor
        double[][] example = new double[3][3];
        example[0][0] = 3;
        example[0][1] = 2;
        example[0][2] = 1.34;
        example[1][0] = 4;
        example[1][1] = 5;
        example[1][2] = 1.34;
        example[2][0] = 0;
        example[2][1] = 0;
        example[2][2] = 1.34;
        Regressor r = new Regressor(example);
        
        // testing prediction
        double[][] values = new double[15][2];
        for (int i = 0; i<15; i++) {
            values[i][0] = i;
            values[i][1] = i;
            double predictions = r.predict(values[i]);
            System.out.print("Prediction : ");
            System.out.println(predictions);
        }
        
    }
}
