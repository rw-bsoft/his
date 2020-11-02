package chis.source.ehr;

import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;

//【浦口】调用大数据健康档案浏览器接口服务传参加密规则，用于对sign进行加密的规则 zhaojian 2017-10-25
public class DESUtil {
	Key key ;

    public DESUtil() {

    }

    public DESUtil(String str) {
        setKey(str); //
    }

    public Key getKey() {
        return key ;
    }

    public void setKey(Key key) {
        this . key = key;
    }

    /**
     * 
     */
    public void setKey(String strKey) {
        try {
            KeyGenerator _generator = KeyGenerator.getInstance ( "DES" );
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(strKey.getBytes());

            _generator.init(secureRandom);
            this . key = _generator.generateKey();
            _generator = null ;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        }
    }

    /**
     * 
     */
    public String encryptStr(String strMing) {
        byte [] byteMi = null ;
        byte [] byteMing = null ;
        String strMi = "" ;
        BASE64Encoder base64en = new BASE64Encoder();
        try {
            byteMing = strMing.getBytes( "UTF8" );
            byteMi = this .encryptByte(byteMing);
            strMi = base64en.encode(byteMi);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        } finally {
            base64en = null ;
            byteMing = null ;
            byteMi = null ;
        }
        return strMi;
    }

    /**
     * 
     *
     * @param byteS
     * @return
     */
    private byte [] encryptByte( byte [] byteS) {
        byte [] byteFina = null ;
        Cipher cipher;
        try {
            cipher = Cipher.getInstance ( "DES" );
            cipher.init(Cipher. ENCRYPT_MODE , key );
            byteFina = cipher.doFinal(byteS);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        } finally {
            cipher = null ;
        }
        return byteFina;
    }

    /**
     * 
     *
     * @param byteD
     * @return
     */
    private byte [] decryptByte( byte [] byteD) {
        Cipher cipher;
        byte [] byteFina = null ;
        try {
            cipher = Cipher.getInstance ( "DES" );
            cipher.init(Cipher. DECRYPT_MODE , key );
            byteFina = cipher.doFinal(byteD);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error initializing SqlMap class. Cause: " + e);
        } finally {
            cipher = null ;
        }
        return byteFina;
    }

}