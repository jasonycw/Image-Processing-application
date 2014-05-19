import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.JToolBar.*;
import javax.swing.filechooser.FileFilter;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImageFrame extends JFrame implements ActionListener,
		ChangeListener, MouseMotionListener, MouseListener {
	private JDesktopPane theDesktop;
	private JInternalFrame frameOriginal;
	private JInternalFrame frameProcessed;
	private JInternalFrame textProcessed;
	private JPanel toolBarPanel;

	// ------------------------------------------

	private JToolBar functionToolBar;
	private JButton openButton;
	private JButton saveButton;
	private JButton undoButton;
	private JButton redoButton;

	// ------------------------------------------

	private JToolBar toolbar;
	private JSlider rgRange;
	private JSlider rbRange;
	private JSlider gbRange;
	private int rgRangeValue;
	private int rbRangeValue;
	private int gbRangeValue;
	private JLabel xyVal;
	private JLabel redVal;
	private JLabel greenVal;
	private JLabel blueVal;

	// ------------------------------------------

	private JScrollPane pane;
	private ImagePanel imagePanelOriginal;
	private ImagePanel imagePanelProcessed;
	private JTextPane textPanelResult;
	private StyledDocument sdoc;
	private boolean[][] mask;
	private JLabel infoBar;

	// ------------------------------------------

	private JMenuBar menuBar;;
	private JMenu menuFile;
	private JMenu menuEdit;
	private JMenu menuProcessing;
	private JMenu menuLookAndFeel;
	private JMenu menuHelp;

	// ------------------------------------------

	private JMenuItem menuItemOpen;
	private JMenuItem menuItemSave;
	private JMenuItem menuItemExit;
	private JMenuItem menuItemUndo;
	private JMenuItem menuItemRedo;
	private JMenuItem menuItemBokehBlurring;
	private JMenuItem menuItemGrayscale;
	private JMenuItem menuItemInvert;
	private JMenuItem menuItemBrightness;
	private JMenuItem menuItemPreservingPartColor;
	private JMenuItem menuItemRandomPixelMovement;
	private JMenuItem menuItemSpinning;
	private JMenuItem menuItemPinching;
	private JMenuItem menuItemPixelate;
	private JMenuItem menuItemConvertToASCII;

	private JRadioButtonMenuItem menuItemMetal;
	private JRadioButtonMenuItem menuItemMotif;
	private JRadioButtonMenuItem menuItemWindows;

	private JMenuItem menuItemAbout;

	// ------------------------------------------

	private BufferedImage inputImage;
	private BufferedImage resultImage;
	private JScrollPane scrollPane;
	private String title;
	private Vector imageBuffer;
	private Vector messageBuffer;
	private int curImageIndex;

	private Rectangle rect;
	private int pointColor[];
	private boolean isSelect;

	private final int IMAGE_SIZE_LIMIT = 10;

	public ImageFrame(String tit) {
		theDesktop = new JDesktopPane(); // create desktop pane
		add(theDesktop); // add desktop pane to frame

		toolBarPanel = new JPanel();

		functionToolBar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
		functionToolBar.setBackground(Color.white);
		openButton = new JButton(new ImageIcon("open.png"));
		openButton.setBackground(Color.white);
		openButton.addActionListener(this);
		saveButton = new JButton(new ImageIcon("save.png"));
		saveButton.setBackground(Color.white);
		saveButton.addActionListener(this);
		undoButton = new JButton(new ImageIcon("undo.png"));
		undoButton.setBackground(Color.white);
		undoButton.addActionListener(this);
		redoButton = new JButton(new ImageIcon("redo.png"));
		redoButton.setBackground(Color.white);
		redoButton.addActionListener(this);
		functionToolBar.add(openButton);
		functionToolBar.add(saveButton);
		functionToolBar.add(new JToolBar.Separator());
		functionToolBar.add(undoButton);
		functionToolBar.add(redoButton);

		toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
		toolbar.setBackground(Color.white);

		toolBarPanel.setLayout(new GridLayout(0, 1));
		toolBarPanel.add(functionToolBar);
		toolBarPanel.add(toolbar);
		toolbar.setVisible(false);
		toolBarPanel.setBackground(Color.white);
		getContentPane().add(toolBarPanel, BorderLayout.NORTH);

		rgRange = new JSlider(JSlider.HORIZONTAL, 0, 80, 40);
		rgRange.setBackground(Color.white);
		rgRange.addChangeListener(this);
		rbRange = new JSlider(JSlider.HORIZONTAL, 0, 80, 40);
		rbRange.setBackground(Color.white);
		rbRange.addChangeListener(this);
		gbRange = new JSlider(JSlider.HORIZONTAL, 0, 80, 40);
		gbRange.setBackground(Color.white);
		gbRange.addChangeListener(this);

		xyVal = new JLabel("          ");
		xyVal.setBackground(Color.white);
		redVal = new JLabel("      ");
		redVal.setBackground(Color.white);
		greenVal = new JLabel("      ");
		greenVal.setBackground(Color.white);
		blueVal = new JLabel("      ");
		blueVal.setBackground(Color.white);

		toolbar.add(new JLabel("(x,y): "));
		toolbar.add(xyVal);
		toolbar.add(new JLabel("(R,G,B) "));
		toolbar.add(redVal);
		toolbar.add(greenVal);
		toolbar.add(blueVal);

		toolbar.add(new JLabel("Red-Green"));
		toolbar.add(rgRange);
		toolbar.add(new JToolBar.Separator());
		toolbar.add(new JLabel("Red-Blue"));
		toolbar.add(rbRange);
		toolbar.add(new JToolBar.Separator());
		toolbar.add(new JLabel("Green-Blue"));
		toolbar.add(gbRange);

		title = tit;
		menuBar = new JMenuBar();

		menuFile = new JMenu("File");
		menuItemOpen = new JMenuItem("Open");
		menuItemOpen.addActionListener(this);
		menuItemSave = new JMenuItem("Save as..");
		menuItemSave.addActionListener(this);
		menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(this);
		menuFile.add(menuItemOpen);
		menuFile.add(menuItemSave);
		menuFile.addSeparator();
		menuFile.add(menuItemExit);
		menuItemSave.setEnabled(false);

		menuEdit = new JMenu("Edit");
		menuItemUndo = new JMenuItem("Undo");
		menuItemRedo = new JMenuItem("Redo");
		menuItemUndo.addActionListener(this);
		menuItemRedo.addActionListener(this);
		menuEdit.add(menuItemUndo);
		menuEdit.add(menuItemRedo);
		menuItemUndo.setEnabled(false);
		menuItemRedo.setEnabled(false);

		menuProcessing = new JMenu("Processing");
		menuItemBokehBlurring = new JMenuItem("Bokeh Blurring");
		menuItemGrayscale = new JMenuItem("Grayscale");
		menuItemInvert = new JMenuItem("Invert");
		menuItemBrightness = new JMenuItem("Brightness");

		menuItemPreservingPartColor = new JMenuItem("Preserving Partial Colors");
		menuItemRandomPixelMovement = new JMenuItem("Random Pixel Movement");
		menuItemSpinning = new JMenuItem("Spinning");
		menuItemPinching = new JMenuItem("Pinching");
		menuItemPixelate = new JMenuItem("Pixelate");
		menuItemConvertToASCII = new JMenuItem("Image to ASCII");

		menuItemBokehBlurring.addActionListener(this);
		menuItemGrayscale.addActionListener(this);
		menuItemInvert.addActionListener(this);
		menuItemBrightness.addActionListener(this);
		menuItemPreservingPartColor.addActionListener(this);
		menuItemRandomPixelMovement.addActionListener(this);
		menuItemSpinning.addActionListener(this);
		menuItemPinching.addActionListener(this);
		menuItemPixelate.addActionListener(this);
		menuItemConvertToASCII.addActionListener(this);

		menuProcessing.add(menuItemBokehBlurring);
		menuProcessing.add(menuItemGrayscale);
		menuProcessing.add(menuItemInvert);
		menuProcessing.add(menuItemBrightness);
		menuProcessing.add(menuItemPreservingPartColor);
		menuProcessing.add(menuItemRandomPixelMovement);
		menuProcessing.add(menuItemSpinning);
		menuProcessing.add(menuItemPinching);
		menuProcessing.add(menuItemPixelate);
		menuProcessing.add(menuItemConvertToASCII);

		menuItemBokehBlurring.setEnabled(false);
		menuItemGrayscale.setEnabled(false);
		menuItemInvert.setEnabled(false);
		menuItemBrightness.setEnabled(false);
		menuItemPreservingPartColor.setEnabled(false);
		menuItemRandomPixelMovement.setEnabled(false);
		menuItemSpinning.setEnabled(false);
		menuItemPinching.setEnabled(false);
		menuItemPixelate.setEnabled(false);
		menuItemConvertToASCII.setEnabled(false);

		menuProcessing.setEnabled(false);

		menuLookAndFeel = new JMenu("Look and Feel");
		menuItemMetal = new JRadioButtonMenuItem("Metal");
		menuItemMotif = new JRadioButtonMenuItem("Motif");
		menuItemWindows = new JRadioButtonMenuItem("Windows");
		menuItemMetal.addActionListener(this);
		menuItemMotif.addActionListener(this);
		menuItemWindows.addActionListener(this);
		menuLookAndFeel.add(menuItemMetal);
		menuLookAndFeel.add(menuItemMotif);
		menuLookAndFeel.add(menuItemWindows);
		ButtonGroup lookAndFeelGroup = new ButtonGroup();
		lookAndFeelGroup.add(menuItemMetal);
		lookAndFeelGroup.add(menuItemMotif);
		lookAndFeelGroup.add(menuItemWindows);
		menuItemMetal.setSelected(true);

		menuHelp = new JMenu("Help");
		menuItemAbout = new JMenuItem("About");
		menuItemAbout.addActionListener(this);
		menuHelp.add(menuItemAbout);

		infoBar = new JLabel(" Info.:");
		infoBar.setFont(new Font("Arial", Font.PLAIN, 12));

		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuProcessing);
		menuBar.add(menuLookAndFeel);
		menuBar.add(menuHelp);

		imagePanelOriginal = new ImagePanel(rect);
		imagePanelOriginal.addMouseMotionListener(this);
		imagePanelOriginal.addMouseListener(this);

		imagePanelProcessed = new ImagePanel(rect);
		getContentPane().add(infoBar, BorderLayout.SOUTH);
		setIconImage(new ImageIcon("icon.jpg").getImage());

		inputImage = null;
		resultImage = null;
		mask = null;
		pointColor = new int[3];

		rgRangeValue = 40;
		rbRangeValue = 40;
		gbRangeValue = 40;

		isSelect = false;
	}

	public void showWin() {
		setSize(800, 600); // set frame size
		setTitle(title);
		setJMenuBar(menuBar);
		setLocationRelativeTo(null);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public Image getInputImage() {
		return inputImage;
	}

	public Image getResultImage() {
		return resultImage;
	}

	private void imageFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open a file");
		fileChooser
				.addChoosableFileFilter(new ExtensionFileDialogFilter("jpg"));

		int returnVal = fileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			try {
				inputImage = ImageIO.read(file);
				resultImage = ImageIO.read(file);
				imagePanelOriginal.setImage(inputImage);
				imagePanelProcessed.setImage(resultImage);

				// ---------------------------
				frameOriginal = new JInternalFrame("Original Image", true,
						true, true, true);
				frameOriginal.add(imagePanelOriginal, BorderLayout.CENTER);
				frameOriginal.pack();
				theDesktop.add(frameOriginal);
				frameOriginal.setVisible(true);

				frameProcessed = new JInternalFrame("Processed Image", true,
						true, true, true);
				frameProcessed.add(imagePanelProcessed, BorderLayout.CENTER);
				frameProcessed.pack();
				theDesktop.add(frameProcessed);

				textPanelResult = new JTextPane();
				sdoc = textPanelResult.getStyledDocument();

				textProcessed = new JInternalFrame("Processed Image", true,
						true, true, true);
				textProcessed.add(textPanelResult, BorderLayout.CENTER);
				textProcessed.pack();
				theDesktop.add(textProcessed);

				scrollPane = new JScrollPane(textPanelResult);
				BufferedImage bufImg = ImageProcessor.convert(inputImage);
				scrollPane.setPreferredSize(new Dimension(bufImg.getWidth(),
						bufImg.getHeight()));
				textProcessed.add(scrollPane, BorderLayout.CENTER);
				textProcessed.pack();

				JInternalFrame[] frames = theDesktop.getAllFrames();
				int x = 10;
				int y = 10;
				for (int i = frames.length - 1; i >= 0; i--) {
					frames[i].setLocation(x, y);
					x += 30;
					y += 30;
				}

				getContentPane().add(theDesktop);
				// ---------------------------

				imageBuffer = new Vector();
				imageBuffer.add(inputImage);
				messageBuffer = new Vector();
				curImageIndex = 0;
				menuItemSave.setEnabled(true);
				menuItemUndo.setEnabled(true);
				menuItemRedo.setEnabled(true);
				menuItemBokehBlurring.setEnabled(true);
				menuItemGrayscale.setEnabled(true);
				menuItemInvert.setEnabled(true);
				menuItemBrightness.setEnabled(true);
				menuItemPreservingPartColor.setEnabled(true);
				menuItemRandomPixelMovement.setEnabled(true);
				menuItemSpinning.setEnabled(true);
				menuItemPinching.setEnabled(true);
				menuItemPixelate.setEnabled(true);
				menuItemConvertToASCII.setEnabled(true);

				infoBar.setText(" Info.: Filename = " + file.getName()
						+ ", Image's size = " + inputImage.getWidth() + " x "
						+ inputImage.getHeight());
				messageBuffer.add(infoBar.getText());

				menuProcessing.setEnabled(true);
			} catch (IOException e) {
			}
		}
	}

	private void imageFileSave() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save file as..");
		fileChooser
				.addChoosableFileFilter(new ExtensionFileDialogFilter("jpg"));

		int returnVal = fileChooser.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String filePath = fileChooser.getSelectedFile().getPath() + ".jpg";

			if (filePath != null) {
				try {
					File file = new File(filePath);
					ImageIO.write(resultImage, "jpg", file);
				} catch (IOException e) {
				}
			}
		}
	}

	private void imageUndo() {
		if (curImageIndex > 0)
			curImageIndex--;
		imagePanelProcessed.setImage((Image) imageBuffer
				.elementAt(curImageIndex));
		infoBar.setText((String) messageBuffer.elementAt(curImageIndex));
	}

	private void imageRedo() {
		if (curImageIndex < imageBuffer.size() - 1)
			curImageIndex++;
		imagePanelProcessed.setImage((Image) imageBuffer
				.elementAt(curImageIndex));
		infoBar.setText((String) messageBuffer.elementAt(curImageIndex));
	}

	private void addToImageBuffer(Image image) {
		if (curImageIndex < imageBuffer.size() - 1) {
			int last = imageBuffer.size() - 1;
			for (int i = curImageIndex + 1; i <= last; i++) {
				imageBuffer.remove(imageBuffer.size() - 1);
				messageBuffer.remove(messageBuffer.size() - 1);
			}
		}
		if (imageBuffer.size() >= IMAGE_SIZE_LIMIT) {
			imageBuffer.remove(0);
			messageBuffer.remove(0);
		}
		imageBuffer.add(image);
		messageBuffer.add(infoBar.getText());
		curImageIndex++;
	}

	private void imageBokehBlurring() {
		resultImage = ImageProcessor.bokehBlurring(inputImage);
		imagePanelProcessed.setImage(resultImage);
		infoBar.setText(" Info.: Blurring the image with Bokeh blurring");
		addToImageBuffer(resultImage);
		textProcessed.setVisible(false);
		frameProcessed.setVisible(true);
	}

	private void imageGrayscale() {
		resultImage = ImageProcessor.toGrayScale(inputImage);
		imagePanelProcessed.setImage(resultImage);
		infoBar.setText(" Info.: Color image is converted to grayscale");
		addToImageBuffer(resultImage);
		textProcessed.setVisible(false);
		frameProcessed.setVisible(true);
	}

	private void imageInvert() {
		resultImage = ImageProcessor.invertImage(inputImage);
		imagePanelProcessed.setImage(resultImage);
		infoBar.setText(" Info.: Color image is inverted");
		addToImageBuffer(resultImage);
		textProcessed.setVisible(false);
		frameProcessed.setVisible(true);
	}

	private void imageBrightness() {
		String str = JOptionPane.showInputDialog(this,
				"Enter amount of brightness adjusted", "Input",
				JOptionPane.QUESTION_MESSAGE);
		int nBrightness = Integer.parseInt(str);
		resultImage = ImageProcessor.brighteningImage(inputImage, nBrightness);
		imagePanelProcessed.setImage(resultImage);
		infoBar.setText(" Info.: Brightness of color image is adjusted");
		addToImageBuffer(resultImage);
		textProcessed.setVisible(false);
		frameProcessed.setVisible(true);
	}

	private void randomPixelMovement() {
		String str = JOptionPane.showInputDialog(this, "Enter pixel offset",
				"Input", JOptionPane.QUESTION_MESSAGE);
		int offset = Integer.parseInt(str);
		resultImage = ImageProcessor.randomPixelMovement(inputImage, offset);
		imagePanelProcessed.setImage(resultImage);
		infoBar.setText(" Info.: Pixels are randomly moved");
		textProcessed.setVisible(false);
		frameProcessed.setVisible(true);
	}

	private void spinning() {
		String str = JOptionPane.showInputDialog(this, "Enter degree of swirl",
				"Input", JOptionPane.QUESTION_MESSAGE);
		double deg = Double.parseDouble(str);
		resultImage = ImageProcessor.spinning(inputImage, deg);
		imagePanelProcessed.setImage(resultImage);
		infoBar.setText(" Info.: Image is spinned");
		textProcessed.setVisible(false);
		frameProcessed.setVisible(true);
	}

	private void pixelate() {
		String str = JOptionPane.showInputDialog(this, "Enter pixel size",
				"Input", JOptionPane.QUESTION_MESSAGE);
		int size = Integer.parseInt(str);
		resultImage = ImageProcessor.pixelate(inputImage, size);
		imagePanelProcessed.setImage(resultImage);
		infoBar.setText(" Info.: Image is pixelated");
		textProcessed.setVisible(false);
		frameProcessed.setVisible(true);
	}

	private void pinching() {
		resultImage = ImageProcessor.pinching(inputImage);
		imagePanelProcessed.setImage(resultImage);
		infoBar.setText(" Info.: Image is pinched");
		textProcessed.setVisible(false);
		frameProcessed.setVisible(true);
	}

	private void preservingPartColor() {
		mask = new boolean[inputImage.getHeight()][inputImage.getWidth()];
		for (int j = 0; j < inputImage.getHeight(); j++)
			for (int i = 0; i < inputImage.getWidth(); i++)
				mask[j][i] = false;

		toolbar.setVisible(true);
		theDesktop.updateUI();
		textProcessed.setVisible(false);
		frameProcessed.setVisible(true);
		infoBar.setText(" Info.: Some colors are preserved");
	}

	private void imageToASCII() {
		char[][] asciiResult = ImageProcessor.imageToASCII(inputImage);

		try {
			sdoc.remove(0, sdoc.getLength());
		} catch (Exception e) {
		}

		int offset = 0;
		StyleContext context = new StyleContext();
		Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setFontSize(style, 10);
		StyleConstants.setLineSpacing(style, -0.7f);

		try {
			for (int j = 0; j < asciiResult.length; j++) {
				for (int i = 0; i < asciiResult[0].length; i++) {
					String s = Character.toString(asciiResult[j][i]);
					sdoc.insertString(offset, s, style);
					sdoc.setParagraphAttributes(offset, 1, style, true);
					offset++;
				}
				sdoc.insertString(offset, "\n", null);
				offset++;
			}
		} catch (Exception e) {
		}

		frameProcessed.setVisible(false);
		textProcessed.setVisible(true);
		textProcessed.repaint();

		infoBar.setText(" Info.: Color image is converted to ASCII");
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == menuItemOpen)
			imageFileChooser();
		else if (source == openButton)
			imageFileChooser();
		else if (source == menuItemSave)
			imageFileSave();
		else if (source == saveButton)
			imageFileSave();
		else if (source == menuItemExit)
			System.exit(0);
		else if (source == menuItemBokehBlurring)
			imageBokehBlurring();
		else if (source == menuItemGrayscale)
			imageGrayscale();
		else if (source == menuItemInvert)
			imageInvert();
		else if (source == menuItemBrightness)
			imageBrightness();
		else if (source == menuItemPreservingPartColor)
			preservingPartColor();
		else if (source == menuItemRandomPixelMovement)
			randomPixelMovement();
		else if (source == menuItemSpinning)
			spinning();
		else if (source == menuItemPinching)
			pinching();
		else if (source == menuItemPixelate)
			pixelate();
		else if (source == menuItemConvertToASCII)
			imageToASCII();
		else if (source == menuItemUndo)
			imageUndo();
		else if (source == undoButton)
			imageUndo();
		else if (source == menuItemRedo)
			imageRedo();
		else if (source == redoButton)
			imageRedo();
		else if (source == menuItemAbout) {
			AboutDialog ad = new AboutDialog(this);
			ad.setVisible(true);
		} else {
			String str = "";
			if (source == menuItemMetal)
				str = "javax.swing.plaf.metal.MetalLookAndFeel";
			else if (source == menuItemMotif)
				str = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
			else if (source == menuItemWindows)
				str = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

			try {
				UIManager.setLookAndFeel(str);
				SwingUtilities.updateComponentTreeUI(this);
			} catch (Exception exception) {
			}
		}
		repaint();
	}

	public synchronized void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		int value;
		if (source == rgRange)
			rgRangeValue = rgRange.getValue();
		else if (source == rbRange)
			rbRangeValue = rbRange.getValue();
		else if (source == gbRange)
			gbRangeValue = gbRange.getValue();
		System.out.println("State is change " + rgRangeValue + " "
				+ rbRangeValue + " " + gbRangeValue);
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		int val = inputImage.getRGB(e.getX(), e.getY());
		Color color = new Color(val);
		xyVal.setText(e.getX() + " " + e.getY() + "    ");
		redVal.setText(color.getRed() + ", ");
		greenVal.setText(color.getGreen() + ", ");
		blueVal.setText(color.getBlue() + "    ");
		repaint();
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			int val = inputImage.getRGB(e.getX(), e.getY());
			Color color = new Color(val);
			pointColor[0] = color.getRed();
			pointColor[1] = color.getGreen();
			pointColor[2] = color.getBlue();
			resultImage = ImageProcessor.preservingPartColor(inputImage, mask,
					color.getRGB(), rgRangeValue, rbRangeValue, gbRangeValue);
			imagePanelProcessed.setImage(resultImage);
			imagePanelProcessed.repaint();
			System.out.println("Mouse Right is pressed");
			addToImageBuffer(resultImage);
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}
}