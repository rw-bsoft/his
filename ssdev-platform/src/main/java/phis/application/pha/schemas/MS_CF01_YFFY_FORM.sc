<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF01" alias="处方待发药详情">
	<item ref="b.BRXM" fixed="true"></item>
	<item id="YSXM" alias="医生姓名" type="string" fixed="true"/>
	<item id="YSDM" alias="医生代码" display="0"/>
	<item id="BRID" alias="病人ID" type="long" display="0"/>
	<item id="KFRQ" alias="开方日期" xtype="datetimefield" type="datetime"  fixed="true"/>
	<item id="CFHM" alias="处方号码" type="string" length="10" fixed="true"/>
	<item id="FPHM" alias="发票号码" type="string" length="20" fixed="true"/>
	<item id="FYGH" alias="发药人" type="string" length="10"  defaultValue="%user.userId" >
		<dic id="phis.dictionary.user_fy" sliceType="1"></dic>
	</item>
	<item id="CKMC" alias="发药窗口" type="string" length="10" defaultValue="0" display="0">
		<dic id="yffylist" defaultIndex="0"></dic>
	</item>
	<imte id="PYGH" alias="配药人" type="string" length="10" fixed="true"  >
		<dic id="phis.dictionary.user" sliceType="1"></dic>
	</imte>
	<item id="CFSB" alias="处方识别" length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true"/>	
	<item id="FYBZ" alias="发药标志" type="int" length="12"  display="0">
		<set type="int">1</set>		
	</item>
	<item id="CFTS" alias="草药帖数"  type="int" minValue="1"  fixed="true" />
	<relations>
		<relation type="child" entryName="phis.application.cic.schemas.MS_BRDA" >
			<join parent="BRID" child="BRID" />	
		</relation>		
	</relations>
</entry>
