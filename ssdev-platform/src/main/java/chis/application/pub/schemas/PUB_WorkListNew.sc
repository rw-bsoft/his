<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.wl.schemas.MDC_Ehr" alias="工作任务">
	<item id="mdcid" type="string" length="16" pkey="true" not-null="1" fixed="true"
		display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="workType" alias="任务名称" type="string" length="2" display="1" width="200">
		<dic id="chis.dictionary.workType" />
	</item>
	<item id="count" alias="任务总数" type="string" display="1" virtual="true" width="100"/>
	<item id="ehrjgdm" alias="管辖机构" type="string" length="20"
		display="0">
		<dic id="manageUnit" includeParentMinLen="6"  render="Tree" parentKey="%user.manageUnit.id"
			rootVisible="true" />
	</item>	
</entry>