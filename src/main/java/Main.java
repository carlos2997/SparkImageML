
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Carlos Alberto Ramirez Otero
 */
public class Main {
    public static void main(String[] args) throws IOException{
        ImageParser ip = new ImageParser();
        SVM svm = new SVM();
        ip.parserImageToCSV("180x200", "B:\\Desktop\\male", "1");
        ip.parserImageToCSV("180x200", "B:\\Desktop\\female", "0");
        ip.parserImageToCSV("180x200", "B:\\Desktop\\prueba", "11");
        //svm.generateAnalysis("B:\\Desktop\\training", "B:\\Desktop\\test");
    }
}
