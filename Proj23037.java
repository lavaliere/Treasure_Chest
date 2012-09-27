/**********************************************************************************
CMSC 256
Proj 2
Written by Tracy Kennedy

I used the given algorithm with some slight modifications to create the assigned
Gaussian Elimination program. It takes in data from a file specified in the cmd line,
parses the doubles from it to build a matrix in a 2D array, then using backwards
substitution to find the unknown x's. 

If the given matrix is unsolvable, the program crashes itself and throws a
runtime exception. Otherwise, it prints out the solved list of solutions for the
given equations.
**********************************************************************************/

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.text.DecimalFormat;

public class Proj23037 {
    public static void main(String[] args) throws IOException {
        System.out.println("Gaussian Elimination");
        System.out.println("T. Kennedy 9/12 \r\n");

        for(int i = 0; i < args.length; i++){
            Scanner fileScan = new Scanner(new FileInputStream(args[i]));
            System.out.println("Opened File: " + args[i] + "\r\n");
            DecimalFormat fmt = new DecimalFormat("0.000");

            while(fileScan.hasNext()){
                //build input data
                int n= fileScan.nextInt();
                System.out.println("Number of unknowns: " + n);
                double [][] dGauss = fileContent(fileScan, n);
                String[][] sFormat = formattedArray(dGauss, n);

                for(int k = 0; k < n; k++){
                    String sPrint = "";
                    for(int j = 0; j < n+1; j++){
                        sPrint += sFormat[k][j];
                        }//for j
                //echo print data
                    System.out.println(sPrint);
                    }//for k

                System.out.println("Solution: ");

                //do gaussian elimination
                int iResult = 1;
                double[] results = gauss(dGauss);
                for(int k = 0; k < results.length; k++){
                    System.out.println("  x_" + iResult + " = " + fmt.format(results[k]));
                    iResult++;
                }
                System.out.println("");

            }//fileScan.hasNext while
        }//for args.length
    }//main method

    private static double[][] fileContent(Scanner fileScan, int n){
        double [][] dFileScanned = new double[n][n+1];
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n+1; j++){
                dFileScanned[i][j] = fileScan.nextDouble();
            }
        }
        return dFileScanned;
    }//fileContent array

    private static String[][] formattedArray(double[][] fInput, int n){
        //here we format the 2D array into a linear equation
        String[][] formattedArray = new String[n][n+1];

        for(int i = 0; i <n; i++){
            int xNum = 1;
            for(int j = 0; j < n + 1; j++){
                if(j == n-1){
                    formattedArray[i][j] = fInput[i][j] + "x_" + xNum + " = ";
                    }    else if(j == n) {
                        formattedArray[i][j] = fInput[i][j] + "";
                }    else{
                    formattedArray[i][j] = fInput[i][j] + "x_" + xNum + " + ";
                }//else
                xNum++;
            }//for j
        }//for i

        return formattedArray;
    }
    
    private static double[] gauss(double[][] equation){
        int n = equation.length;
        double[] matrixRow = new double[n];

        for(int i = 0; i < n; i++){    //STEP ONE - for loop for elimination through the rows
            int p = i;
        //STEP TWO=  let p be the smallest integer with
            if(equation[p][i]== 0){
	            for(int k = i; k < n - 1; k++){
	                for(int q = i +1; q < n; q++){
	                    if(equation[k][0] <= equation[q][0] && equation[k][0]!= 0){
	                        p = k;
	                    } else if(equation[q][0] <= equation[k][0] && equation[q][0]!= 0) {
	                        p = q;
	                    } 
	                }//q
	            }//for k 
            }
	       //if no p found, then throw runtimeexception
	       if(equation[p][i] == 0 ){
	           throw new RuntimeException("singular matrix - no unique solution");
	            } 

      //STEP THREE- if p!= i, the switch equation[p][] with equation[i][]
             if(p!= i){
                 double[][] switchRow = new double[1][n+1];
                 for(int w = 0; w < n+1; w++){
                     double[][] elimCopy = new double[1][n+1];
                     elimCopy[0][w] =  equation[p][w];
                     equation[p][w] =  equation[i][w];
                     equation[i][w] =  elimCopy[0][w];
                 }
             }//for p!=i
             
             if(equation[i][i] == 0){
                    throw new RuntimeException("singular matrix - no unique solution");
                }
     //STEP FOUR -for j=i + 1...n through step 6
            for(int j = i+1; j < n ; j++){
     //STEP FIVE - set m = equation[j][i]/equation[i][i]
            	double m = equation[j][i]/equation[i][i];
     //STEP SIX- do (equation[j] - (m * equation[i])) -> equation[j]
                for(int loop = 0; loop < n+1 ; loop++){ //looping through row i to find m* row i's elements
                    equation[j][loop] = equation[j][loop] - (m * equation[i][loop]);
                    }//for loop
                }//for j
            }//for i
     //STEP 7 - if equation[n][n] = 0, throw runtimeException
            if(equation[n-1][n-1] == 0){
                throw new RuntimeException("singular matrix - no unique solution");
            }
     //STEP 8- set matrixrow = equation[n][n+1]/equation[n][n]
            	matrixRow[n-1] = equation[n-1][n]/equation[n-1][n-1];
     //STEP 9 - for i = n-1....1
            for(int i = n-1 ; i > -1; i--){
            double matrixSet = 0.0;
                for(int j = i+1; j < n; j++){ 
                	//for the summation notation loop to find matrixSet
                    matrixSet = matrixSet + (equation[i][j] * matrixRow[j]);
                    }
                matrixRow[i] = (equation[i][n] - matrixSet)/equation[i][i];
            }
     //STEP 10 -output matrixrow[1]...matrixrow[n]
        return matrixRow;
    } 
}//Proj23037 class
