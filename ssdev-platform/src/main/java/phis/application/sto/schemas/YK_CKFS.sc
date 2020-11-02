<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_CKFS" alias="出库方式">
  <item id="JGID" alias="机构ID" length="20" not-null="1"  type="string" display="0" defaultValue="%user.manageUnit.id"/>
  <item id="XTSB" alias="药库识别" length="18" not-null="1" display="0" type="long" pkey="true"/>
  <item id="CKFS" alias="出库方式" length="4" not-null="1" display="1" pkey="true" generator="assigned" type="int">
  	<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
  </item>
  <item id="FSMC" alias="方式名称" type="string" not-null="1" length="20" renderer="onRenderer_reg"/>
  <item id="FSLB" alias="方式类别" length="4" type="int" display="0"/>
  <item id="CKDH" alias="出库单号" length="6" type="int" not-null="1" minValue="1" defaultValue="1" />
  <item id="SBFH" alias="识别符号" type="string" length="4" display="0"/>
  <item id="KSPB" alias="科室领用" length="1" not-null="1" type="int" defaultValue="0">
  	<dic id="phis.dictionary.confirm"/>
  </item>
  <item id="DYFS" alias="对应方式" length="8" type="int">
  	<dic id="phis.dictionary.correspondingWay_ck"/>
  </item>
</entry>
