import java.io.*;
import java.util.Scanner;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;

public class OpenFile {

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
            
            // styles with different layer combinations (taken from eistein-candidates)
            int[][] styles = new int[3][2];
            styles[0] = new int[]{8, 9};
            styles[1] = new int[]{8, 1};
            styles[2] = new int[]{9, 6};

            int style = -1;
            Scanner userInput = new Scanner(System.in);
            boolean flag = true;
            
            // loop to get style from user
            // only accepts whole numbers between 0 and 9
            while(flag) {
                try {
                    System.out.println("Enter a style (0-2).");
                    style = Integer.parseInt(userInput.nextLine());
                    if (!(style > -1 && style < 5)) continue;
                    userInput.close();
                    flag = false;
                    System.out.println("Dreamifying...\n");
                } catch (Exception notWholeNumber) {}
            }
            

            // start python script with file path and layers as arguments
            // last argument signals this is not the loop file
            ProcessBuilder startProcess = new ProcessBuilder("python", System.getProperty("user.dir") 
                                            + "\\main.py", filePath, ""+styles[style][0], ""+styles[style][1], "0");
            Process pythonScript = startProcess.start();

            // read output from script for debugging
            BufferedReader debugging = new BufferedReader(new InputStreamReader(pythonScript.getErrorStream()));
            String pythonOutput = null;
            while((pythonOutput = debugging.readLine()) != null) {
                System.out.println(pythonOutput);
            }

            // delete newImage jpg file if exists
            if (deleteFile) {
                File newFile = chooseFile.getSelectedFile();
                newFile.delete();
            }

        }

    }

}