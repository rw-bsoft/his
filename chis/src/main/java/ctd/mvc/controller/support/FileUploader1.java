package ctd.mvc.controller.support;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sun.misc.BASE64Decoder;


import ctd.mvc.controller.MVCController;
import ctd.service.core.Service;
import ctd.util.JSONUtils;

@Controller("mvcFileUpload1")
public class FileUploader1 extends MVCController{
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploader1.class);
	

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/**/*.1uploadForm", method = RequestMethod.POST)
	protected void fileUpload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String baseString = request.getParameter("userName");
		String nameId = request.getParameter("nameId");
		GenerateImage(baseString,nameId,request);
		HashMap<String,Object> res = new HashMap<String,Object>();
		HashMap<String,Object> rec = new HashMap<String,Object>();
		int code = 200;
		String msg = "Success";
		res.put(Service.RES_CODE, code);
		res.put(Service.RES_MESSAGE, msg);
		rec.put("file", nameId+".jpg");
		res.put("body", rec);
		writeToResponse(response, res);		
	}
	public boolean GenerateImage(String imgStr,String nameId,HttpServletRequest request)  
    {   //对字节数组字符串进行Base64解码并生成图片  
		if (imgStr == null) //图像数据为空  
            return false;  
        BASE64Decoder decoder = new BASE64Decoder();  
        try   
        {  
            //Base64解码  
            byte[] b = decoder.decodeBuffer(imgStr);  
            for(int i=0;i<b.length;++i)  
            {  
                if(b[i]<0)  
                {//调整异常数据  
                    b[i]+=256;  
                }  
            }  
            //生成jpeg图片 
            String path=request.getRealPath("");
            String imgFilePath = path+"//photo//"+nameId+".jpg";//新生成的图片  
            OutputStream out = new FileOutputStream(imgFilePath);      
            out.write(b);  
            out.flush();  
            out.close();  
            return true;  
        }   
        catch (Exception e)   
        {  
            return false;  
        }  
    }  
	
	private void writeToResponse(HttpServletResponse response,HashMap<String,Object> res){
		response.setContentType("text/html;charset=UTF-8");
		try {
			OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(),"utf-8");
			JSONUtils.write(out, res);
		}  catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	
}
