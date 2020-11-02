<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_FYMX" alias="费用明细表" sort="JFRQ desc">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
	<item id="FYRQ" alias="费用日期" type="date" not-null="1"/>
	<item id="FYXH" alias="费用序号" type="long" length="18" not-null="1" display="0"/>
	
	<item id="JFRQ" alias="记账日期" type="date" width="130" not-null="1" />
	<item id="FYMC" alias="费用名称" width="170" length="80"/>
	<item id="FYDJ" alias="单    价" type="double" length="10" precision="4" not-null="1" />
	<item id="FYSL" alias="数    量" type="double" width="70" length="10" precision="2" not-null="1" />
	<item id="ZFBL" alias="自负比例" type="double" width="70" length="4" precision="3" not-null="1"/>
	<item id="ZJJE" alias="总计金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="ZFJE" alias="自负金额" type="double" length="12" precision="2" not-null="1" />
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<item id="YSGH" alias="医生工号" length="10" display="0"/>
	<item id="SRGH" alias="输入工号" length="10" display="0"/>
	<item id="QRGH" alias="确认工号" length="10" display="0"/>
	<item id="ZXKS" alias="执行科室" type="long" length="18" not-null="1" >
			<dic id="phis.dictionary.department_zyyj" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
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
		<relation type="parent" entryName="phis.application.fsb.schemas.JC_BRRY" >
			<join parent="ZYH" child="ZYH"/>
		</relation>
	</relations>
</entry>
