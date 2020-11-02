<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="RES_DataSetCatalog" alias="数据集目录">
	<item id="CatalogId" alias="目录ID(自增)" type="int" not-null="1" generator="auto" pkey="true" display="0" />
	<item id="CatalogKey" alias="目录标识" type="string" length="20" not-null="1" />
	<item id="CatalogName" alias="目录名称" type="string" length="50" not-null="1" colspan="2" />
	<item id="CustomIdentify" alias="自定义标识符" type="string" length="50" width="100" fixed="true" />
	<item id="DataStandardId" alias="数据标准" type="string" length="16" not-null="1" fixed="true" display="0">
		<dic id="platform.dataset.dic.resDataStandard" />
	</item>
	<item id="ParentCatalogId" alias="隶属目录" type="int" colspan="2">
		<dic id="platform.dataset.dic.resDataSetCatalog" render="Tree" />
	</item>
	<item id="Status" alias="启用" type="int" defaultValue="1" display="0">
		<dic id="platform.dataset.dic.resBoolean" />
	</item>
</entry>
