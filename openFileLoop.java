import java.io.*;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;

public class OpenFileLoop {

    public static void main(String[] args) throws IOException {
        
        // create window to select a jpg or png file
        JFileChooser chooseFile = new JFileChooser(System.getProperty("user.dir"));
        chooseFile.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("JPG or PNG file", "jpg", "png");
        chooseFile.addChoosableFileFilter(extensionFilter);
        
        // execute the following code if user selects a file
        if(chooseFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            
            // grab file name and path
            String filePath = chooseFile.getSelectedFile().toString();
            
            Boolean deleteFile = false;

            // convert image to jpg if necessary
            if (filePath.substring(filePath.length() - 3).equals("png")) {
                
                // create images objects for conversion
                BufferedImage baseImage = ImageIO.read(chooseFile.getSelectedFile());
                BufferedImage newImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                
                Graphics2D editImage = newImage.createGraphics();
                
                // convert to png
                editImage.setColor(Color.WHITE);
                editImage.fillRect(0, 0, newImage.getWidth(), newImage.getWidth());
                editImage.drawImage(baseImage, 0, 0, null);
                editImage.dispose();
                
                // create file and adjust path
                filePath = chooseFile.getSelectedFile().toString().replace(".png", ".jpg");
                ImageIO.write(newImage, "jpg", new File(filePath));

                deleteFile = true;
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
                    ProcessBuilder startProcess = new ProcessBuilder("python", System.getProperty("user.dir") 
                                                + "\\main.py", filePath, layer1, layer2, "1");
                    Process pythonScript = startProcess.start();

                    // read output from script for debugging
                    BufferedReader debugging = new BufferedReader(new InputStreamReader(pythonScript.getErrorStream()));
                    String pythonOutput = null;
                    while((pythonOutput = debugging.readLine()) != null) {
                        System.out.println(pythonOutput);
                    }
                }
            }

            // delete newImage jpg file if exists
            if (deleteFile) {
                File newFile = chooseFile.getSelectedFile();
                newFile.delete();
            }

        }

    }

}