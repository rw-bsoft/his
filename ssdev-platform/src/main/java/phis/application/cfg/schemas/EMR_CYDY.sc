<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_CYDY" alias="抗菌药物使用原因">
  <item id="DYID" alias="ID" type="long" length="8" not-null="1" generator="assigned" pkey="true" display="0">
  	<key>
			<rule name="increaseId" type="increase" startPos="1" />
	</key>
	</item>
  <item id="JGID" alias="机构ID" type="string" defaultValue="%user.manageUnit.id"  length="20" display="0"/>
  <item id="DYMC" alias="名称" length="255" width="200"/>
  <item id="DYLB" alias=" " type="int" length="8" display="0"/>
  <item id="GSLB" alias=" " type="int" length="1" display="0"/>
  <item id="GSDM" alias=" " length="20" display="0"/>
  <item id="PYDM" alias="拼音代码"  length="10" target="DYMC" fixed="true" codeType="py"/>
  <item id="WBDM" alias="五笔代码" length="10"  target="DYMC" fixed="true" codeType="wb"/>
  <item id="QTDM" alias="其他代码" fixed="true" length="10"  />
  <item id="ZXBZ" alias="注销标志"  type="int" defaultValue="0" length="1"  >
 	 <dic id="phis.dictionary.status"/>
  </item>
</entry>
