<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF02" alias="门诊处方" sort="c.KFRQ desc">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0"  type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1000" />
		</key>
	</item>
	<item id="CFSB" alias="处方识别" display="0" type="long" length="18" not-null="1"  />
	<item id="YPXH" alias="药品序号" type="long"  display="0" length="18" not-null="1"/>
	<item ref="b.YPSX"/>
	<item ref="c.KFRQ" type="datetime" width="130"/>
	<item alias="药品名称" ref="b.YPMC"/>
	<item id="YYTS" alias="用药天数" type="int" width="80"/>
	<item id="YPYF" alias="使用频率" type="string" length="18" width="60">
		<dic id="phis.dictionary.drugUseRate"/>
	</item>
	<item ref="b.JLDW" display="1" width="70"/>
	<item id="YCJL" alias="一次剂量" type="double" length="10" precision="2"/>
	<item id="YPSL" alias="总量" type="double" length="10" precision="2" not-null="1" width="60"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH" />
		</relation>	
		<relation type="child" entryName="phis.application.cic.schemas.MS_CF01" >
			<join parent="CFSB" child="CFSB" />
		</relation>		
	</relations>
</entry>
