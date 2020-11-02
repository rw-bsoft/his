<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="drIt_sendExchangeReport" alias="接收下转申请">
	<item id="recordId" alias="记录号" type="string" length="16"
		not-null="1" pkey="true" generator="assigned" hidden="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1" />
		</key>
  </item>
  <item id="exchangeNo" alias="预约单号" length="7" queryable="true" width="90" colspan="3" display="0"/>
  <item id="reserveNo" alias="转诊单号" length="7" queryable="true"  display="0" />
  <item ref="b.cardTypeCode" not-null="0"/>
	<item ref="b.cardNo" queryable="true" not-null="0"/>
	<item ref="b.personName" not-null="1"/>
	<item ref="b.idCard" queryable="true" not-null="1"/>
	<item ref="b.sexCode" not-null="1"/>
	<item ref="b.birthday" not-null="1"/>
	<item id="brxz" alias="病人性质" length="18" not-null="1" defaultValue="1000" >
		<dic id="chis.dictionary.patientPropertiesForDr" />
	</item>
	<item ref="b.contactNo" not-null="1"/>
    <item id="mpiId" alias="MPIID" type="string" length="32" width="120"  display="0"/>
	<item id="payType" alias="支付方式" type="string" length="20" not-null="1" >
		<dic id="chis.dictionary.payModeForDr"/>
	</item>
	<item id="emrNo" alias="门诊号" type="string" length="20"/>
	<item id="hosNo" alias="住院号" type="string" length="20"/>
  <item id="hospitalCode" alias="下转医院" length="32" width="120"  not-null="1" queryable="true">
  	<dic id="chis.dictionary.communityCode"/>
  </item>
  <item id="exchangeTime" alias="下转日期" type="date"  not-null="1" />
  <item id="doctorName" alias="主治医生" length="20" type="string" not-null="1">
  	<!--<dic id="chis.dictionary.doctorCode"/>-->
  </item>
  <item id="submitor" alias="提交人" length="20" type="string" not-null="1" >
  	<!--<dic id="chis.dictionary.doctorCode"/>-->
  </item>
  <item id="submitAgency" alias="提交机构" length="120"  not-null="1" >
    <dic id="chis.dictionary.hospitalCode"/>
  </item>
  <item id="submitTime" alias="提交时间" length="80" type="timestamp" display="0"/>
  <item id="treatResult" alias="治疗结果" length="20" colspan="2" not-null="1" />
  <item id="turnReason" alias="转诊原因" length="1000" colspan="3" hidden="true" />
  <item id="healthAdvice" alias="建议康复内容" length="200" width="120" xtype="textarea" not-null="1" colspan="3"/>
  <item id="leaveConclusion" alias="出院小结" length="2000" width="120" xtype="textarea" not-null="1" colspan="3"/>
	
	<relations>
		<relation type="parent" entryName="chis.application.dr.schemas.DR_DemographicInfo"/>
	</relations>
</entry>
