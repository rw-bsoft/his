<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.ag.schemas.EHR_AreaGridChild"
	alias="网格地址" sort="orderNo asc , regionCode asc">
	<item id="regionCode" alias="网格地址编号" queryable="true" type="string"
		length="25" not-null="1" width="180" display="1" generator="assigned"
		pkey="true" />
	<item id="path" alias="上级网格地址" type="string" colspan="2" fixed="true"
		display="2" virtual="true" />
	<item id="orderNo" alias="排列序号" type="int" virtual="true" />
	<item id="parentCode" alias="父节点编码" type="string" length="25"
		display="2" fixed="true" virtual="true" />
	<item id="regionNo" alias="网格地址编号" type="string" display="2"
		not-null="true" length="4" virtual="true" />
	<item id="regionName" alias="网格地址名称" queryable="true" type="string"
		length="50" width="200" not-null="1" virtual="true" />
	<item id="regionAlias" alias="网格地址简称" type="string" length="30"
		virtual="true" width="200" />

	<item id="pyCode" alias="拼音检索码" type="string" length="50"
		queryable="true" virtual="true">
		<set type="exp" run="server">['py',['$','r.regionName']]</set>
	</item>
	<item id="mapSign" alias="地图物理标识" type="string" length="10"
		hidden="true" virtual="true" />
	<item id="isBottom" alias="是否最底层" type="string" length="1" hidden="true"
		defaultValue="y" virtual="true">
		<dic id="chis.dictionary.yesOrNo" />
	</item>
	<item id="isFamily" alias="层次属性" type="string" length="3"
		queryable="true" not-null="1" virtual="true">
		<dic id="chis.dictionary.isFamily" render="Tree" onlySelectLeaf="true"
			keyNotUniquely="true" />
	</item>
	<item id="levelProp" alias="层次属性" type="string" length="100"
		display="0" virtual="true" />
	
	<item id="manaDoctor" alias="责任人" type="string" length="20">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true"
			rootVisible="true" parentKey="%user.manageUnit.id" />
	</item>

	<item id="cdhDoctor" alias="儿保医生" type="string" length="20">
		<dic id="chis.dictionary.user02" render="Tree" onlySelectLeaf="true"
			rootVisible="true" parentKey="%user.manageUnit.id" />
	</item>

	<item id="mhcDoctor" alias="妇保医生" type="string" length="20">
		<dic id="chis.dictionary.user03" render="Tree" onlySelectLeaf="true"
			rootVisible="true" parentKey="%user.manageUnit.id" />
	</item>
	<item id="GPS" alias="GPS(纬,经度)" type="string" length="30" colspan="2" virtual="true"/>
</entry>
