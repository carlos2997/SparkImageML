import java.io.Serializable;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;

import scala.Tuple2;

/**
 *
 * @author Carlos Alberto Ramirez Otero
 */
public class ImageAnalytic implements Serializable{

    private final List<Image> initialTrainingImages;
    private JavaRDD training;
    
    public ImageAnalytic(List<Image> initialTrainingImages){
        this.initialTrainingImages = initialTrainingImages;
    }
    
    public void generateNewTrainingDataModel(List<Image> newTrainingImages){
        System.setProperty("hadoop.home.dir", "B:\\Documents\\NetBeansProjects\\ImageMLSpark");
        SparkConf conf = new SparkConf().setAppName("SVM vs Navie Bayes").setMaster("local[2]").set("spark.executor.memory","1g");
        JavaSparkContext sc = new JavaSparkContext(conf);
        this.training = parseImageArrayToRDD(newTrainingImages,sc);
    }
    
    private JavaRDD parseImageArrayToRDD(List<Image> collectionImages, JavaSparkContext sc){
        return sc.parallelize(collectionImages).cache().map(new Function<Image, LabeledPoint>() {
            @Override
            public LabeledPoint call(Image v1) throws Exception {
                return new LabeledPoint(v1.getAnalysis(), Vectors.dense(v1.getData()));
            }
        });
    }

    public void execImageAnalysis(List<Image> testingImages) {
        System.setProperty("hadoop.home.dir", "B:\\Documents\\NetBeansProjects\\ImageMLSpark");
        // Create Java spark context       
        
        SparkConf conf = new SparkConf().setAppName("SVM vs Navie Bayes").setMaster("local[2]").set("spark.executor.memory","1g");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        if(this.training != null){
            this.training = parseImageArrayToRDD(this.initialTrainingImages,sc);
        }
        
        System.out.println("Training Count: "+training.count());
        
        JavaRDD test = parseImageArrayToRDD(testingImages, sc);
        
        System.out.println("Test Count: "+test.count());
        
        final NaiveBayesModel model = NaiveBayes.train(training.rdd(), 1.0);

        JavaPairRDD<Double, Double> predictionAndLabel = test.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
            @Override
            public Tuple2<Double, Double> call(LabeledPoint p) {
                return new Tuple2<Double, Double>(model.predict(p
                        .features()), p.label());
            }
        });
        
        double accuracy = 1.0
                * predictionAndLabel.filter(
                        new Function<Tuple2<Double, Double>, Boolean>() {
                    @Override
                    public Boolean call(Tuple2<Double, Double> pl) {
                        System.out.println("NAIVE BAYES-PREDICTION (PREDICTED - EXPECTED): "+pl._1() + " -- " + pl._2());
                        return pl._1().intValue() == pl._2().intValue();
                    }
                }).count() / (double) test.count();
        
        System.out.println("navie bayes accuracy : " + accuracy);

        final SVMModel svmModel = SVMWithSGD.train(training.rdd(), 100);

        JavaPairRDD<Double, Double> predictionAndLabelSVM = test.mapToPair(new PairFunction<LabeledPoint, Double, Double>() {
            @Override
            public Tuple2<Double, Double> call(LabeledPoint p) {
                return new Tuple2<Double, Double>(svmModel.predict(p
                        .features()), p.label());
            }
        });
        
        double accuracySVM = 1.0
                * predictionAndLabelSVM.filter(
                        new Function<Tuple2<Double, Double>, Boolean>() {
                    @Override
                    public Boolean call(Tuple2<Double, Double> pl) {
                    
                        System.out.println("SVM-PREDICTION (PREDICTED - EXPECTED): "+pl._1() + " -- " + pl._2());
                        return pl._1().intValue() == pl._2().intValue();
                    }
                }).count() / (double) test.count();
        
        System.out.println("svm accuracy : " + accuracySVM);
        
    }
}