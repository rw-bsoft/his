/**
 * @(#)PIXProxy.java Created on Oct 13, 2009 3:12:36 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.empi;

import com.bsoft.pix.protocol.PIXGetEmpiIdRequest;
import com.bsoft.pix.protocol.PIXGetLocalIdRequest;
import com.bsoft.pix.protocol.PIXLockCardRequest;
import com.bsoft.pix.protocol.PIXMergePersonRequest;
import com.bsoft.pix.protocol.PIXQueryPersonRequest;
import com.bsoft.pix.protocol.PIXRegisterCardRequest;
import com.bsoft.pix.protocol.PIXSubmitPersonRequest;
import com.bsoft.pix.protocol.PIXUnlockCardRequest;
import com.bsoft.pix.protocol.PIXUpdatePersonRequest;
import com.bsoft.pix.protocol.PIXWriteOffCardRequest;
import com.bsoft.pix.protocol.PIXWriteOffPersonRequest;
import com.bsoft.pix.server.embedded.EmbeddedResponse;
import com.bsoft.pix.server.embedded.PIXHandlerDispather;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class PIXProxy {

	private PIXHandlerDispather pixHandlerDispather;
 
	/**
	 * 获取个人的EmpiId.
	 * 
	 * @param requestXML
	 *            一个xml形式的请求字符串,应该包含姓名及其他能确定其身份的信息.
	 * @return
	 */
	public EmbeddedResponse getEmpiId(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXGetEmpiIdRequest.REQ_TYPE);
	}

	/**
	 * 获取个人的详细信息.
	 * 
	 * @param requestXML
	 *            一个xml形式的请求字符串 ,应该包含姓名及其他能确定其身份的信息.
	 * @return
	 */
	public EmbeddedResponse getPerson(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXQueryPersonRequest.REQ_TYPE);
	}

	/**
	 * 提交一条个人信息.
	 * 
	 * @param requestXML
	 *            一个xml形式的请求字符串,应该包含姓名及其他能确定其身份的信息.
	 * @return
	 */
	public EmbeddedResponse submitPerson(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXSubmitPersonRequest.REQ_TYPE);
	}

	/**
	 * 注销一条个人信息。
	 * 
	 * @param requestXML
	 * @return
	 */
	public EmbeddedResponse writeOffPerson(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXWriteOffPersonRequest.REQ_TYPE);
	}

	/**
	 * 获取在某个机构里的LocalId。
	 * 
	 * @param requestXML
	 * @return
	 */
	public EmbeddedResponse getLocalId(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXGetLocalIdRequest.REQ_TYPE);
	}

	/**
	 * 冻结（挂失）一张卡。
	 * 
	 * @param requestXML
	 * @return
	 */
	public EmbeddedResponse lockCard(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXLockCardRequest.REQ_TYPE);
	}

	/**
	 * 合并个人信息。
	 * 
	 * @param requestXML
	 * @return
	 */
	public EmbeddedResponse mergePerson(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXMergePersonRequest.REQ_TYPE);
	}

	/**
	 * 注册卡。
	 * 
	 * @param requestXML
	 * @return
	 */
	public EmbeddedResponse registerCard(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXRegisterCardRequest.REQ_TYPE);
	}

	/**
	 * 解冻结（解挂失）卡。
	 * 
	 * @param requestXML
	 * @return
	 */
	public EmbeddedResponse unlockCard(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXUnlockCardRequest.REQ_TYPE);
	}

	/**
	 * 更新个人信息。
	 * 
	 * @param requestXML
	 * @return
	 */
	public EmbeddedResponse updatePerson(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXUpdatePersonRequest.REQ_TYPE);
	}

	/**
	 * 注销卡。
	 * 
	 * @param requestXML
	 * @return
	 */
	public EmbeddedResponse writeOffCard(String requestXML) {
		return pixHandlerDispather.handle(requestXML,
				PIXWriteOffCardRequest.REQ_TYPE);
	}

	public PIXHandlerDispather getPixHandlerDispather() {
		return pixHandlerDispather;
	}

	public void setPixHandlerDispather(PIXHandlerDispather pixHandlerDispather) {
		this.pixHandlerDispather = pixHandlerDispather;
	}
}
