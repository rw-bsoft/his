<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_JKYS" alias="接口元素定义表">
	<item id="JKLB" alias="接口类别" type="int" length="2" fixed="true">
		<dic id="phis.dictionary.emr_jkys_jklb"/>
	</item>
	<item id="JKYS" alias="接口元素名称" length="100" width="100" not-null="1" generator="assigned" pkey="true" fixed="true"/>
	<item id="GLYS" alias="关联元素" length="200"/>
	<item id="JKKZ" alias="接口扩展" type="string" length="180"/>
	
	<item id="BZXX" alias="备注信息" length="255" display="0"/>
</entry>
