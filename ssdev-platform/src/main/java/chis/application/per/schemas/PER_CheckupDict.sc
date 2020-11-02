<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.per.schemas.PER_CheckupDict" alias="项目字典" sort="orderNo asc">
	<item id="checkupProjectId" pkey="true" alias="项目编号" type="string" length="16"
		width="160" not-null="1" fixed="true"
		generator="assigned" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="285"/>
		</key>
	</item>
	<item id="orderNo" alias="序号" type="int" />
	<item id="checkupProjectName" alias="项目名称" type="string" length="60" not-null="1" queryable="true" width="150"/>
	<item id="checkupUnit" alias="项目单位"/>
	<item id="referenceLower" alias="参考下限" type="double" length="10" precision="2"/>
	<item id="referenceUpper" alias="参考上限" type="double" length="10" precision="2"/>
	<item id="projectOffice" alias="检查科室" type="string" length="16" hidden="true"/>
	<item id="projectOfficeCode" alias="项目科室代码" type="string" length="16" hidden="true">
		<dic id="chis.dictionary.projectOffice"/>
	</item>
	<item id="projectType" alias="项目类型" type="string" not-null="1" length="2" width="140" queryable="true">
		<dic id="chis.dictionary.perCheckDicType"/>
	</item>
	<item id="projectClass" alias="项目类别" type="string" length="2" hidden="true"/>

	<item id="checkupDic" alias="体检字典" type="string" length="20" >
		<dic id="chis.dictionary.checkupDic"/>
	</item>
	<item id="memo" alias="备注" colspan="2" type="string" length="100" width="200"/>
</entry>
