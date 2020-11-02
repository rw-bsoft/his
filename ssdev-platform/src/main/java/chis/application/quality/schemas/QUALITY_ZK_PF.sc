<?xml version="1.0" encoding="UTF-8"?>
<entry tableName="chis.application.quality.schemas.QUALITY_ZK"   alias="项目标识字典" >
	<item id="ID"  type="string" length="16"     not-null="1"   pkey="true" hidden="true" display="0">
		  <key>
		      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		    </key>
	</item>
	<item id="XMLB" alias="项目类别" type="string" length="20" not-null="1"  >
			<dic id="chis.dictionary.QualityControl_XMLB"/>
	    </item>
	<item id="XMZLB" alias="项目子类别" type="string" length="20">
		<dic id="chis.dictionary.QualityControl_XMZLB"/>
	 </item>
	<item id="XMMC" alias="项目名称" type="string" length="40"  not-null="1"  >
		  <dic id="chis.dictionary.QualityControl_XMMC"/>
	    </item>
	 <item id="XMBS" alias="项目标识" type="string" length="40"  not-null="1"  />
	<item id="BZMS" alias="标准描述" type="string" length="500"/>
	<item id="KXX" alias="可选项" type="string" length="500"/>
	<item id="ZFS" alias="总分数" type="string" length="10"/>
</entry>
