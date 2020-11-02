<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpm.schemas.MPM_FieldMaintainCommonQuery" tableName="MPM_FieldMaintain" sort="fieldId desc" alias="数据结构维护">
	<item id="fieldId" alias="字段编号" type="string" length="16" not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="alias" alias="字段名称" type="string" length="100"  width="300" queryable="true"/>
	<item id="id" alias="字段代码" type="string" length="30" width="160" queryable="true"/>
	<item id="type" alias="类型" type="string" length="10" queryable="true">
		<dic>
			<item key="string" text="字符串"/>
			<item key="int" text="整型"/>
			<item key="date" text="日期"/>
			<item key="datetime" text="时间"/>
			<item key="bigDecimal" text="小数型"/>
			<item key="double" text="浮点型"/>
		</dic>
	</item>
	<item id="dicRender" alias="是否字典" type="string" queryable="true">
		<dic>
			<item key="0" text="非字典"/>
			<item key="1" text="单选"/>
			<item key="2" text="多选"/>
			<item key="3" text="纯文本"/>
		</dic>
	</item>
	<item id="notNull" alias="是否必输" type="string" queryable="true">
		<dic>
			<item key="1" text="是"/>
			<item key="0" text="否"/>
		</dic>
	</item>
	<item id="defaultValue" alias="默认值" type="string" length="100"/>
	<item id="length" alias="长度" type="int" length="5" not-null="1"/>
	<item id="useType" alias="适用类别" type="string" length="2" queryable="true">
		<dic id="chis.dictionary.PHQUseType" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="pyCode" alias="拼音码" type="string" length="50">
		<set type="exp" run="server">['py',['$','r.alias']]</set>
	</item>
	<item id="remark" alias="备注" type="string" length="100"/>
	
	<item id="inputUser" alias="录入人" type="string" length="20" update="false" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user06" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false" width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入时间" type="datetime" update="false" defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20"
		width="180" display="1" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		fixed="true" defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.date']</set>
	</item>
</entry>