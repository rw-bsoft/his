<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_JSGL" alias="病人入院">
	<item id="ZYH" alias="住院号" type="long" length="18" display="2"/>
	<item id="ZYHM" alias="住院号码" length="10"/>
	<item id="BRCH" alias="床号" type="string" length="20"/>
	<item id="BRXM" alias="姓名" type="long" fixed="true" length="18" not-null="1"/>
	<item id="BRXZ" alias="性质" type="long" fixed="true" length="18" not-null="1">
		<dic id="phis.dictionary.patientProperties_ZY" autoLoad="true"/>
	</item>
	<item id="BRKS" alias="科室" type="long" fixed="true" length="18" not-null="1">
		<dic id="phis.dictionary.department_zy" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="RYRQ" alias="入院日期" type="date" xtype="datetimefield" fixed="true" not-null="1"/>
	<item id="FYHJ" alias="总费用" type="double" fixed="true" precision="2" not-null="1"/>
	<item id="ZFHJ" alias="自负" type="double" fixed="true" display="2" precision="2" not-null="1"/>
	<item id="JKHJ" alias="缴款" type="double" fixed="true" precision="2" not-null="1"/>
	<item id="ZHZF" alias="账户支付" type="double" fixed="true" display="0" precision="2" not-null="1"/>
	<item id="YBZF" alias="医保支付" type="double" fixed="true" display="0" precision="2" not-null="1"/>
	<item id="JSJE" alias="余款" type="double" fixed="true" precision="2" not-null="1"/>
	<item id="ZYTS" alias="天数" type="int" precision="2" fixed="true" not-null="1"/>
	<item id="JSRQ" alias="结算日期" type="date" xtype="datetimefield" fixed="true" not-null="1"/>
	<item id="DRGS" alias="单病种" type="string">
		<dic id="phis.dictionary.drgs" searchField="PYDM" autoLoad="true"/>
	</item>
	<item id="SRKH" alias="输入的卡号(杭州医保用)"   length="40"  display="0"/>
	<item id="ZYLSH" alias="住院流水号(杭州医保用)"   length="18"  display="0"/>
	<item id="YYLSH" alias="住院交易流水号(杭州医保用)"  type="string"  length="20" display="0"/>
	<item id="GRBH" alias="个人编号(杭州市医保用)" length="20" display="0"/>
	</entry>
