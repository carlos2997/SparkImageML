import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Carlos Alberto Ramirez Otero
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ImageParser ip = new ImageParser();
        List<Image> trainingImages = new CopyOnWriteArrayList<>();
        for (Image trainImg0 : ip.parserFolderFilesToImages("180x200", "B:\\Desktop\\conPlan","0")) {
            trainingImages.add(trainImg0);
        }
        for (Image trainImg1 : ip.parserFolderFilesToImages("180x200", "B:\\Desktop\\sinPlan", "1")) {
            trainingImages.add(trainImg1);
        }
        
        ImageAnalytic svm = new ImageAnalytic(trainingImages);
        svm.execImageAnalysis(ip.parserFolderFilesToImages("180x200", "B:\\Desktop\\prueba","1"));
        
        //svm.execImageAnalysis(ip.parserFileToImage("180x200", new File(""),"1"));
        /*
        ip.parserImageToCSV("1152x864", "B:\\Desktop\\male","training","1");
        ip.parserImageToCSV("1152x864", "B:\\Desktop\\female","training","0");
        ip.parserImageToCSV("1152x864", "B:\\Desktop\\prueba","test","00");
         */
        
        
    }
}
