<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="DR_Referrals" alias="转诊记录" sort="submitTime desc">
	<item id="recordId" alias="记录号" type="string" length="16"
		not-null="1" pkey="true" generator="assigned" hidden="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1" />
		</key>
	</item>
	<item id="reserveNo" alias="转诊单号" type="java.lang.String" length="7" update="false" colspan="3"/>
	<item ref="b.cardTypeCode" not-null="1"/>
	<item ref="b.cardNo" not-null="0"/>
	<item ref="b.personName" not-null="1" queryable="true"/>
	<item ref="b.idCard" not-null="1" queryable="true"/>
	<item ref="b.sexCode" not-null="1" queryable="true"/>
	<item ref="b.birthday" not-null="1" queryable="true"/>
	<item id="brxz" alias="病人性质" length="18" not-null="0" defaultValue="1000" update="false">
		<dic id="chis.dictionary.patientPropertiesForDr" />
	</item>
	<item ref="b.contactNo" not-null="0"/>
	<item id="operationTime" alias="转诊日期" type="date" width="90" 
		queryable="true" not-null="1" />
	<item id="mpiId" alias="MPIID" type="java.lang.String" length="32" display="0"/>
	<item id="payType" alias="支付方式" type="string" length="20" update="false">
		<dic id="chis.dictionary.payModeForDr"/>
	</item>
	<item id="businessType" alias="业务类型" type="string"  not-null="0" length="16" update="false" display="0">
		<dic id="chis.dictionary.businessType"/>
	</item>   
	<item id="emrNo" alias="门诊号" type="string" length="20" update="false" queryable="true"/>
	<item id="hosNo" alias="住院号" type="string" length="20" update="false" queryable="true"/>
	<item id="hospitalCode" alias="转入医院" type="string" length="30" 
		  not-null="1" width="125" update="false" queryable="true">
		<dic id="chis.dictionary.hospitalCode"/>
	</item>
	<item id="department" alias="转入科室" type="string" length="50" display="0" update="false"/>
	<item id="departmentCode" alias="转入科室" type="string" length="20" display="0" update="false">
		<dic id="chis.dictionary.departmentCode"/>
	</item>
	<item id="operator" alias="操作人" type="string"  not-null="0" length="50" update="false"  display="0">
		<dic id="chis.dictionary.doctorCode"/>
	</item>
	<item id="submitorDoctor" alias="申请医生" type="string" not-null="0" length="30" update="false" queryable="true"/>
	<item id="submitAgency" alias="申请机构" type="string" not-null="1" length="30" update="false"  queryable="true"/>
	<item id="ZZRQ" alias="转诊日期" type="date" not-null="1" queryable="true"/>
	<item id="JZYS" alias="接诊医生" type="string" not-null="0" length="30" queryable="true"/>
	<item id="JYZRKS" alias="建议转入科室" type="string" length="50" queryable="true"/>
	<item id="turnReason" alias="转出原因" type="string" length="500" xtype="textarea" not-null="1" colspan="3" update="false">
	</item>
	<item id="DiseaseDescription" alias="病情描述" type="string" length="500" xtype="textarea" not-null="1" colspan="3" update="false">
	</item>
	<item id="announcements" alias="转诊注意事项" type="string" length="500"  xtype="textarea" not-null="1" colspan="3" update="false">
	</item>
	<item id="JYJCXM" alias="检验/检查项目" type="string" length="500" xtype="textarea" not-null="1" colspan="3" update="false">
	</item>
	<item id="JYJCXMJG" alias="检验/检查项目结果" type="string" length="500" xtype="textarea" not-null="1" colspan="3" update="false">
	</item>
	<item id="KFCSZD" alias="康复措施指导" type="string" length="500" xtype="textarea" not-null="1" colspan="3" update="false">
	</item>
	<item id="applyInfo" alias="预约信息" type="string" length="200" xtype="displayfield" 
		virtual="true" not-null="0"  colspan="3" display="0"/>
	
	<relations>
		<relation type="parent" entryName="chis.application.dr.schemas.DR_DemographicInfoForm"/>
	</relations>
</entry>