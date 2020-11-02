package ctd.service;

import java.util.HashMap;
import java.util.List;

import phis.source.utils.SequenceUtil;

import ctd.sequence.KeyManager;
import ctd.sequence.exception.KeyManagerException;
import ctd.util.context.ContextUtils;
import ctd.util.exception.CodedBaseException;

public class KeyCreator {

	public static String create(String entryName,
			List<HashMap<String, String>> rec, String pkey)
			throws CodedBaseException {
		return create(entryName, rec, pkey, ContextUtils.getContext());
	}

	// 此处对整个create方法进行synchronized，高并发下对性能影响较大，只需要对entryName进行锁定即可
	// 验证
	public static String create(String entryName,
			List<HashMap<String, String>> rec, String pkey, Object obj)
			throws CodedBaseException {
		String key = null;
		try {
			/**
			 * 进入条件 type="sequence"
			 * seqName可选，如果配置了，就取seqName对应的序列，如果没有配置，则默认为seq_{entryName}
			 * 增加序列方式（oracle等支持序列的,mysql除外）,根据配置的seqName获取数据库序列
			 * 若未找到对应序列，则自动创建序列(查找对应entryName表的最大值作为起始值创建序列)
			 */
			// ****************begin****************
			for (HashMap<String, String> m : rec) {
				if (m.containsKey("type")
						&& ("sequence".equals(m.get("type")) || "increase"
								.equals(m.get("type")))) {
					return SequenceUtil.getKey(entryName, m, pkey, obj);
				} 
			}
			// ****************end******************
			synchronized (entryName) {
				if (KeyManager.isLeader()) {
					key = KeyManager.getKeyByName(entryName, rec, pkey, obj);
				} else {
					key = KeyManager.RemoteLoader(entryName, rec, pkey, obj);
				}
			}
		} catch (KeyManagerException e) {
			throw new CodedBaseException(e.getMessage());
		}
		if (key == null) {
			throw new CodedBaseException("keyId create failed,check rules");
		}
		return key;
	}
}
