<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpm.schemas.MPM_FieldMaintain" alias="数据结构维护">
	<item id="fieldId" alias="字段编号" length="16" not-null="1" generator="assigned"  pkey="true" type="string" width="160"
		hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="alias" alias="字段名称" length="1000" width="160" not-null="1" queryable="true"/>
	<item id="id" alias="字段代码" length="30" width="160" not-null="1" queryable="true"/>
	<item id="type" alias="类型" type="string" length="10" not-null="1" queryable="true">
		<dic>
			<item key="string" text="字符串"/>
			<item key="int" text="整型"/>
			<item key="date" text="日期"/>
			<item key="datetime" text="时间"/>
			<item key="bigDecimal" text="小数型"/>
			<item key="double" text="浮点型"/>
		</dic>
	</item>
	<item id="dicRender" alias="是否字典" type="string" not-null="1">
		<dic>
			<item key="0" text="非字典"/>
			<item key="1" text="单选"/>
			<item key="2" text="多选"/>
		</dic>
	</item>	
	<item id="notNull" alias="是否必输" type="string" not-null="1">
		<dic>
			<item key="1" text="是"/>
			<item key="0" text="否"/>
		</dic>
	</item>
	<item id="defaultType" alias="默认值类型" length="1" not-null="1" defaultValue="9">
		<dic>
			<item key="1" text="当前登陆人"/>
			<item key="2" text="当前登陆机构"/>
			<item key="3" text="当前登录日期"/>
			<item key="4" text="当前登录时间"/>
			<item key="9" text="其他"/>
		</dic>
	</item>
	<item id="defaultValue" alias="默认值" length="100"/>
	<item id="length" alias="长度" type="int" length="5" not-null="1"/>
	<item id="pyCode" alias="拼音码" type="string" length="50" queryable="true">
		<set type="exp" run="server">['py',['$','r.alias']]</set>
	</item>
	<item id="remark" alias="备注" length="100" colspan="3"/>
	<item id="inputUser" alias="录入医生" type="string" length="20" update="false"
		queryable="true" defaultValue="%user.userId" fixed="true" display="0">
		<dic id="chis.dictionary.user06" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false"
		width="320" defaultValue="%user.manageUnit.id" fixed="true" colspan="2" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="date" queryable="true" update="false"
		defaultValue="%server.date.date" fixed="true"  maxValue="%server.date.date" display="0">
		<set type="exp">['$','%server.date.date']</set>
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
	<item id="lastModifyDate" alias="最后修改日期" type="date" defaultValue="%server.date.date"
		fixed="true" display="1">
		<set type="exp">['$','%server.date.date']</set>
	</item>
</entry>
