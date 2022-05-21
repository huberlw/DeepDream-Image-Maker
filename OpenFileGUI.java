import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;

public class OpenFileGUI extends JFrame {
    private String baseImage;
    private static String openImage;
    private JFrame appWindow;
    public static JPanel imageSpace;
    public static JButton dreamButton;
    private JButton resetButton;
    private JComboBox<String> styleSelect;
    private JComboBox<String> layer1Select;
    private JComboBox<String> layer2Select;
    public JComboBox<String> depthSelect;
    public static JProgressBar dreamProgress;
    private static BufferedImage drawImage;
    private static boolean flag;
    protected File output;
    private JLabel layerLabel;
    private JLabel depthLabel;
    private JLabel styleLabel;
    private JCheckBoxMenuItem advancedItem;
    private DefaultComboBoxModel<String> presets;
    private ArrayList<String> stylePresets;
    private ArrayList<int[]> stylePresetLayers;
    private String[] layerOptions = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
    private String[] depthOptions = { "1", "2", "3", "4", "5", "6" };

    public static void main(String[] args) {     
        new OpenFileGUI().setupGUI();
    }

    private void setupGUI() {
        // create app window
        appWindow = new JFrame("DeepDreamer");
        appWindow.setLayout(new BorderLayout());
        appWindow.setMinimumSize(new Dimension(700, 700));
        appWindow.setLocationRelativeTo(null); // centers window

        // create menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(false); // disables white outline
        menuBar.setBackground(Color.decode("#152232"));
        
        // file options
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Sans", Font.BOLD, 12));
        JMenuItem openItem = new JMenuItem("Open File...");
        JMenuItem urlItem = new JMenuItem("Open URL...");
        JMenuItem saveItem = new JMenuItem("Save as...");
        fileMenu.setForeground(Color.decode("#d3d5f3"));
        fileMenu.add(openItem);
        fileMenu.add(urlItem);
        fileMenu.add(saveItem);

        // advanced settings
        JMenu settingsMenu = new JMenu ("Settings");
        settingsMenu.setFont(new Font("Sans", Font.BOLD, 12));
        advancedItem = new JCheckBoxMenuItem("Advanced options");
        settingsMenu.setForeground(Color.decode("#d3d5f3"));
        settingsMenu.add(advancedItem);
        JMenuItem customPresetItem = new JMenuItem("Create Custom Preset");
        settingsMenu.add(customPresetItem);

        // get help
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("Sans", Font.BOLD, 12));
        JMenuItem infoItem = new JMenuItem("How it Works");
        helpMenu.setForeground(Color.decode("#d3d5f3"));
        helpMenu.add(infoItem);

        JMenuItem layerDepthItem = new JMenuItem("Layers/Depth");
        helpMenu.add(layerDepthItem);
        
