<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MS_CF01"   alias="处方待发药列表" sort="a.FPHM">
	<item id="CFSB" alias="处方识别" display="0"  type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="FPHM" alias="发票号码" type="string" length="20" queryable="true" selected="true" width="120" summaryType="count" summaryRenderer="totalCFS"/>
	<item id="YFSB" alias="药房识别" type="long" length="18" display="0"/>
	<item id="CFHM" alias="处方号码" type="string" length="10" display="2" queryable="true"/>
	<item id="CFLX" alias="处方类型" type="int" display="0"/>
	<item id="SJJG" alias="上级发药机构"   length="20"  display="0" />
	<item id="SJYF" alias="上级发药药房"   length="18" type="long" display="0" >
		<dic id="phis.dictionary.pharmacy_db"/> 
	</item>
	<item id="SJFYBZ" alias="上级发药标志"   length="1" type="int" display="0" />
	<item ref="c.FYBZ" display="0"></item>
	<item ref="b.BRXM"  queryable="true"></item>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_BRDA" >	
			<join parent="BRID" child="BRID" />
		</relation>	
	</relations>
</entry>
