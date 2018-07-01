import java.io.Serializable;


/**
 *
 * @author Carlos Alberto Ramirez Otero
 */
public class Image implements Serializable {
    
    private String name;
    private double analysis;
    private double[] data;

    public Image() {
    }

    public Image(String name, double analysis, double[] data) {
        this.name = name;
        this.analysis = analysis;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAnalysis() {
        return analysis;
    }

    public void setAnalysis(double analysis) {
        this.analysis = analysis;
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
    }
    
    @Override
    public String toString(){
        return "Image: [name: "+name+", analysis: "+Double.toString(analysis)+", dataLenght: "+Integer.toString(data.length)+"]";
    }
}
