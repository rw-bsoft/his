<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PCIS_CFDP02" alias="抽样02">
	<item id="DPXH" alias="识别序号" length="18" type="long"  generator="assigned" pkey="true" not-null="1" >
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="20" type="string"  defaultValue="%user.manageUnit.id"  not-null="1" display="0"/>
	<item id="CYXH" alias="抽样序号" length="18"   type="long" not-null="1" display="0"/>
	<item id="CFHM" alias="处方号码" type="string" length="10" not-null="1"/>
	<item ref="b.KFRQ" alias="处方日期"/>
	
	
	
	<item id="DPLX" alias="点评类型" length="1"   type="int" not-null="1"/>
	<item id="CFSB" alias="处方识别" length="18"   type="long"/>
	<item id="KSDM" alias="科室代码" length="18"   type="long"/>
	<item id="YSGH" alias="医生工号" type="string" length="10" />
	<item id="PYGH" alias="调配药师" type="string" length="10" />
	<item id="FYGH" alias="发药药师" type="string" length="10" />
	<item id="ZDMC" alias="诊断名称" type="string" length="255" />
	<item id="DPBZ" alias="点评标志" length="1"   type="int" not-null="1" defaultValue="0"/>
	<item id="YPPZ" alias="药品品种数" length="2"   type="int"/>
	<item id="KJYW" alias="是否包含抗菌药物" length="1"   type="int"/>
	<item id="ZSYW" alias="是否包含注射药物" length="1"   type="int"/>
	<item id="JBYW" alias="国家基本药物种数" length="2"   type="int"/>
	<item id="TYMS" alias="药品通用名种数" length="2"   type="int"/>
	<item id="CFJE" alias="处方金额" length="12" type="double" precision="2" />	
	<item id="SFHL" alias="是否合理" length="1"   type="int"/>
	<item id="WTDM" alias="问题代码" type="string" length="255" />
	<item id="DPSM" alias="点评说明(预留)" type="string" length="255" />
	<item id="DPGH" alias="点评工号" type="string" length="10" />
	<item id="DPRQ" alias="点评日期" type="datetime" />
	<item id="ZFBZ" alias="作废标志(冗余设计同PCIS_CFDP01)" length="1"   type="int" not-null="1" defaultValue="0"/>
	
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_CF01" >
		           <join parent="CFSB" child="CFSB" />
		</relation>
	</relations>
</entry>
