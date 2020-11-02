<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF01" alias="抽样02_list">
	<item id="CFSB" alias="处方识别" display="0" type="long" length="18" not-null="1" isGenerate="false" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="10"
				startPos="1000" />
		</key>
	</item>
	<item id="CFLX" alias="处方类型" type="int" length="1" not-null="1" defaultValue="1" display="0"/>
	<item ref="b.DPXH"  display="0"/>
	<item id="CFHM" alias="处方号码" type="string" length="10" not-null="1" width="60"/>
	<item id="KFRQ" alias="处方日期" type="date" not-null="1" />
	<item ref="c.CSNY" display="0"/>
	<item id="NL" alias="年龄" type="String" virtual="true" width="40"/>
	<item ref="b.ZDMC" width="120"/>
	<item ref="b.YPPZ" alias="药品种数" />
	<item ref="b.KJYW" alias="抗菌药(0/1)" width="90"/>
	<item ref="b.ZSYW" alias="注射剂(0/1)" width="90"/>
	<item ref="b.JBYW" alias="基本药物数" width="90"/>
	<item ref="b.TYMS" alias="通用名数"/>
	<item ref="b.CFJE" />
	<item ref="b.YSGH" alias="处方医生"/>
	<item ref="b.PYGH"/>
	<item ref="b.FYGH" />
	<item ref="b.SFHL" />
	<item ref="b.WTDM" alias="存在问题(代码)" width="120"/>
	<item ref="b.DPGH" alias="点评人"/>
	<item ref="b.DPRQ" width="90"/>
	<relations>
		<relation type="child" entryName="phis.application.pcm.schemas.PCIS_CFDP02" >
		           <join parent="CFSB" child="CFSB" />
		</relation>
		<relation type="child" entryName="phis.application.cic.schemas.MS_BRDA" >
		           <join parent="BRID" child="BRID" />
		</relation>
	</relations>
</entry>
