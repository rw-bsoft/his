<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.fhr.schemas.EHR_FamilyRecord"  alias="家庭健康档案" sort="familyId desc">
	<item id="familyId" pkey="true" alias="家庭编号" type="string"
		length="17" queryable="true"  generator="assigned"  width="160">
	  <key>
		<Rule name="areaCode" defaultFill="0" length="12" fillPos="after" type="string">
      		%codeCtx.regionCode
	  	</Rule>
		<Rule name="increaseId" defaultFill="0" type="increase" length="5" startPos="1"/>
		</key>
	</item>
	<item id="ownerName" alias="户主姓名" type="string" length="20" queryable="true" width="160"/>
	<item id="manaDoctorId" alias="责任医生" type="string" length="20" queryable="true" update="false" width="160">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" />
	</item>
	<item id="regionCode" alias="网格地址" type="string" length="25" 
		queryable="true" update="false" width="160">
		<dic id="chis.dictionary.areaGrid_family" includeParentMinLen="6" filterMin="10" filterMax="18" render="Tree" onlySelectLeaf="true"  />
	</item>
	<item id="regionCode_text" type="string" length="200" display="0" width="160"/>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" queryable="true" width="160">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="familyHome" alias="家庭电话" type="string" length="50" queryable="true" width="160"/>
	<item id="createUser" alias="建档人" type="string" length="20"  queryable="true" update="false"  width="160">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" /><!-- parentKey="%user.manageUnit.id" 901-->
	</item>
	<item id="createDate" alias="建档日期" type="date" queryable="true" width="160" maxValue="%server.date.today"/>
</entry>