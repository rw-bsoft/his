<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_ServicePackage" alias="签约服务包">
	<item id="SPID" alias="服务包编号" type="string" length="20" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="manaUtil" alias="所属机构" type="string" length="20" width="180" not-null="1" defaultValue="%user.manageUnit.id" queryable="true">
		<dic id="chis.@manageUnit" filter="['and',['le',['len',['$','item.key']],['i',9]],['ge',['$','item.key'],['s','320124001']],['le',['$','item.key'],['s','320124008']]]"/>
	</item>
	<item id="packageName" alias="服务包名称" type="string" length="50" width="180" not-null="1" queryable="true"/>
	<item id="spPrice" alias="价格" type="double" length="6" not-null="1" precision="2"/>
	<item id="spDiscount" alias="折扣" type="double" length="6" not-null="1" precision="2"/>
	<item id="spRealPrice" alias="实际价格" type="double" length="6" not-null="1" precision="2"/>
	<item id="startUsingDate" alias="启用日期" type="date" width="100" hidden="true"/>
	<item id="validEndDate" alias="有效结止日期" type="date" width="100" hidden="true"/>
	<item id="isUsePrice" alias="是否使用固定价格" type="int" width="100" defaultValue="0" not-null="1">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
  		</dic>
	</item>
	<item id="SFXM" alias="收费项目" type="long" length="18" width="200" not-null="1">
		<dic id="chis.dictionary.jyqysfxm"/>
	</item>
	<item id="packageIntendedPopulation" alias="适应人群" type="string" length="20" width="180">
		<dic id="chis.dictionary.intendedPopulation" render="LovCombo"/>
	</item>
    <item id="sortNumberSP" alias="排序号" type="int" length="3" display="2"/>
    <item id="isGhsfjm" alias="挂号收费是否减免" type="int" length="3" display="2" defaultValue="0" not-null="1">
		<dic>
			<item key="0" text="否"/>
			<item key="1" text="是"/>
  		</dic>
	</item>
	<item id="intro" alias="简介" type="string" xtype="textarea" length="2000"/>
	<item id="remark" alias="备注信息" type="string" xtype="textarea" length="2000"/>
	<item id="type" alias="备注信息" type="string" xtype="textarea" length="100"/>

	<item id="LOGOFF" alias="注销标志" type="int" display="1" length="1" defaultValue="0">
		<dic>
			<item key="0" text="启用"/>
			<item key="1" text="注销"/>
		</dic>
	</item>
</entry>
