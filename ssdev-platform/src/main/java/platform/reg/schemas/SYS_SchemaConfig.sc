<?xml version="1.0" encoding="UTF-8"?>
<entry id="SchemaConfig" alias="数据结构管理">
	<item id="id" alias="标识名" not-null="1"/>
	<item id="alias" alias="别名" not-null="1"/>
	<item id="type" alias="类型" not-null="1">
		<dic id="platform.reg.dictionary.schemaItemType"/>
	</item>
	<item id="pkey" alias="是否主键">
		<dic id="platform.reg.dictionary.boolType"/>
	</item>
	<item id="generator" alias="主键生成方式"/>
	<item id="dic" alias="字典">
		<dic id="platform.reg.dictionary.dicConfig" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="dicRender" alias="字典展示方式">
		<dic id="platform.reg.dictionary.dicRender"/>
	</item>
	<item id="not-null" alias="不为空">
		<dic id="platform.reg.dictionary.yesOrNo"/>
	</item>
	<item id="defaultValue" alias="默认值"/>
	<item id="display" alias="显示方式">
		<dic id="platform.reg.dictionary.displayType"/>
	</item>
	<item id="length" alias="长度" type="int"/>
	<item id="width" alias="宽度" type="int"/>
	<item id="colspan" alias="跨列" type="int"/>
	<item id="hidden" alias="是否隐藏">
		<dic id="platform.reg.dictionary.boolType"/>
	</item>
	<item id="queryable" alias="是否查询">
		<dic id="platform.reg.dictionary.boolType"/>
	</item>
</entry>