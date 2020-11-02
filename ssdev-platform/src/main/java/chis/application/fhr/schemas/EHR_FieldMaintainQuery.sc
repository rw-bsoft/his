<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.mpm.schemas.MPM_FieldMaintainQuery" alias="数据结构维护查询" tableName="MPM_FieldMaintain">
	<item id="alias" alias="字段名称" length="1000"/>
	<item id="id" alias="字段代码" length="30"/>
	<item id="type" alias="类型" type="string" length="10">
		<dic>
			<item key="string" text="字符串"/>
			<item key="int" text="整型"/>
			<item key="date" text="日期"/>
			<item key="datetime" text="时间"/>
			<item key="bigDecimal" text="小数型"/>
			<item key="double" text="浮点型"/>
		</dic>
	</item>
	<item id="dicRender" alias="是否字典" type="string">
		<dic>
			<item key="0" text="非字典"/>
			<item key="1" text="单选"/>
			<item key="2" text="多选"/>
		</dic>
	</item>	
	<item id="notNull" alias="是否必输" type="string">
		<dic>
			<item key="1" text="是"/>
			<item key="0" text="否"/>
		</dic>
	</item>
	<item id="length" alias="长度" type="int" length="5"/>
</entry>
