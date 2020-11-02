<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYMX" alias="费用明细表">
	<item id="JLXH" alias="记录序号"  type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" />
	<item id="FYXH" alias="费用序号" type="long" length="18" not-null="1" display="0"/>
	<item ref="b.ZYHM" layout="BRXX"/>
	<item ref="b.BRCH" layout="BRXX"/>
	<item ref="b.BRXM" fixed="true" layout="BRXX"/>
	<item ref="b.BRXB" fixed="true"  layout="BRXX"/>
	<item ref="b.BRXZ" fixed="true" layout="BRXX"/>
	<item ref="b.BRKS" layout="BRXX" not-null="1"/>
	<item ref="b.RYRQ" fixed="true" layout="BRXX"/>
	<item ref="b.CYRQ" fixed="true" layout="BRXX"/>
	<item id="FYMC" alias="费用名称" length="80" colspan="2" not-null="1" mode="remote" layout="FYXX"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<item id="FYSL" alias="费用数量" type="string" length="11" precision="2" fixed="true"  defaultValue="1.00" layout="FYXX"/>
	<item id="FYDJ" alias="费用单价" type="string" length="11" precision="4" fixed="true" minValue="0" maxValue="999999.9999" defaultValue="0.0000" layout="FYXX"/>
	<item id="ZFBL" alias="自负比例" type="string" length="4" fixed="true" precision="3" defaultValue="1.000" not-null="1" layout="FYXX"/>
	<item id="ZXKS" alias="执行科室" type="long" length="18" fixed="true" layout="FYXX">
		<dic id="phis.dictionary.department_zyyj" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YSGH" alias="执行医生" length="10" fixed="true" layout="FYXX">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="FYRQ" alias="费用日期"  fixed="true" type="date"  defaultValue="%server.date.date" layout="FYXX"/>
	<item id="ZJJE" alias="总计金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="ZFJE" alias="自负金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="SRGH" alias="输入工号" length="10" display="0"/>
	<item id="QRGH" alias="确认工号" length="10" display="0"/>
	<item id="FYBQ" alias="费用病区" type="long" length="18" not-null="1" display="0"/>
	<item id="FYKS" alias="费用科室" type="long" length="18" not-null="1" display="0"/>
	<item id="JFRQ" alias="记费日期" type="date" not-null="1" display="0"/>
	<item id="XMLX" alias="项目类型" type="int" length="2" not-null="1" display="0"/>
	<item id="YPLX" alias="药品类型" type="int" length="1" not-null="1" display="0"/>
	<item id="FYXM" alias="费用项目" type="long" length="18" not-null="1" display="0"/>
	<item id="JSCS" alias="结算次数" type="int" length="3" not-null="1" display="0"/>
	<item id="YZXH" alias="医嘱序号" type="long" length="18" display="0"/>
	<item id="HZRQ" alias="汇总日期" type="date" display="0"/>
	<item id="YJRQ" alias="月结日期" length="8" display="0"/>
	<item id="ZLJE" alias="自理金额" type="double" length="12" precision="2" not-null="1" display="0"/>
	<item id="ZLXZ" alias="诊疗小组" type="int" length="4" display="0"/>
	<item id="YEPB" alias="婴儿判别" type="int" length="1" display="0"/>
	<item id="DZBL" alias="打折比例" type="double" length="6" precision="3" not-null="1" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.hos.schemas.ZY_BRRY_FY" />
	</relations>
</entry>
