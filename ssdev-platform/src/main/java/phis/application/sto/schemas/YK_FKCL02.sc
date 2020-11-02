<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_FKCL02" alias="付款处理明细表">
	<item id="DWMC" alias="供货单位" type="string" length="20" width="250"/>
	<item id="DWXH" alias="单位序号" type="long" length="18" display="0"/>
	<item id="YSDH" alias="验收单号" type="int" length="6" display="0"/>
	<item id="PWD" alias="票未到" type="int" length="1" display="0"/>
	<item id="PFZE" alias="批发总额" type="double" length="11" precision="4" display="0"/>
	<item id="RKFS" alias="入库方式" type="long" length="4">
		<dic id="phis.dictionary.storeroomStorage"/>
	</item>
	<item id="RKRQ" alias="记账日期" type="datetime"/>
	<item id="FPHM" alias="发票号码" type="string" length="10"/>
	<item id="JHZE" alias="进价总额" type="double" length="11" precision="4"/>
	<item id="JXCE" alias="进销差额" type="double" length="11" precision="4"/>
	<item id="LSZE" alias="零售总额" type="double" length="11" precision="4"/>
	<item id="FSMC" alias="方式名称" type="string" length="10" display="0"/>
	<item id="YSRQ" alias="验收日期" type="datetime" display="0"/>
	<item id="FKRQ" alias="付款日期" type="datetime" display="0"/>
	<item id="RKDH" alias="入库单号" type="int" length="6" display="0"/>
	<item id="RKBZ" alias="备注" type="string" length="30"/>
	<item id="PYDM" alias="拼音代码" type="string" length="6" display="0"/>
</entry>
