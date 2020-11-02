<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hr.schemas.EHR_HealthBackRecord" alias="健康档案退回记录" >
	<item id="phrId" alias="健康档案号" type="string" length="30" display="0" not-null="1" pkey="true"/>
	<item id="backTimes" alias="退回次数" type="int" length="3" display="0" not-null="1" pkey="true"/>
	<item id="empiId" alias="empiId" type="string" length="32" display="0"/>
	<item id="backDate" alias="退回日期" type="date" length="11" width="100" maxValue="%server.date.today" display="0"/>
	<item id="backReason" alias="退回原因" type="string" length="300" width="200" colspan="2" xtype="textarea"/>
	<item id="backPerson" alias="退回人" type="string" length="20" width="160" display="0"/>
	<item id="lastVerifyLevel" alias="被退回人级别" type="string" length="1" width="120" display="0"/>
	<item id="lastVerifyPerson" alias="被退回人" type="string" length="20" width="360" display="0"/>
</entry>