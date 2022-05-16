import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class OpenFileGUI extends JFrame {
    private String baseImage;
    private static String openImage;
    private JFrame appWindow;
    public static JPanel imageSpace;
    private JPanel layerSpace;
    private JPanel styleSpace;
    private JButton dreamButton;
    private JButton resetButton;
    private JComboBox<String> styleSelect;
    private JComboBox<String> layer1Select;
    private JComboBox<String> layer2Select;
    private static BufferedImage drawImage;
    private static boolean flag;
    private boolean advancedOptions;
    protected File output;
    public static void main(String[] args) { 
        new OpenFileGUI().setupGUI();
    }

    private void setupGUI() {
		// set up app window
        output = null;
        advancedOptions = false;
        appWindow = new JFrame("DeepDreamer");
        appWindow.setLayout(new BorderLayout());
        appWindow.setMinimumSize(new Dimension(300, 300));
        appWindow.setSize(500, 500);
        appWindow.setLocationRelativeTo(null); // Centers window

        // set up menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(false);
        menuBar.setBackground(Color.DARK_GRAY);
        JMenu fileMenu = new JMenu("File");
        JMenu settingsMenu = new JMenu ("Settings");
        JMenu Help = new JMenu("Help");
        fileMenu.setForeground(Color.WHITE);
        settingsMenu.setForeground(Color.WHITE);
        Help.setForeground(Color.WHITE);
        JMenuItem openItem = new JMenuItem("Open File...");
        JMenuItem urlItem = new JMenuItem("Open URL...");
        JMenuItem saveItem = new JMenuItem("Save as...");
        JCheckBoxMenuItem advancedItem = new JCheckBoxMenuItem("Advanced options");
        JMenuItem documentation = new JMenuItem("How it Works");
        settingsMenu.add(advancedItem);
        Help.add(documentation);
        fileMenu.add(openItem);
        fileMenu.add(urlItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        menuBar.add(Help);
        appWindow.add(menuBar, BorderLayout.NORTH);
        
        // set up image space
        imageSpace = new JPanel(new BorderLayout());
        imageSpace.setBackground(Color.BLACK);
        appWindow.add(imageSpace, BorderLayout.CENTER);
        // ---USER OPTIONS---

        // set up options section
        JPanel userOptions = new JPanel(new BorderLayout());
        appWindow.add(userOptions, BorderLayout.SOUTH);
        JPanel mainOptions = new JPanel();
        mainOptions.setBackground(Color.DARK_GRAY);
        userOptions.add(mainOptions, BorderLayout.CENTER);

        // style selection
        styleSpace = new JPanel();
        styleSpace.setBackground(Color.DARK_GRAY);
        styleSpace.setLayout(new BoxLayout(styleSpace, BoxLayout.Y_AXIS));

        JLabel styleLabel = new JLabel("Style");
        styleLabel.setForeground(Color.WHITE);
        styleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        styleSelect = new JComboBox<String>(new String[] {"Glitch", "Disease", "Electric"});
        styleSelect.setEnabled(false);
        
        styleSpace.add(styleLabel);
        styleSpace.add(Box.createVerticalStrut(2));
        styleSpace.add(styleSelect);
        mainOptions.add(styleSpace);

        // layer selection
        layerSpace = new JPanel();
        layerSpace.setBackground(Color.DARK_GRAY);
        layerSpace.setLayout(new BoxLayout(layerSpace, BoxLayout.Y_AXIS));
        JLabel layerLabel = new JLabel("Layers");
        layerLabel.setForeground(Color.WHITE);
        layerLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        layerSpace.add(layerLabel);
        layerSpace.add(Box.createVerticalStrut(2));

        JPanel layerSelect = new JPanel();
        layerSelect.setLayout(new BoxLayout(layerSelect, BoxLayout.X_AXIS));

        advancedOptions = false;
        String[] layerOptions = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        layer1Select = new JComboBox<String>(layerOptions);
        layer1Select.setSelectedIndex(9);
        layer1Select.setEnabled(false);
        layer2Select = new JComboBox<String>(layerOptions);
        layer2Select.setSelectedIndex(6);
        layer2Select.setEnabled(false);
        
        layerSelect.add(layer1Select);
        layerSelect.add(layer2Select);
        layerSpace.add(layerSelect, BorderLayout.SOUTH);
        mainOptions.add(layerSpace);
        layerSpace.setVisible(false);

        // dreamify button

        JPanel dreamSpace = new JPanel(); // another panel makes the spacing consistent with the other buttons
        dreamSpace.setBackground(Color.DARK_GRAY);
        dreamSpace.setLayout(new BoxLayout(dreamSpace, BoxLayout.Y_AXIS));
        dreamSpace.add(Box.createVerticalStrut(1));
        dreamSpace.add(new JLabel(" "), BorderLayout.NORTH);
        dreamButton = new JButton("Dreamify");
        dreamButton.setEnabled(false);
        dreamSpace.add(dreamButton, BorderLayout.SOUTH);
        mainOptions.add(dreamSpace);
        
        // reset button
        JPanel resetSpace = new JPanel(); // another panel makes the spacing consistent with the other buttons
        resetSpace.setBackground(Color.DARK_GRAY);
        resetSpace.setLayout(new BoxLayout(resetSpace, BoxLayout.Y_AXIS));
        resetSpace.add(Box.createVerticalStrut(1));
        resetSpace.add(new JLabel(" "), BorderLayout.NORTH);
        resetButton = new JButton("Reset");
        resetButton.setEnabled(false);
        resetSpace.add(resetButton, BorderLayout.SOUTH);
        mainOptions.add(resetSpace);

        // ---END USER OPTIONS---


        appWindow.setVisible(true);
        
        // listeners
        openItem.setActionCommand("open");
        openItem.addActionListener(new ButtonClickListener());
        urlItem.setActionCommand("url");
        urlItem.addActionListener(new ButtonClickListener());
        saveItem.setActionCommand("save");
        saveItem.addActionListener(new ButtonClickListener());
        advancedItem.setActionCommand("advanced");
        advancedItem.addActionListener(new ButtonClickListener());
        documentation.setActionCommand("documentation");
        documentation.addActionListener(new ButtonClickListener());
        dreamButton.setActionCommand("dream");
        dreamButton.addActionListener(new ButtonClickListener());
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(new ButtonClickListener());
        styleSelect.setActionCommand("style");
        styleSelect.addActionListener(new ButtonClickListener());
        layer1Select.setActionCommand("layer1");
        layer1Select.addActionListener(new ButtonClickListener());
        layer2Select.setActionCommand("layer1");
        layer2Select.addActionListener(new ButtonClickListener());

        // end program when window closes
        appWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event){
               System.exit(0);
            }        
         }); 
    }

    private void dreamify() {
        // get layes from selection
        String layer1 = String.valueOf(Integer.parseInt(layer1Select.getSelectedItem().toString())-1);
        String layer2 = String.valueOf(Integer.parseInt(layer2Select.getSelectedItem().toString())-1);
        JOptionPane.showMessageDialog(appWindow, "Dreamifying...");
        // start python script with file path and layers as arguments
        ProcessBuilder startProcess = new ProcessBuilder("python", System.getProperty("user.dir") + "\\main.py",
                                    openImage, layer1, 
                                    layer2);
        try {
            Process pythonScript = startProcess.start();
            // read output from script for image file path
            BufferedReader debugging = new BufferedReader(new InputStreamReader(pythonScript.getInputStream()));
            String pythonOutput = null;
            while((pythonOutput = debugging.readLine()) != null) {
	    			if (pythonOutput.contains("&&&")) {
                    String dreamImage = pythonOutput.substring(3, pythonOutput.length());
                    setImage("./output/" + dreamImage);
                    output = new File("./output/" + dreamImage);
                    resetButton.setEnabled(true);
                    break;
                }
            }
            JOptionPane.showMessageDialog(appWindow, "Dreamify Complete!");

        } catch (IOException noScript) {
            JOptionPane.showMessageDialog(appWindow, "Cannot load Python script!", "Error 117", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openFile() {
        // create window to select a "jpg" file
        JFileChooser chooseFile = new JFileChooser(System.getProperty("user.dir"));
        chooseFile.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("JPG file", "jpg");
        chooseFile.addChoosableFileFilter(extensionFilter);

        if (chooseFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            baseImage = chooseFile.getSelectedFile().toString();
            if (baseImage == openImage) return;
            setImage(baseImage);
        }
    }
    
    private void setImage(String imagePath) {    
        // don't set image if already set
        if (openImage != null && openImage.equals(imagePath)) {
            return;
        }

        flag = true; 
        openImage = imagePath; 
            
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.execute();

        // buttons should be usable now that image exists
        dreamButton.setEnabled(true);
        styleSelect.setEnabled(true);
        if (advancedOptions) {
            layer1Select.setEnabled(true);
            layer2Select.setEnabled(true);
        }
        
        appWindow.addComponentListener(new ComponentAdapter( ) {
            public void componentResized(ComponentEvent ev) {
                loadImage();
            }
        });
            
    }

    public static void loadImage() {
        // if new image is being set
        if (flag) {
            try {
                drawImage = ImageIO.read(new File(OpenFileGUI.openImage));
            } catch (Exception fileNotFound) {}
        }

        // get image resolution based on window size
        double drawRatio = 0;
        if (((double)drawImage.getWidth() - imageSpace.getWidth()) > ((double)drawImage.getHeight() - imageSpace.getHeight())) {
            drawRatio = (double)imageSpace.getWidth() / drawImage.getWidth();
        } else {
            drawRatio = (double)imageSpace.getHeight() / drawImage.getHeight();
        }
        BufferedImage drawCanvas = new BufferedImage((int)(drawImage.getWidth()*drawRatio), (int)(drawImage.getHeight()*drawRatio), BufferedImage.TYPE_INT_RGB);
        
        // load image
        Graphics2D draw = drawCanvas.createGraphics();
        draw.drawImage(drawImage, 0, 0, (int)(drawImage.getWidth()*drawRatio), (int)(drawImage.getHeight()*drawRatio), null);
        draw.dispose();
        ImageIcon imageIcon = new ImageIcon(drawCanvas);

        // update image space
        imageSpace.removeAll();
        imageSpace.add(new JLabel(imageIcon));
        imageSpace.revalidate();
        
        flag = false;
    }
    private static void openDocumentation() {
        try {
            Desktop.getDesktop().browse(new URI("https://ai.googleblog.com/2015/06/inceptionism-going-deeper-into-neural.html"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void saveImage(File output) {
           JFileChooser fileChooser = new JFileChooser();
           int returnval = fileChooser.showSaveDialog(imageSpace);
           if (returnval == JFileChooser.APPROVE_OPTION) {
               File fileToSave = fileChooser.getSelectedFile();

               try {
                   BufferedImage img = ImageIO.read(output);
                   ImageIO.write(img, "png", fileToSave);
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           }
    }


    private class ButtonClickListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            switch(command) {
                case ("open"):
                    openFile();
                    break;
                case ("url"):
                    try {
                        // get image from url
                        String url = JOptionPane.showInputDialog(appWindow, "", "Enter a URL that ends in .jpg", JOptionPane.PLAIN_MESSAGE);
                        if (url != null) {
                            String[] split = url.split("/");
                            String fileName = split[split.length - 1];
                            InputStream inputStream = new URL(url).openStream() ;
                            File directory = new File("./input/");
                            if (!directory.exists()){
                                directory.mkdir();
                            }
                            Path path = Paths.get("./input/" + fileName);
                            if (!Files.exists(path)) {
                                Files.copy(inputStream, Paths.get("./input/" + fileName));
                            }
                            baseImage = "./input/" + fileName;
                            setImage(baseImage);
                        }
                    } catch (IOException notValidImage) {
                        JOptionPane.showMessageDialog(appWindow, "Invalid image url!", "Error 303", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                case ("save"):
                    if (output != null) {
                        saveImage(output);
                    }else {

                        break;
}
                case ("advanced"):
                    if (layerSpace.isVisible()) {
                        if (styleSelect.getSelectedItem() == "Custom") {
                            styleSelect.setSelectedItem("Glitch");
                            layer1Select.setSelectedIndex(9);
                            layer1Select.setEnabled(false);
                            layer2Select.setSelectedIndex(6);
                            layer2Select.setEnabled(false);
                        }
                        layerSpace.setVisible(false);
                        styleSelect.removeItem("Custom");
                        layerSpace.revalidate();
                    } else {
                        layerSpace.setVisible(true);
                        styleSelect.revalidate();
                        styleSelect.addItem("Custom");
                        layerSpace.revalidate();
                    }
                    break;
                case ("documentation"):
                    openDocumentation();
                    break;
                case ("dream"):
                    dreamify();
                    break;
                case ("reset"):

                    setImage(baseImage);
                    break;
                case ("style"):
                    if (((String)styleSelect.getSelectedItem()).equals("Glitch")) {
                        layer1Select.setSelectedIndex(9);
                        layer1Select.setEnabled(false);
                        layer2Select.setSelectedIndex(6);
                        layer2Select.setEnabled(false);
                    }
                    else if (((String)styleSelect.getSelectedItem()).equals("Disease")) {
                        layer1Select.setSelectedIndex(8);
                        layer1Select.setEnabled(false);
                        layer2Select.setSelectedIndex(9);
                        layer2Select.setEnabled(false);
                    }
                    else if (((String)styleSelect.getSelectedItem()).equals("Electric")) {
                        layer1Select.setSelectedIndex(8);
                        layer1Select.setEnabled(false);
                        layer2Select.setSelectedIndex(1);
                        layer2Select.setEnabled(false);
                    }
                    else {
                        layer1Select.setEnabled(true);
                        layer2Select.setEnabled(true);
                    }
                    break;
            }

            }

        }		
     }



class ImageLoader extends SwingWorker<Void, Void>{

    protected Void doInBackground() throws Exception {
        // add loading image text
        OpenFileGUI.imageSpace.removeAll();
        JLabel imageLoading = new JLabel("Loading image...", SwingConstants.CENTER);
        imageLoading.setFont(new Font("Sans", Font.BOLD, 16));
        OpenFileGUI.imageSpace.add(imageLoading);
        OpenFileGUI.imageSpace.revalidate();
        return null;
    }

    // only start loading image after loading text is displayed
    protected void done() {
        OpenFileGUI.loadImage();
    }

}
