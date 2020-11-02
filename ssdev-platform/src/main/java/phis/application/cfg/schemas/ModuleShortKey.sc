<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ModuleShortKey" alias="模块快捷键">
	<item id="moduleId" alias="模块ID" type="string" length="100" pkey="true"
		width="160" not-null="1" queryable="true" selected="true" fixed="true"
		virtual="true" />
	<item id="moduleName" alias="模块名称" type="string" width="160"
		virtual="true" length="500" />
	<item id="shortcutKey" alias="快捷键" type="string" length="400"
		virtual="true" width="100" not-null="1"/>
</entry>
