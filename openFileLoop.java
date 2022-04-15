import java.io.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;

public class openFileLoop {

    public static void main(String[] args) throws IOException {
        
        // create window to select a jpg or png file
        JFileChooser openFile = new JFileChooser(System.getProperty("user.dir") + "\\input");
        openFile.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("JPG or PNG file", "jpg", "png");
        openFile.addChoosableFileFilter(extensionFilter);
        int fileOpened = openFile.showOpenDialog(null);
        
        // execute the following code if user selects a file
        if(fileOpened == JFileChooser.APPROVE_OPTION) {
            
            // grab file name and path
            String[] filePaths = {openFile.getSelectedFile().toString(), ""};
            
            // code to convert image to jpg if necessary
            if (filePaths[0].substring(filePaths[0].length() - 3).equals("png")) {
                
                // create images for converting and object for editing
                BufferedImage oldImage = ImageIO.read(openFile.getSelectedFile());
                BufferedImage newImage = new BufferedImage(oldImage.getWidth(), oldImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D editImage = newImage.createGraphics();
                
                // fill new image with white and paste old image on top
                editImage.setColor(Color.WHITE);
                editImage.fillRect(0, 0, newImage.getWidth(), newImage.getWidth());
                editImage.drawImage(oldImage, 0, 0, null);
                editImage.dispose();
                
                // create new file as converted jpg
                ImageIO.write(newImage, "jpg", new File("image-to-convert.jpg"));
                filePaths[1] = openFile.getSelectedFile().toString();
                filePaths[0] = "image-to-convert.jpg";
            }
            
            // loop to continue creating images
            for (int i = 0; i <= 9; i++) {
                for (int k = 0; k <= 9; k++) {                    
                    // don't make image if layers are the same
                    if (i == k) continue;
                    
                    String layer1 = Integer.toString(i);
                    String layer2 = Integer.toString(k);

                    // start python script with file path and layers as arguments
                    // last argument signals this is the loop file
                    ProcessBuilder processBuilder = new ProcessBuilder
                    ("python", System.getProperty("user.dir") + "\\main.py", filePaths[0], filePaths[1], layer1, layer2, "1");
                    Process pythonScript = processBuilder.start();

                    // read output from script for debugging
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pythonScript.getErrorStream()));
                    String pythonOutput = null;
                    while((pythonOutput = bufferedReader.readLine()) != null) {
                        System.out.println(pythonOutput);
                    }
                }
            }

            // delete converted jpg file if exists
            File newFile = new File("image-to-convert.jpg");
            if (newFile.exists()) newFile.delete();
            
        }

    }

}