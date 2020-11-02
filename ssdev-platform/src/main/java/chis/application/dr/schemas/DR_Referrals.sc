<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="DR_Referrals" alias="转诊记录">
	<item id="recordId" alias="记录号" type="int" length="16"
		not-null="1" pkey="true" generator="assigned" hidden="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1" />
		</key>
	</item>
	<item id="reserveNo" alias="转诊单号" type="string" queryable="true" length="16" width="120"/>
	<item id="mpiId" alias="MPIID" type="java.lang.String" length="32" display="0">
	</item>
	<item id="department" alias="转入科室" type="string" length="50"/>
	<item id="departmentCode" alias="转入科室编码" type="string" length="20" display="0">
		<dic id="dr.dictionary.departmentCode"/>
	</item>
	<item id="hospitalCode" alias="转入医院" type="string" length="30" 
		  queryable="true" not-null="1" width="125">
		<dic id="dr.dictionary.hospitalCode"/>
	</item>
	<item id="status" alias="转诊状态" type="string" length="30" >
		<dic id="dr.dictionary.state"/>
	</item>
	<item id="submitTime" alias="转诊日期" type="timestamp"  xtype="datefield" queryable="true" width="150">
	</item>
	<item id="businessType" alias="业务类型" type="string"  length="16" >
		<dic id="dr.dictionary.businessType"/>
	</item>   

	<item id="operator" alias="操作人" type="string"  length="50">
	</item>
	<item id="operationTime" alias="操作时间" type="date" >
	</item>
	<item id="isverify" alias="审核结果" type="string" length="30" >
		<dic id="dr.dictionary.isverify"/>
	</item>

	<item id="turnReason" alias="转出原因" type="string" length="500" xtype="textarea" not-null="1" colspan="3">
	</item>
	<item id="DiseaseDescription" alias="病情描述" type="string" length="500" xtype="textarea" not-null="1" colspan="3">
	</item>
	<item id="announcements" alias="转诊注意事项" type="string" length="500"  xtype="textarea" not-null="1" colspan="3">
	</item>
	<item id="JYJCXM" alias="检验/检查项目" type="string" length="500"  xtype="textarea" not-null="1" colspan="3" display="0">
	</item>
	<item id="JYJCXMJG" alias="检验/检查项目结果" type="string" length="500"  xtype="textarea" not-null="1" colspan="3" display="0">
	</item>
	<item id="KFCSZD" alias="康复措施指导" type="string" length="500"  xtype="textarea" not-null="1" colspan="3" display="0">
	</item>
	<item id="refuseReason" alias="不接收原因" type="string" length="500"  xtype="textarea"  colspan="3">
	</item>	
	<item id="ZZRQ" alias="转诊日期" type="date" display="0">
	</item>
	<item id="JZYS" alias="接诊医生" type="string" length="30" display="0">
	</item>
	<item id="JYZRKS" alias="建议转入科室" type="string" length="50" display="0">
	</item>
	<item id="submitorDoctor" alias="申请医生" type="string" length="30" display="1">
	</item>
	
	<item id="submitAgency" alias="申请机构" type="string" length="30" display="1">  
		<dic id="dr.dictionary.communityCode"/>
	</item>
	
	<item id="wardArea" alias="病区" type="string"  length="50" display="0"/>
	<item id="bedNo" alias="病床号" type="string"  length="50" display="0"/>
	<item id="doctorName" alias="主治医生姓名" type="string"  length="50" display="0"/>
	<item id="doctorPhone" alias="主治医生电话" type="string"  length="50" display="0"/>
	
	<item id="isNew" alias="是否最新状态" type="string" length="1" xtype="displayfield" 
	virtual="true" not-null="1" display="0" colspan="3"/>
	<item id="applyInfo" alias="预约信息" type="string" length="200" xtype="displayfield" 
	virtual="true" not-null="1" display="0" colspan="3"/>
	
	<item id="payType" alias="支付方式"  type="string" not-null="1"  length="1" display="0"/>
	<item id="brxz" alias="病人性质" type="int"  length="1" display="0"/>
	<item id="emrNo" alias="门诊号" type="string"  length="20" display="0"/>
	<item id="hosNo" alias="住院号" type="string"  length="20" display="0"/>
</entry>