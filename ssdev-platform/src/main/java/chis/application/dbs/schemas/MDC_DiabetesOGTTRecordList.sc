<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesOGTTRecord" alias="糖尿病高危档案" sort="a.OGTTID desc">
	<item id="OGTTID" alias="管理编号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item ref="b.planDate"  display="1" queryable="true"/>
	<item ref="b.visitDate"  display="1" queryable="true"/>
	<item id="phrId"  alias="档案编号" type="string" length="30" width="165" hidden="true" display="0" fixed="true"/>
	<item id="empiId" alias="empiid" type="string" length="32" fixed="true" display="0" colspan="3" hidden="true"/>
	<item id="registerDate" alias="登记日期" type="date" defaultValue="%server.date.today" queryable="true">
	</item>
	<item id="nextScreenDate" alias="下次筛查时间" display="0" type="date" defaultValue="%server.date.today" queryable="true">
	</item>
	<relations>
        <relation type="parent" entryName="chis.application.pub.schemas.PUB_VisitPlan"> 
          <join parent="recordId" child="phrId"/>  
        </relation>
	</relations>
</entry>
