package Helpers;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;

import ANN.Network;

public class GUI extends JFrame {
	private static int[] images = IDXReader.read("test-images.idx3-ubyte");
	private static int imagesIndex = 0;
	private static String image;
	private static String prediction;
	public GUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(null);
		
		JTextPane testImage = new JTextPane();
		testImage.setFont(new Font("Monospaced", Font.BOLD, 14));
		testImage.setEditable(false);
		testImage.setBackground(Color.WHITE);
		testImage.setBounds(10, 24, 387, 468);
		getContentPane().add(testImage);
		
		JTextPane predictionValue = new JTextPane();
		predictionValue.setFont(new Font("Monospaced", Font.BOLD, 98));
		predictionValue.setEditable(false);
		predictionValue.setBackground(Color.WHITE);
		predictionValue.setBounds(455, 160, 109, 103);
		getContentPane().add(predictionValue);
		
		JButton newImageBtn = new JButton("New Image");
		newImageBtn.setBackground(Color.WHITE);
		newImageBtn.setFont(new Font("Roboto", Font.BOLD, 16));
		newImageBtn.setBounds(429, 305, 126, 45);
		getContentPane().add(newImageBtn);
		setResizable(false);
		setTitle("Handwriting Recognition");
		setBounds(0, 0, 600, 600);
		newImageBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] newImage = Arrays.copyOfRange(images, imagesIndex, imagesIndex + IDXReader.PIXELSPERIMAGE);
				newTestCase(newImage);
				testImage.setText(image);
				predictionValue.setText(prediction);
				imagesIndex += IDXReader.PIXELSPERIMAGE;
			}
		});
		
		setVisible(true);
	}
	
	public static void newTestCase(int[] newImage) {
		StringBuilder sb = new StringBuilder();
		int newPrediction = Network.predict(newImage);
		for (int i = 0; i < newImage.length; i++) {
			if(i % 28 == 0)
				sb.append("\n");
			sb.append(newImage[i] > 0 ? 1 : " ");
		}
		image = sb.toString();
		prediction = String.valueOf(newPrediction);
	}
}

