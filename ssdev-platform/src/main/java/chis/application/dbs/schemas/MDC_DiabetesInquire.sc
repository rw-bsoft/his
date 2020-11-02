<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.dbs.schemas.MDC_DiabetesInquire" alias="糖尿病询问记录" sort="inquireDate desc">
	<item id="inquireId" alias="标识列" type="string" length="16"
		not-null="1" pkey="true" fixed="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>

	<item id="phrId" alias="档案编号" type="string" length="30" width="165"
		fixed="true" display="0" />
	<item id="empiId" alias="empiid" type="string" length="32"
		fixed="true" colspan="3" hidden="true" />
	<item id="inquireDate" alias="询问日期" type="date" defaultValue="%server.date.today"  maxValue="%server.date.today">
	</item>
	<item id="inquireWay" alias="询问方式" type="string" length="1"
		not-null="1">
		<dic>
			<item key="1" text="电话" />
			<item key="2" text="上门" />
			<item key="3" text="门诊" />
		</dic>
	</item>

	<item id="fbs" alias="空腹血糖" type="double" length="6" />
	<item id="pbs" alias="餐后血糖" type="double" length="6" />

	<item id="description" alias="措施描述" type="string" xtype="textarea"
		length="100" colspan="2" display="2" />
	<item id="inquireUnit" alias="询问机构" type="string" length="20"
		update="false" fixed="true" width="165"
		defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inquireUser" alias="询问人员" type="string" length="20"
		update="false" fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" 
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
</entry>
