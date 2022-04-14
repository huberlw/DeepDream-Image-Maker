import java.io.*;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class openFile {

    public static void main(String[] args) throws IOException {
        
        // create window to select a jpg or png file
        JFileChooser openFile = new JFileChooser(System.getProperty("user.dir") + "\\input");
        openFile.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("JPG or PNG file", "jpg", "png");
        openFile.addChoosableFileFilter(extensionFilter);
        int fileOpened = openFile.showOpenDialog(null);
        
        // execute the following code if user selects a file
        if(fileOpened == JFileChooser.APPROVE_OPTION) {
            
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
            
            // grab path to file
            String path = openFile.getSelectedFile().toString();

            // start python script with file path and layers as arguments
            // last argument signals this is not the loop file
            ProcessBuilder processBuilder = new ProcessBuilder("python", System.getProperty("user.dir") + "\\main.py", path, ""+styles[style][0], ""+styles[style][1], "0");
            Process pythonScript = processBuilder.start();

            // read output from script for debugging
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pythonScript.getErrorStream()));
            String pythonOutput = null;
            while((pythonOutput = bufferedReader.readLine()) != null) {
                System.out.println(pythonOutput);
            }

        }

    }

}