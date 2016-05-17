/**
 * Created by leo on 16-5-16.
 */
public class VectorMath {
    final static int VECSIZE = 2;

    public static double[] add(double[] a, double[] b){
        double[] result = new double[VECSIZE];
        for(int i = 0; i < VECSIZE; i++){
            result[i] = a[i] + b[i];
        }
        return result;
    }

    public static double[] add(double[] a, double b){
        double[] result = new double[VECSIZE];
        for(int i = 0; i < VECSIZE; i++){
            result[i] = a[i] + b;
        }
        return result;
    }

    public static double[] subtract(double[] a, double[] b){
        double[] result = new double[VECSIZE];
        for(int i = 0; i < VECSIZE; i++){
            result[i] = a[i] - b[i];
        }
        return result;
    }

    public static double[] subtract(double[] a, double b){
        double[] result = new double[VECSIZE];
        for(int i = 0; i < VECSIZE; i++){
            result[i] = a[i] - b;
        }
        return result;
    }

    public static double[] invert(double[] a){
        double[] result = new double[VECSIZE];
        for(int i = 0; i < VECSIZE; i++){
            result[i] = -a[i];
        }
        return result;
    }

    public static double dotProd(double[] a, double[] b){
        double result = 0;
        for(int i = 0; i < VECSIZE; i++){
            result += a[i]*b[i];
        }
        return result;
    }

    public static double[] scale(double[] a, double b){
        double[] result = new double[VECSIZE];
        for(int i = 0; i < VECSIZE; i++){
            result[i] = a[i]*b;
        }
        return result;
    }

}
