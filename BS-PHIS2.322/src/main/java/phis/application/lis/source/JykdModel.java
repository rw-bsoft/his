package phis.application.lis.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.util.context.Context;

import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;

public class JykdModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(JykdModel.class);

	public JykdModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 标本接收状态回写
	 * @author caijy
	 * @createDate 2017-3-12
	 * @description 
	 * @updateInfo
	 * @param xml
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public String bbjszthx(String xml, Context ctx)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();//返回给检查的数据
		Map<String,Object> map_inXml;
		try {
			//检验传过来的xml转成Map
			map_inXml=BSPHISUtil.xml2map_jc(xml);
		} catch (Exception e) {
			//出现异常,返回错误响应
			map_ret.put("MsgHeader", getHeadMessage(false,"xml格式解析错误:"+e.getMessage()));
			return getReturnXml(map_ret);
		}
		//判断下LABREQUEST标签是否为空
		if(!map_inXml.containsKey("LABREQUEST")||map_inXml.get("LABREQUEST")==null||!( map_inXml.get("LABREQUEST") instanceof Map)){
			map_ret.put("MSGHEADER", getHeadMessage(false,"LABREQUEST标签为空或者数据错误"));
			return getReturnXml(map_ret);
			}
		Map<String,Object> map_exmrequest=(Map<String,Object>)map_inXml.get("LABREQUEST");
		//数据完整性校验
		String s_sjjy=sjjy(map_exmrequest, new String[]{"FLAG","YJXHS","PATIENTTYPE"});
		if(!"true".equals(s_sjjy)){
			map_ret.put("MSGHEADER", getHeadMessage(false,s_sjjy));
			return getReturnXml(map_ret);
		}
		int flag=MedicineUtils.parseInt(map_exmrequest.get("FLAG"));//标志位
		String yjxhs=MedicineUtils.parseString(map_exmrequest.get("YJXHS"));//医瞩序号数据集
		String patienttype=MedicineUtils.parseString(map_exmrequest.get("PATIENTTYPE"));//患者类型,空是全部,01门诊,02急诊,03体检,04住院
		StringBuffer hql_update=new StringBuffer();
		List<Long>l_yjxhs=new ArrayList<Long>();
		//由于不字段yjxhs数据格式  转换成list的代码略,以后补上
		//转换代码开始
		//....
		//转换代码结束
		if("01".endsWith(patienttype)){//门诊
			hql_update.append("update MS_YJ01 set ZXPB=:zxpb,ZXRQ=sysdate where YJXH in (:yjxhs)");
		}else {//04住院
			hql_update.append("update YJ_ZY01 set ZXPB=:zxpb,ZXRQ=sysdate where YJXH in (:yjxhs) ");
		}
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("yjxhs", l_yjxhs);
		map_par.put("zxpb", flag);
		int x;
		try {
			x=dao.doUpdate(hql_update.toString(), map_par);
		} catch (PersistentDataOperationException e) {
			map_ret.put("MSGHEADER", getHeadMessage(false,"数据库更新失败:"+e.getMessage()));
			return getReturnXml(map_ret);
		}
		if(x==0){
			map_ret.put("MSGHEADER", getHeadMessage(false,"数据库更新失败:未找到对应记录"));
			return getReturnXml(map_ret);
		}
		map_ret.put("MSGHEADER", getHeadMessage(true,"更新检查状态成功"));
		return getReturnXml(map_ret);
	}
	
	/**
	 * 门诊计费执行情况服务
	 * @author caijy
	 * @createDate 2017-3-12
	 * @description 
	 * @updateInfo
	 * @param xml
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public String mzjfzxqk(String xml, Context ctx)
			throws ModelDataOperationException {
		Map<String,Object> map_ret=new HashMap<String,Object>();//返回给检查的数据
		Map<String,Object> map_inXml;
		try {
			//检验传过来的xml转成Map
			map_inXml=BSPHISUtil.xml2map_jc(xml);
		} catch (Exception e) {
			//出现异常,返回错误响应
			map_ret.put("MsgHeader", getHeadMessage(false,"xml格式解析错误:"+e.getMessage()));
			return getReturnXml(map_ret);
		}
		//判断下LABREQUEST标签是否为空
		if(!map_inXml.containsKey("LABREQUEST")||map_inXml.get("LABREQUEST")==null||!( map_inXml.get("LABREQUEST") instanceof Map)){
			map_ret.put("MSGHEADER", getHeadMessage(false,"LABREQUEST标签为空或者数据错误"));
			return getReturnXml(map_ret);
			}
		Map<String,Object> map_exmrequest=(Map<String,Object>)map_inXml.get("LABREQUEST");
		//数据完整性校验
		String s_sjjy=sjjy(map_exmrequest, new String[]{"SQDID","ZTID"});
		if(!"true".equals(s_sjjy)){
			map_ret.put("MSGHEADER", getHeadMessage(false,s_sjjy));
			return getReturnXml(map_ret);
		}
		long sqid=MedicineUtils.parseLong(map_exmrequest.get("SQDID"));//申请ID
		String ztid=MedicineUtils.parseString(map_exmrequest.get("ZTID"));//项目组合ID
		StringBuffer hql=new StringBuffer();
		hql.append("select FPHM as FPHM from MY_YJ01 where SQID=:sqid");
		Map<String,Object> map_par=new HashMap<String,Object>();
		map_par.put("sqid", sqid);
		try {
			Map<String,Object> map_fp=dao.doLoad(hql.toString(), map_par);
			if(map_fp==null||map_fp.size()==0){
				map_ret.put("MSGHEADER", getHeadMessage(false,"未找到对应申请单号:"+sqid+"的记录"));
				return getReturnXml(map_ret);
			}
			String fphm=MedicineUtils.parseString(map_fp.get("FPHM"));
			map_fp.put("SQDID", sqid);
			map_fp.put("ZTID", ztid);
			map_fp.put("SFBZ", "".equals(fphm)?0:1);
			map_fp.put("FPHM", fphm);
			map_ret.put("MSGHEADER", getHeadMessage(true,""));
			map_ret.put("LABREQUEST", map_fp);
		} catch (PersistentDataOperationException e) {
			map_ret.put("MsgHeader", getHeadMessage(false,"数据查询失败:"+e.getMessage()));
			return getReturnXml(map_ret);
		}	
		return getReturnXml(map_ret);
	}
	
	/**
	 * 拼接返回的head数据
	 * @author caijy
	 * @createDate 2017-3-10
	 * @description 
	 * @updateInfo
	 * @param tag 是否成功,true是成功,false失败
	 * @param c 成功的记录条数,失败传0
	 * @param Detail 响应描述,成功可以直接传""
	 * @return
	 */
	public Map<String,Object> getHeadMessage(boolean tag,String Detail){
		Map<String,Object> map_head=new HashMap<String,Object>();
		map_head.put("SENDER", "基卫");
		map_head.put("STATUS", tag);
		map_head.put("CODE", tag?"":9000);
		map_head.put("MESSAGE", "".equals(Detail)&&tag?"查询检验申请单成功":Detail);
		return map_head;
	}
	/**
	 * 拼接返回xml
	 * @author caijy
	 * @createDate 2017-3-10
	 * @description 
	 * @updateInfo
	 * @param BSXml
	 * @return
	 */
	public String getReturnXml(Map<String,Object> BSXml){
		Map<String,Object> map_ret=new HashMap<String,Object>();
		map_ret.put("BSXML", BSXml);
		return BSPHISUtil.map2xml_jc(map_ret);
	}
	/**
	 * 校验字段的完整性
	 * @author caijy
	 * @createDate 2017-3-11
	 * @description 
	 * @updateInfo
	 * @param map
	 * @param jyzd
	 * @return true是校验通过 否则返回失败字符串
	 */
	public String sjjy(Map<String,Object> map,String[] jyzd){
		StringBuffer s_ret=new StringBuffer();
		for(String key:jyzd){
			if(!map.containsKey(key)||"".equals(MedicineUtils.parseString(map.get(key)))){
				s_ret.append(key).append(",");
			}
		}
		if(s_ret.length()>0){
			return "数据校验失败:"+s_ret.toString()+"为空";
		}
		return "true";
	}
}
