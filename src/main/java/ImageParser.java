
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;

/**
 * @author Carlos Alberto Ramirez Otero
 */
public class ImageParser {

    public ImageParser() {}

    public void toGray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.21);
                int green = (int) (c.getGreen() * 0.72);
                int blue = (int) (c.getBlue() * 0.07);
                int sum = red + green + blue;
                Color newColor = new Color(sum, sum, sum);
                image.setRGB(j, i, newColor.getRGB());
            }
        }
    }

    public BufferedImage createResizedCopy(BufferedImage originalImage,
            int scaledWidth, int scaledHeight,
            boolean preserveAlpha) {
        System.out.println("resizing...");
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
    
    private Image parserBufferedImageToImageClass(File input, int scaledWidth, int scaledHeight, String analysis) throws IOException {
        BufferedImage image = createResizedCopy(ImageIO.read(input), scaledWidth, scaledHeight, Boolean.TRUE);
        toGray(image);
        Raster raster = image.getData();
        int w = raster.getWidth(), h = raster.getHeight();
        double[] dataImg = new double[w * h];
        int cont = 0;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                dataImg[cont] = raster.getSample(x, y, 0) * 1.0;
                cont += 1;
            }
        }
        Image imgageRepr = new Image(input.getName(), Double.parseDouble(analysis), dataImg);
        return imgageRepr;
    }

    public List<Image> parserFileToImage(String pixels, File file, String analysis) throws IOException {
        String[] piexels = pixels.split("x");
        int scaledWidth = Integer.parseInt(piexels[0]);
        int scaledHeight = Integer.parseInt(piexels[1]);
        List<Image> images = new CopyOnWriteArrayList<>();
        images.add(parserBufferedImageToImageClass(file, scaledWidth, scaledHeight, analysis));
        return images;
    }
  
    public List<Image> parserFolderFilesToImages(String pixels, String pathFolderFiles, String analysis) throws IOException {
        String[] piexels = pixels.split("x");

        int scaledWidth = Integer.parseInt(piexels[0]);
        int scaledHeight = Integer.parseInt(piexels[1]);

        ArrayList<String> paths = new ArrayList<>();

        Files.walkFileTree(Paths.get(pathFolderFiles), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //Files.delete(file);
                paths.add(file.toFile().getAbsolutePath());
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                //Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });

        List<Image> images = new CopyOnWriteArrayList<>();
        for (String file : paths) {
            String extension = "";
            int extIndex = file.indexOf('.');
            extension = file.substring(extIndex + 1);
            System.out.println(file + " extension " + extension);
            File input = new File(file);
            images.add(parserBufferedImageToImageClass(input, scaledWidth, scaledHeight, analysis));
        }
        return images;
    }

}
