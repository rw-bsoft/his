/**
 * @Project: znmh_platform
 * @Title: EncryptMethod3Des.java
 * @Package com.hoperun.framework.ssi.freemarker.method
 * @author wu_shenliang1
 * @date 2015年5月12日 
 * @Copyright: © 2015 HopeRun Inc. All rights reserved.
 * @version V1.0  
 */
package chis.source.ws;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

/**
 * 
 * @Description TODO
 * @author wu_shenliang1
 * @version 1.0
 */
public class EncryptMethodDesUrl 
{
    
    public String encrypt(List params)
    {
        String content = params.get(0).toString();
        String key = params.get(1).toString();
        String algorithm = params.get(2).toString();
        
        String result = encrypt(content, key, algorithm);
        return result;
    }
    
    public String decrypt(List params)
    {
        String content = params.get(0).toString();
        String key = params.get(1).toString();
        String algorithm = params.get(2).toString();
        
        String result = decrypt(content, key, algorithm);
        
        return result;
    }
    //加密
    public static String encrypt(String content, String keyStr, String algorithm)
    {
        String result = null;
        try
        {
            SecretKey secretKey = createKey(keyStr, algorithm);
            
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] b = cipher.doFinal(content.getBytes("UTF-8"));
            
            result = new String(Base64.encode(b));
            result = URLEncoder.encode(result);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        
        return result;
    }
    //解密
    public static String decrypt(String hexContent, String keyStr,
            String algorithm)
    {
        String result = null;
        try
        {
            hexContent = URLDecoder.decode(hexContent);
            byte[] decodedContent = Base64.decode(hexContent);
            
            SecretKey secretKey = createKey(keyStr, algorithm);
            
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] b = cipher.doFinal(decodedContent);
            
            result = new String(b);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public static SecretKey createKey(String keyStr, String algorithm)
            throws UnsupportedEncodingException
    {
        byte keyByte[] = keyStr.getBytes("UTF-8");
        return new SecretKeySpec(keyByte, algorithm);
    }
    
    public static void main(String[] args)
    {
        String miwen = "8dzLbIhTh3d5D163fE719UxdECmPyEx3";
        
        //String mingwen=decrypt("8dzLbIhTh3d5D163fE719UxdECmPyEx3","mynj1234","des");
        
        String mingwen = "320124199209102422";
        
        System.out.println(mingwen);
        
        System.out.println(encrypt(mingwen, "zhls4321", "des"));
    }
    
}
