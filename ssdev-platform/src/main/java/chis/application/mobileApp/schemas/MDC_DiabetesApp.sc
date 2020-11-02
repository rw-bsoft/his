<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.dbs.schemas.MDC_DiabetesVisit" alias="糖尿病随访" version="1344848127031" filename="D:\workspace\BSCHIS\WebRoot\WEB-INF\config\schema\mdc/MDC_DiabetesVisit.xml"> 
	<item id="visitId" pkey="true" alias="随访标识" type="string" length="16" hidden="true" display="0"> 
		<key> 
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
		</key> 
	</item>  
	<item id="empiId" alias="EMPIID" type="string" length="32"
		not-null="1" display="0" />
	<item id="visitDate" alias="随访日期" type="date" not-null="1" queryable="true"/>  

	<item id="visitWay" alias="随访方式" type="string" length="1" not-null="true"> 
		<dic id="chis.dictionary.visitWay"/> 
	</item>  



</entry>
