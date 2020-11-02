<?xml version="1.0" encoding="UTF-8"?>
<entry   entityName="chis.application.quality.schemas.QUALITY_ZK_CKZKBG_DZ"   >
	<item id="ID"  type="string" length="16"     not-null="1"   pkey="true" >
		  <key>
		      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		    </key>
	</item>
	 <item id="XMMC" alias="项目名称" type="string" length="40"  not-null="1"  >
		  <dic id="chis.dictionary.QualityControl_XMMC"/>
	 </item> 
	<item id="ZKZ" alias="质控值" type="string" length="500"/>
	<item id="SFZ" alias="随访值" type="string" length="500"/>
	<item id="DF" alias="分值" type="string" length="10"/> 
</entry>
