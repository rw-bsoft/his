<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="CF02" alias="门诊处方" sort="KFRQ desc">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0"  type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1000" />
		</key>
	</item>
	<item id="CFSB" alias="处方识别" display="0" type="long" length="18" not-null="1"  />
	<item id="SFZH" alias="身份证号" display="0" type="string" length="18" not-null="1"  />
	<item id="YPXH" alias="药品序号" type="long"  display="0" length="18" not-null="1"/>
	<item id="YPSX" alias="剂型" type="long"  not-null="1" length="18" >
		<dic id="phis.dictionary.dosageForm" defaultIndex="0"/>
	</item>
	<item id="KFRQ" alias="开方日期" xtype="datetime" type="date"  />
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80" />
	<item id="YYTS" alias="用药天数" type="int" width="80"/>
	<item id="YYBS" alias="YYBS" type="int" width="80" hidden="true" />
	<item id="YPYF" alias="使用频率" type="string" length="18" width="60">
		<dic id="phis.dictionary.drugUseRate"/>
	</item>
	<item id="JLDW" alias="剂量单位" type="string" length="8" />
	<item id="YCJL" alias="一次剂量" type="double" length="10" precision="2"/>
	<item id="YPSL" alias="总量" type="double" length="10" precision="2" not-null="1" width="60"/>
</entry>