        // add to menu bar
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);
        menuBar.add(Box.createVerticalStrut(30));

        // add to app window
        appWindow.add(menuBar, BorderLayout.NORTH);
        
        // create image space
        imageSpace = new JPanel(new BorderLayout());
        imageSpace.setBackground(Color.decode("#0b1622"));
        appWindow.add(imageSpace, BorderLayout.CENTER);
        
        // --- USER OPTIONS ---
        // create options
        JPanel userOptions = new JPanel();
        userOptions.setBackground(Color.decode("#151f2e"));
        userOptions.add(Box.createVerticalStrut(60));
        appWindow.add(userOptions, BorderLayout.SOUTH);

        // style selection ui
        styleLabel = new JLabel("Style");
        styleLabel.setFont(new Font("Sans", Font.PLAIN, 14));
        styleLabel.setForeground(Color.decode("#d3d5f3"));

        // instantiate presets
        presets = new DefaultComboBoxModel<String>(new String[]{"Glitch", "Disease", "Electric"});
        stylePresets = new ArrayList<>(Arrays.asList("Glitch", "Disease", "Electric"));
        stylePresetLayers = new ArrayList<>(Arrays.asList(new int[][]{{ 9, 6 }, { 8, 9 }, { 8, 1 }}));
        
        // style selection
        styleSelect = new JComboBox<String>(presets);
        styleSelect.setPreferredSize(new Dimension(90, 30));
        styleSelect.setFont(new Font("Sans", Font.BOLD, 14));
        userOptions.add(styleLabel);
        userOptions.add(styleSelect);

        // layer selection
        layerLabel = new JLabel("Layers");
        layerLabel.setFont(new Font("Sans", Font.PLAIN, 14));
        layerLabel.setForeground(Color.decode("#d3d5f3"));

        layer1Select = new JComboBox<String>(layerOptions);
        layer1Select.setPreferredSize(new Dimension(45, 30));
        layer1Select.setFont(new Font("Sans", Font.BOLD, 14));
        layer1Select.setSelectedIndex(9);
        layer2Select = new JComboBox<String>(layerOptions);
        layer2Select.setPreferredSize(new Dimension(45, 30));
        layer2Select.setFont(new Font("Sans", Font.BOLD, 14));
        layer2Select.setSelectedIndex(6);
        layer2Select.add(Box.createHorizontalStrut(4));

        // depth selection
        depthLabel = new JLabel("Depth");
        depthLabel.setFont(new Font("Sans", Font.PLAIN, 14));
        depthLabel.setForeground(Color.decode("#d3d5f3"));

        depthSelect = new JComboBox<String>(depthOptions);
        depthSelect.setPreferredSize(new Dimension(45, 30));
        depthSelect.setFont(new Font("Sans", Font.BOLD, 14));
        depthSelect.setSelectedIndex(3);
        
        userOptions.add(layerLabel);
        userOptions.add(layer1Select);
        userOptions.add(layer2Select);
        userOptions.add(depthLabel);
        userOptions.add(depthSelect);
        layerLabel.setVisible(false);
        layer1Select.setVisible(false);
        layer2Select.setVisible(false);
        depthLabel.setVisible(false);
        depthSelect.setVisible(false);

        // dreamify button
        userOptions.add(Box.createHorizontalStrut(4));
        dreamButton = new JButton("Dreamify");
        dreamButton.setPreferredSize(new Dimension(100, 30));
        dreamButton.setFont(new Font("Sans", Font.BOLD, 14));
        dreamButton.setEnabled(false);
        userOptions.add(dreamButton);
        dreamButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // reset button
        userOptions.add(Box.createHorizontalStrut(4));
        resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(100, 30));
        resetButton.setFont(new Font("Sans", Font.BOLD, 14));
        resetButton.setEnabled(false);
        userOptions.add(resetButton);
        dreamButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // --- END USER OPTIONS ---

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
        infoItem.setActionCommand("infoItem");
        infoItem.addActionListener(new ButtonClickListener());
        layerDepthItem.setActionCommand("layerDepthItem");
        layerDepthItem.addActionListener(new ButtonClickListener());
        dreamButton.setActionCommand("dream");
        dreamButton.addActionListener(new ButtonClickListener());
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(new ButtonClickListener());
        styleSelect.setActionCommand("style");
        styleSelect.addActionListener(new ButtonClickListener());
        layer1Select.setActionCommand("layer");
        layer1Select.addActionListener(new ButtonClickListener());
        layer2Select.setActionCommand("layer");
        layer2Select.addActionListener(new ButtonClickListener());
        customPresetItem.setActionCommand("createCustom");
        customPresetItem.addActionListener(new ButtonClickListener());

        // end program when window closes
        appWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
               System.exit(0);
            }        
         }); 
    }

    private int checkIfInteger(String output) {
        try {
            return Integer.parseInt(output);
        }
        catch (NumberFormatException nfe) {
            return -1;
        }
    }

    private void checkFileLength(String fileName, double sizeThreshhold) {
        File file = new File(fileName);
        double bytes = file.length();
        double kilobytes = (bytes / 1024);
        //if file exceeds this size, warning will be issued
        if (kilobytes > sizeThreshhold) JOptionPane.showMessageDialog(appWindow, "This is a large file, dreamification may take a while");
        return;
    }

    private void openFile() {
        // sets look and feel for JFileChooser to os look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("MASSIVE ERROR LMAO!");
        }
        
        // create window to select a jpg or png file
        JFileChooser chooseFile = new JFileChooser(System.getProperty("user.dir"));
        chooseFile.setAcceptAllFileFilterUsed(false);
        chooseFile.addChoosableFileFilter(new FileNameExtensionFilter("JPG or PNG Image", "jpg","jfif","pjpeg", "pjp","png"));
        chooseFile.setDialogTitle("Choose File to Dreamify!");

        if (chooseFile.showOpenDialog(appWindow) == JFileChooser.APPROVE_OPTION) {
            baseImage = chooseFile.getSelectedFile().toString();
            if (baseImage.equals(openImage)) return;
            //warn user if file is large
            checkFileLength(baseImage,120.0);
            setImage(baseImage);
        }

        // reset so it doesn't mess with other components
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("MASSIVE ERROR!");
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
        layer1Select.setEnabled(true);
        layer2Select.setEnabled(true);
        
        appWindow.addComponentListener(new ComponentAdapter( ) {
            public void componentResized(ComponentEvent ev) {
                if (dreamProgress != null)
                    OpenFileGUI.imageSpace.revalidate();
                else
                    loadImage();
            }
        });
    }

    public static void loadImage() {
        // if new image is being set
        if (flag) {
            try {
                drawImage = ImageIO.read(new File(OpenFileGUI.openImage));
            } catch (Exception fileNotFound) {
                JOptionPane.showMessageDialog(null, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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

    private static void openinfoItem() {
        try {
            Desktop.getDesktop().browse(new URI("https://ai.googleblog.com/2015/06/inceptionism-going-deeper-into-neural.html"));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void openLayerDepthItem() {
        JOptionPane.showMessageDialog(appWindow, "Layers impact what image features are recognized and enhanced in any given dream.\n" +
                                                "If your dreamification was unsatisfactory, try changing the preset.\n" + 
                                                "Or enable advanced settings to have even more control\n\n" +
                                                "Depth determines how deep the dreamification goes. A smaller depth will\n" +
                                                "results in faster processing times, but less intense dreams.", "Layers/Depth Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    private static void saveImage(File output) {
       JFileChooser fileChooser = new JFileChooser();
       fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
       fileChooser.setAcceptAllFileFilterUsed(false);
       fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG file", "png"));
       fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG file", "jpg"));

       if (fileChooser.showSaveDialog(imageSpace) == JFileChooser.APPROVE_OPTION) {
           File fileToSave = fileChooser.getSelectedFile();
           String name = fileToSave.toString();

           if (!name.endsWith("png") && fileChooser.getFileFilter().getDescription().equals("PNG")) {
               name += ".png";
               save("png", name, output);
           }
           else if (!name.endsWith("jpg") && fileChooser.getFileFilter().getDescription().equals("JPG")) {
               name += ".jpg";
               save("jpg", name, output);
           }
           else if (fileChooser.getFileFilter().getDescription().equals("JPG")){
               save("jpg", name, output);
           }
           else {
               save("png", name, output);
           }
       }
    }

    private static void save(String type, String name, File output)
    {
        try {
            BufferedImage img = ImageIO.read(output);
            ImageIO.write(img, type, new File(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                            //warn user if file is large
                            checkFileLength(baseImage,120.0);
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
                    if (advancedItem.isSelected()) {
                        layerLabel.setVisible(true);
                        layer1Select.setVisible(true);
                        layer2Select.setVisible(true);
                        depthLabel.setVisible(true);
                        depthSelect.setVisible(true);
                        styleSelect.addItem("Custom");
                    } else {
                        if (styleSelect.getSelectedItem() == "Custom") {
                            styleSelect.setSelectedItem("Glitch");
                            layer1Select.setSelectedIndex(9);
                            layer2Select.setSelectedIndex(6);
                        }
                        layerLabel.setVisible(false);
                        layer1Select.setVisible(false);
                        layer2Select.setVisible(false);
                        depthLabel.setVisible(false);
                        depthSelect.setVisible(true);
                        styleSelect.removeItem("Custom");
                    }
                    break;
                case ("infoItem"):
                    openinfoItem();
                    break;
                case ("layerDepthItem"):    
                    openLayerDepthItem();
                    break;
                case ("dream"):
                    DreamWorker dw = new DreamWorker();
                    dw.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if ("progress".equals(evt.getPropertyName())) {
                                // do progress bar stuff
                            }
                        }
                    });
                    dw.execute();
                    break;
                case ("reset"):

                    setImage(baseImage);
                    break;
                case ("style"):
                    for (int i = 0; i < stylePresets.size(); i++) {
                        if ((styleSelect.getSelectedItem()).equals(stylePresets.get(i))) {
                            layer1Select.setSelectedIndex(stylePresetLayers.get(i)[0]);
                            layer2Select.setSelectedIndex(stylePresetLayers.get(i)[1]);
                            break;
                        }
                    }
                    break;
                case ("layer"):
                    if (layer1Select.getSelectedIndex() == 9) {
                        if (layer2Select.getSelectedIndex() == 6) {
                            styleSelect.setSelectedItem("Glitch");
                            break;
                        }
                    } else if (layer1Select.getSelectedIndex() == 8) {
                        if (layer2Select.getSelectedIndex() == 9) {
                            styleSelect.setSelectedItem("Disease");
                            break;
                        } else if (layer2Select.getSelectedIndex() == 1) {
                            styleSelect.setSelectedItem("Electric");
                            break;
                        }
                    }
                    styleSelect.setSelectedItem("Custom");
                    break;
                case ("createCustom"):
                    // layers input
                    JLabel layerLabel = new JLabel("Layers");

                    JComboBox<String> layerList1 = new JComboBox<String>(layerOptions);
                    layerList1.setPreferredSize(new Dimension(45, 30));
                    layerList1.setFont(new Font("Sans", Font.BOLD, 14));
                    layerList1.setSelectedIndex(0);

                    JComboBox<String> layerList2 = new JComboBox<String>(layerOptions);
                    layerList2.setPreferredSize(new Dimension(45, 30));
                    layerList2.setFont(new Font("Sans", Font.BOLD, 14));
                    layerList2.setSelectedIndex(9);
                    
                    // name input
                    JLabel nameLabel = new JLabel("Preset Name");
                    JTextField textField = new JTextField(10);

                    // panels for main panel
                    JPanel listPanel = new JPanel();
                    listPanel.add(layerLabel);
                    listPanel.add(layerList1);
                    listPanel.add(layerList2);

                    JPanel inputPanel = new JPanel();
                    inputPanel.add(nameLabel);
                    inputPanel.add(textField);

                    JPanel mainPanel = new JPanel(new BorderLayout());
                    mainPanel.add(listPanel, BorderLayout.NORTH);
                    mainPanel.add(inputPanel, BorderLayout.SOUTH);

                    int result = JOptionPane.showConfirmDialog(appWindow, mainPanel, "Choose Layers", JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

                    // user clicks OK
                    if (result == 0) {
                        String name = textField.getText();
                        
                        if (name != null) {
                            // add name to style prests
                            stylePresets.add(name);

                            // add layers to style preset layers
                            int layer1 = Integer.parseInt(layerList1.getSelectedItem().toString()) - 1;
                            int layer2 = Integer.parseInt(layerList2.getSelectedItem().toString()) - 1;
                            stylePresetLayers.add(new int[]{layer1, layer2});

                            // add element to styles
                            presets.addElement(name);
                        }
                    }
                }
            }
        }

    class DreamWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            
            OpenFileGUI.dreamButton.setEnabled(false);

            // run dream
            // get layers from selection
            String layer1 = String.valueOf(Integer.parseInt(layer1Select.getSelectedItem().toString())-1);
            String layer2 = String.valueOf(Integer.parseInt(layer2Select.getSelectedItem().toString())-1);
            String depth = String.valueOf(-Integer.parseInt(depthSelect.getSelectedItem().toString()));

            // create progress bar
            DreamProgress dp = new DreamProgress(depthSelect.getSelectedItem().toString());
            dp.execute();

            // start python script with file path and layers as arguments
            ProcessBuilder startProcess;

            String os = System.getProperty("os.name");
            if (os.contains("Windows"))
                startProcess = new ProcessBuilder("python", System.getProperty("user.dir") + "\\main.py", openImage, layer1, layer2, depth);
            else
                startProcess = new ProcessBuilder("python3", System.getProperty("user.dir") + "\\main.py", openImage, layer1, layer2, depth);

            try {
                Process pythonScript = startProcess.start();
                // read output from script for image file path
                BufferedReader debugging = new BufferedReader(new InputStreamReader(pythonScript.getInputStream()));

                String pythonOutput = null;
                while((pythonOutput = debugging.readLine()) != null) {
                    if (pythonOutput.contains("&&&")) {
                        String dreamImage = pythonOutput.substring(3);
                        setImage("./output/" + dreamImage);
                        OpenFileGUI.dreamButton.setEnabled(true);
                        output = new File("./output/" + dreamImage);
                        resetButton.setEnabled(true);
                        break;
                    }
                    else {
                        int output;
                        if ((output = checkIfInteger(pythonOutput)) > 0) {
                            dreamProgress.setValue(output);

                            int percent = output / Math.abs((Integer.parseInt(depth) - 1));
                            dreamProgress.setString(String.format("Dreamifying image... %d%%", percent));
                        }
                    }
                }

            } catch (IOException noScript) {
                JOptionPane.showMessageDialog(appWindow, "Python script failed!", "Error 117", JOptionPane.ERROR_MESSAGE);
            }

            dreamProgress = null;

            return null;
        }

        @Override
        protected void done() {
            super.done();
        }
    }
}

class ImageLoader extends SwingWorker<Void, Void> {
    @Override
    protected Void doInBackground() throws Exception {
        // add loading image text
        OpenFileGUI.imageSpace.removeAll();
        JLabel imageLoading = new JLabel("Loading image...", SwingConstants.CENTER);
        imageLoading.setFont(new Font("Sans", Font.BOLD, 20));
        imageLoading.setForeground(Color.decode("#d3d5f3"));
        OpenFileGUI.imageSpace.add(imageLoading);
        OpenFileGUI.imageSpace.revalidate();
        return null;
    }

    @Override // only start loading image after loading text is displayed
    protected void done() {
        OpenFileGUI.loadImage();
    }
}

class DreamProgress extends SwingWorker<Void, Void> {
    int max;
    
    DreamProgress(String depth) {
        max = (Integer.parseInt(depth) + 1) * 100;
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        OpenFileGUI.dreamProgress = new JProgressBar(0, max);
        OpenFileGUI.dreamProgress.setValue(0);
        OpenFileGUI.dreamProgress.setOpaque(true);
        OpenFileGUI.dreamProgress.setStringPainted(true);
        OpenFileGUI.dreamProgress.setString("Dreamifying image... 0%");
        OpenFileGUI.dreamProgress.setFont(new Font("Sans", Font.BOLD, 20));
        
        OpenFileGUI.dreamProgress.setBackground(Color.decode("#0b1622"));
        OpenFileGUI.dreamProgress.setForeground(Color.decode("#6382bf"));
        OpenFileGUI.dreamProgress.setBorderPainted(false);

        OpenFileGUI.imageSpace.removeAll();
        OpenFileGUI.imageSpace.add(OpenFileGUI.dreamProgress);
        OpenFileGUI.imageSpace.revalidate();

        return null;
    }

    @Override
    protected void done() {
        super.done();
    }
}
