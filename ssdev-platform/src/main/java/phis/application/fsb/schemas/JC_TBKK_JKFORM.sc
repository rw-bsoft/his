<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_TBKK"  alias="退补FORM" >
	<item id="SJHM" alias="收据号码" length="20" readOnly="true" not-null="1" queryable="true"/>
	<item id="ZYHM" alias="家床号" length="10" emptyText="请输入住院号码回车查询" not-null="1"/>
	<item id="BRXM" alias="病人姓名" length="40" fixed="true"/>
	<item id="JKJE" alias="缴款金额" type="double" minValue="0.01" maxValue="99999999.99" length="11" precision="2" update="true"/>
	<item id="JKFS" alias="缴款方式" type="int" length="6" not-null="1" update="true">
		<dic id="phis.dictionary.payment"  filter="['and',['eq',['$','item.properties.SYLX'],['s',2]],['eq',['$','item.properties.ZFBZ'],['s',0]],['eq',['$','item.properties.HBWC'],['s',0]]]" autoLoad="false"/>
	</item> 
	<item id="ZPHM" alias="票(卡)号码" fixed="true" length="20"/>
	<item id="JKHJ" alias="缴款合计" readOnly="true" length="10"/>
	<item id="ZFHJ" alias="自费合计" readOnly="true" length="10"/>
	<item id="SYHJ" alias="剩余合计" readOnly="true" length="10"/>
	<item id="CZGH" alias="收款员" length="10" fixed="true" defaultValue="%user.userId">
		<dic id="phis.dictionary.user" autoLoad="true"/>
	</item>
	<item id="JKRQ" alias="缴款日期" type="datetime"  not-null="1" update="true"/>
</entry>
