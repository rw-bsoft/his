package phis.application.reg.ws;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code128Encoder;
import org.jbarcode.encode.InvalidAtributeException;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.TextPainter;
import org.jbarcode.paint.WidthCodedPainter;
import org.jbarcode.util.ImageUtil;

public class buildEwm{
	//设置条形码高度   
			private static final double BARCODE_HEIGHT=40;
			//设置条形码默认分辨率
			private static final int BARCODE_DPI=800;
				//设置条形码字体样式
			private static final String FONT_FAMILY="console";
			//设置条形码字体大小    
			private static final int FONT_SIZE=15;
			//设置条形码文本    
					public static String TEXT= "";
			//创建jbarcode
			private static JBarcode jbc=null;
			static JBarcode getJBarcode() throws InvalidAtributeException{
				if (jbc==null){
					jbc=createCode128();
					jbc.setEncoder(Code128Encoder.getInstance());
					jbc.setTextPainter(CustomTextPainter.getInstance());
					jbc.setBarHeight(BARCODE_HEIGHT);
					jbc.setXDimension(Double.valueOf(0.8).doubleValue());
					jbc.setShowText(true);
				}
				return jbc;
			}
		    public static JBarcode createCode128()
		    {
		        JBarcode jbarcode = new JBarcode(Code128Encoder.getInstance(), WidthCodedPainter.getInstance(), BaseLineTextPainter.getInstance());
		        jbarcode.setBarHeight(17D);
		        try
		        {
		            jbarcode.setXDimension(0.26458333299999998D);
		        }
		        catch(InvalidAtributeException invalidatributeexception) { }
		        jbarcode.setShowText(true);
		        jbarcode.setCheckDigit(true);
		        jbarcode.setShowCheckDigit(false);
		        return jbarcode;
		    }
			 public static void createBarcode(String message, File file,String text) { 
				 try {                  
					 FileOutputStream fos = new FileOutputStream(file);    
					 createBarcode(message, fos,text); 
					 fos.close();            
				 } catch (IOException e) {        
					 throw new RuntimeException(e);              }          }  
			 public static void createBarcode(String message, OutputStream os,String text) {    
				 try {              	
					 //设置条形码文本            	TEXT=text;                
					 //创建条形码的BufferedImage图像                  
					 BufferedImage image = getJBarcode().createBarcode(message);          
					 ImageUtil.encodeAndWrite(image,ImageUtil.PNG,os);
					 os.flush();              } catch (Exception e) { 
						 throw new RuntimeException(e);              }          }       
			 /**          * 静态内部类          * 自定义的 TextPainter， 允许定义字体，大小，文本等          * 参考底层实现：BaseLineTextPainter.getInstance()          */          
			 protected static class CustomTextPainter implements TextPainter {           
				 private static CustomTextPainter instance =new CustomTextPainter();      
				 public static CustomTextPainter getInstance() {                
					 return instance;             }           
				 public void paintText(BufferedImage barCodeImage, String text, int width) {  
					 //绘图                  
					 Graphics g2d = barCodeImage.getGraphics();               
					 //创建字体                
					 Font font = new Font(FONT_FAMILY, Font.PLAIN, FONT_SIZE * width);  
					 g2d.setFont(font);       
					 FontMetrics fm = g2d.getFontMetrics();       
					 int height = fm.getHeight();   
					 int center = (barCodeImage.getWidth() - fm.stringWidth(text)) / 2; 
					 g2d.setColor(Color.WHITE);     
					 g2d.fillRect(0, 0, barCodeImage.getWidth(), barCodeImage.getHeight() * 1 / 20);         
					 g2d.fillRect(0, barCodeImage.getHeight() - (height * 9 / 10), barCodeImage.getWidth(), (height * 9 / 10));       
					 g2d.setColor(Color.BLACK);       
					 //绘制文本              
					 g2d.drawString(TEXT, 0, 145);      
					 //绘制条形码            
					 g2d.drawString(text, center, barCodeImage.getHeight() - (height / 10) - 2); 
					 }          }
			 public static void main(String[] args) throws FileNotFoundException, IOException {
				 List<String> list=new ArrayList<String>();     
				 list.add("KJ4.1-0127-0001");     
				 list.add("KJ4.1-0128-0001");         
				 list.add("KJ4.1-0129-0001");   
				 list.add("KJ4.1-0130-0001");    
				 if(list!=null && list.size()>0){
					 for(String message:list){
						 buildEwm.createBarcode(message,new File("D:\\codeImg\\"+message+".png"),message);             
						 }  
					 }         
				 } 
}
