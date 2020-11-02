<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.quality.schemas.QUALITY_ZK_Form"  
	   tableName="chis.application.quality.schemas.QUALITY_ZK" >
	<item id="ID" alias="id" type="string" length="16"     not-null="1"   pkey="true"  display="1">
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
	<item id="ZFS" alias="满分" type="string" length="10"/>
	<item id="XMMC" alias="项目名称" type="string" length="40"  not-null="1"  >
		  <dic id="chis.dictionary.QualityControl_XMMC"/>
	    </item>
     <item id="XMBS" alias="项目标识" type="string" length="40"  not-null="1"  />
	<item id="BZMS" alias="标准描述" type="string" length="500" width="200" colspan="2"/>
	<item id="KXX" alias="可选项" type="string" length="500" width="250" colspan="2"/>
</entry>
