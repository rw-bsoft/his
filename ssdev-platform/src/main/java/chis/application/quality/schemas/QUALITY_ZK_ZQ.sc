<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.quality.schemas.QUALITY_ZK_ZQ"  >
	<item id="NO"  type="string" length="16"     not-null="1"   pkey="true" hidden="true" display="0">
		  <key>
		      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		    </key>
	</item>
	<item id="TP" alias="" type="string" renderer="onTpRenderer" width="23" length="20" virtual="true" display="1"/>
	<item id="SFWC" alias="是否完成" type="string" defaultValue="0"  
		length="1">
		<dic>
			<item key="1" text="否" />
			<item key="2" text="是" />
		</dic>
	</item>
	<item id="XMLB" alias="项目类别" type="string" length="20" not-null="1"  >
			<dic id="chis.dictionary.QualityControl_XMLB"/>
	</item>
	<item id="ZQLB" alias="周期" type="string" length="40" not-null="1">
	 </item>
	<item id="ZKJB" alias="质控级别" type="string" length="40"  not-null="1"  >
		<dic id="chis.dictionary.QualityControl_ZKJB"/>
    </item>
    <item id="ZKLB" alias="质控类别" type="string" length="40"  not-null="1"  >
		<dic id="chis.dictionary.QualityControl_ZKLB"/>
    </item>
    <item id="WCRQ" alias="完成日期" type="string" length="40"  display="0"   >
    </item>
	<item id="MANAUNITID" alias="管辖机构" type="string" length="20"
		 defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id"  />
	</item>
</entry>
