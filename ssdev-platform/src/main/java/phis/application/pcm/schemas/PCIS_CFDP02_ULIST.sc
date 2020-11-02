<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="PCIS_CFDP02" alias="抽样02_list">
	<item id="DPXH" alias="识别序号" length="18" type="long"  generator="assigned" pkey="true" not-null="1" display="0">
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="DPBZ" alias="点评" length="1" type="int" not-null="1" defaultValue="0" renderer="onRenderer_dp" width="30"/>
	<item ref="b.KFRQ" alias="处方日期"/>
	<item id="CFHM" alias="处方号码" type="string" length="10" not-null="1"/>
	<item ref="b.BRXM" alias="姓名"/>
	<item id="ZDMC" alias="诊断" type="string" length="255" width="120"/>
	<item id="SFHL" alias="点评结果" length="1"   type="int" width="60" renderer="onRenderer_bz">
		<dic>
			<item key="1" text="不合理"/>
			<item key="0" text="合理"/>
		</dic>
	</item>
	<item id="KSDM" alias="处方科室" length="18"   type="long">
		<dic id="phis.dictionary.department" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YSGH" alias="处方医生" type="string" length="10" >
		<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="PYGH" alias="调配药师" type="string" length="10" >
		<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item id="FYGH" alias="发药药师" type="string" length="10" >
	<dic  id="phis.dictionary.user" sliceType="1"/>
	</item>
	<item ref="b.CFLX" display="0"/>
	<item ref="b.CFSB" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_CF01" >
		           <join parent="CFSB" child="CFSB" />
		</relation>
	</relations>
</entry>
