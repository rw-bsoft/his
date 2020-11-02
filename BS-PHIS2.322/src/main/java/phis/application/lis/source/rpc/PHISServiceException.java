package phis.application.lis.source.rpc;

/**
 * @description
 * @author <a href="mailto:chengzx@bsoft.com.cn">chzhxiang</a>
 */
public class PHISServiceException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private int code = 0;
	 
	   public PHISServiceException()
	   {
	   }
	 
	   public PHISServiceException(String message) {
	     super(message);
	   }
	 
	   public PHISServiceException(Throwable t) {
	     super(t);
	   }
	 
	   public PHISServiceException(String message, Throwable t) {
	     super(message, t);
	   }
	 
	   public PHISServiceException(int code, String message) {
	     this(message);
	     this.code = code;
	   }
	 
	   public PHISServiceException(int code, String message, Throwable t) {
	     this(message, t);
	     this.code = code;
	   }
	 
	   public int getCode() {
	     return this.code;
	   }
	 
	   public void setCode(int code) {
	     this.code = code;
	   }
}
