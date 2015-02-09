package com.akjava.gwt.lbp.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.lbp.ByteImageDataIntConverter;
import com.akjava.gwt.lib.client.experimental.lbp.SimpleLBP;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public class GWTLBPTest implements EntryPoint {

	private VerticalPanel rootPanel;
	private VerticalPanel main;
	private CheckBox improvedCheck;
	private ListBox neighborListBox;

	private ImageElement lastImageElement;
	private Button reconvertBt;
	public void onModuleLoad() {
		rootPanel = new VerticalPanel();
		RootPanel.get("main").add(rootPanel);
		
		
		main = new VerticalPanel();
		
		
		HorizontalPanel h1=new HorizontalPanel();
		h1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		h1.setSpacing(2);
		
		Button test=new Button("Test lena1.jpg",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				new ImageElementLoader().load("lena1.png", new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						loadImage(element);
					}
					
					@Override
					public void onError(String url, ErrorEvent event) {
						LogUtils.log(event);
					}
				});
			}
		});
		rootPanel.add(h1);
		h1.add(test);
		
		FileUploadForm fupload=FileUtils.createSingleFileUploadForm(new DataURLListener() {
			@Override
			public void uploaded(File file, String text) {
				ImageElement image=ImageElementUtils.create(text);
				loadImage(image);
				
			}
		}, false);
		h1.add(fupload);
		
		
		improvedCheck = new CheckBox();
		
		h1.add(improvedCheck);
		h1.add(new Label("Improved-LBP"));
		
		
		h1.add(new Label("Neighbors:"));
		neighborListBox = new ListBox();
		for(int i=1;i<=10;i++){
			neighborListBox.addItem(""+i);
		}
		neighborListBox.setSelectedIndex(0);
		
		
		reconvertBt = new Button("Re-Convert",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				loadImage(lastImageElement);
			}
		});
		
		
		h1.add(neighborListBox);
		h1.add(new Label("Samples:8(Fixed)"));
		
		h1.add(reconvertBt);
		reconvertBt.setEnabled(false);
		
		rootPanel.add(main);
	}
	
	private void loadImage(ImageElement element){
		reconvertBt.setEnabled(true);
		lastImageElement=element;
		main.clear();
		Image image=new Image(element.getSrc());
		main.add(image);
		createLBP(element);
	}

	protected void createLBP(ImageElement element) {
		
		Canvas canvas=CanvasUtils.createCanvas(element);
		
		Canvas graycanvas=CanvasUtils.createCanvas(element);
		CanvasUtils.convertToGrayScale(canvas,graycanvas);
		//main.add(graycanvas);//for debug grayscale image
		
		ByteImageDataIntConverter converter=new ByteImageDataIntConverter(canvas.getContext2d(),true);
		
		int[][] bytes=converter.convert(canvas.getContext2d().getImageData(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight()));
		
		boolean improved=improvedCheck.getValue();
		int neighbors=Integer.parseInt(neighborListBox.getValue(neighborListBox.getSelectedIndex()));
		
		SimpleLBP lbp3=new SimpleLBP(improved,neighbors);
		int[][] convertedPixel=lbp3.convert(bytes);
	
		ImageData imageData=converter.reverse().convert(convertedPixel);
		
		CanvasUtils.copyTo(imageData, canvas);
		main.add(new Image(canvas.toDataUrl()));
		
	}
	
}
