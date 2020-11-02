<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="RES_DataSet" alias="数据集">
	<item id="DataSetId" alias="数据集ID(自增)" type="int" not-null="1" generator="auto" pkey="true" display="0" />
	<item id="StandardIdentify" alias="卫生部标识符" type="string" length="50" not-null="1" queryable="true" width="120" />
	<item id="CustomIdentify" alias="自定义标识符" type="string" length="50" width="120" queryable="true" />
	<item id="DName" alias="名称" type="string" length="50" not-null="1" width="160" queryable="true"/>
	<item id="DataStandardId" alias="数据标准" type="string" length="16" not-null="1" fixed="true" width="120" queryable="true" display="1">
		<dic id="platform.dataset.dic.resDataStandard" />
	</item>
	<item id="Catalog" alias="目录" type="int" not-null="1" colspan="2" width="180" queryable="true">
		<dic id="platform.dataset.dic.resDataSetCatalog" render="Tree" />
	</item>
	<item id="Status" alias="启用" type="int" defaultValue="1">
		<dic id="platform.dataset.dic.resBoolean" />
	</item>
	<item ref="b.Status" id="bStatus" display="0" />
	<item id="DComment" alias="说明" type="string" length="500" colspan="3" width="120" />
	<item id="Parent" alias="依赖数据集" type="string" length="50" colspan="2" width="120" >
		<dic id="platform.dataset.dic.resDataSet" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="Foreignkey" alias="关联外键" type="string" length="50" colspan="1" width="120" />
	<item id="DataSetMapping" alias="数据集映射" type="string" length="4000" colspan="3" display="0" width="250" height="200" xtype="textarea" />
	<relations>
		<relation type="children" entryName="platform.dataset.schemas.RES_DataStandard">
			<join parent="DataStandardId" child="DataStandardId" />
		</relation>
	</relations>
</entry>
