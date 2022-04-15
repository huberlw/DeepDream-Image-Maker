import java.io.*;
import java.util.Scanner;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;

public class openFile {

    public static void main(String[] args) throws IOException {
        
        // create window to select a jpg or png file
        JFileChooser openFile = new JFileChooser(System.getProperty("user.dir") + "\\input");
        openFile.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("JPG or PNG file", "jpg", "png");
        openFile.addChoosableFileFilter(extensionFilter);
        
        // execute the following code if user selects a file
        if(openFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            
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
            
            // styles with different layer combinations
            int[][] styles = new int[5][2];
            styles[0] = new int[]{7, 9};
            styles[1] = new int[]{5, 3};
            styles[2] = new int[]{3, 8};
            styles[3] = new int[]{1, 2};
            styles[4] = new int[]{6, 4};

            int style = -1;
            Scanner userInput = new Scanner(System.in);
            boolean flag = true;
            
            // loop to get style from user
            // only accepts whole numbers between 0 and 9
            while(flag) {
                try {
                    System.out.println("Enter a style (0-4).");
                    style = Integer.parseInt(userInput.nextLine());
                    if (!(style > -1 && style < 5)) continue;
                    userInput.close();
                    flag = false;
                    System.out.println("Dreamifying...\n");
                } catch (Exception notWholeNumber) {}
            }
            

            // start python script with file path and layers as arguments
            // last argument signals this is not the loop file
            ProcessBuilder processBuilder = new ProcessBuilder
            ("python", System.getProperty("user.dir") + "\\main.py", filePaths[0], filePaths[1], ""+styles[style][0], ""+styles[style][1], "0");
            Process pythonScript = processBuilder.start();

            // read output from script for debugging
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pythonScript.getErrorStream()));
            String pythonOutput = null;
            while((pythonOutput = bufferedReader.readLine()) != null) {
                System.out.println(pythonOutput);
            }

            // delete converted jpg file if exists
            File newFile = new File("image-to-convert.jpg");
            if (newFile.exists()) newFile.delete();

        }

    }

}