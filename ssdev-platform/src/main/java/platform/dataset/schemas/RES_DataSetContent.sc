<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="RES_DataSetContent" alias="数据集内容" sort="a.DataGroup desc,a.DSequence">
  <item id="DataSetContentId" alias="数据集内容ID" type="int" not-null="1" generator="auto" pkey="true" display="0"/>
  <item id="DataSetId" alias="数据集ID" type="int" not-null="1" display="0"/>
  <item id="DataElementId" alias="数据元ID" type="int" not-null="1" display="0"/>
  <item ref="b.StandardIdentify" fixed="true"/>
  <item id="CustomIdentify" alias="自定义标识符" type="string" length="50" not-null="1" regexp="^[a-zA-Z]\w*"/>
  <item ref="b.CustomIdentify" id="bCustomIdentify" display="0"/>
  <item id="InteriorIdentify" alias="内部标识符" type="string" length="50" display="0"/>
  <item id="DName" alias="别名" type="string" length="50"/>
  <item ref="b.DName" id="bDName" display="0"/>
  <item id="PyCode" alias="拼音码" type="string" length="50" fixed="true">
  	<set type="exp" run="server">['py',['$','r.DName']]</set>
  </item>
  <item ref="b.DataType" id="bDataType" update="false" display="0"/>
  <item id="DataType" alias="数据类型" type="string" length="2">
  	<dic id="platform.dataset.dic.resdataType"/>
  </item>
  <item ref="b.DataLength" id="bDataLength" update="false" display="0"/>
  <item id="DataLength" alias="数据长度" type="string" length="15"/>
  <item ref="b.Maximum" fixed="true"/>
  <item ref="b.Minimum" fixed="true"/>
  <item ref="b.CodeSystem" id="bCodeSystem" fixed="true"/>
  <item id="CodeSystem" alias="字典启用" type="string" length="50">
  	<dic>
		<item key="0" text="启用" />
		<item key="1" text="关闭" />
	</dic>
  </item>
  <item ref="b.Category" fixed="true" display="0"/>
  <item id="ForceIdentify" alias="强制性标识" type="string" length="1" not-null="1" defaultValue="0">
  	<dic id="platform.dataset.dic.resForceIdentify"/>
  </item>
  <item id="Frequency" alias="重复次数" type="string" length="1"/>
  <item id="DSequence" alias="顺序" type="int"/>
  <item id="DefaultValueWhenNull" alias="空时默认值" type="string" length="200"/>
  <item id="SampleValue" alias="示例值" type="string" length="200"/>
  <item id="ImportantItem" alias="关键数据项" type="int" display="0"/>
  <item id="TargetTable" alias="目标表名" type="string" length="50" display="0"/>
  <item id="TargetColumn" alias="目标字段名" type="string" length="50" display="0"/>
  <item ref="b.DataStandardId"/>
  <item ref="b.StandardRule" fixed="true"/>
  <item id="SummerFlag" alias="摘要标志" type="string" length="1" defaultValue="0">
  	<dic>
		<item key="0" text="非摘要" />
		<item key="1" text="摘要" />
	</dic>
  </item>
  <item id="DataGroup" alias="分组" type="int">
  	<dic id="platform.dataset.dic.resDataGroup"/>
  </item>
  <item id="DComment" alias="定义" type="string" length="500" xtype="textarea" colspan="2"/>
  <item ref="b.DComment" id="bDComment" display="0"/>
  <relations>
		<relation type="parent" entryName="platform.dataset.schemas.RES_DataElement">
			<join parent="DataElementId" child="DataElementId" />
		</relation>
	</relations>
</entry>
