<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MED_MS_PATIENT"  alias="医技项目取消门诊病人集合" >
	<item id="YJXH" alias="医技序号"  length="18" type="long" not-null="1" display="0"/>
	<item id="TJHM" alias="特检号码"  length="10" type="string" display="0" />
	<item id="JZKH" alias="卡号"  length="20" type="string" not-null="1" />
	<item id="MZHM" alias="门诊号码"  length="32" type="string" not-null="1" />
	<item id="BRXM" alias="病人姓名"  length="40" type="string" />
	<item id="ZXRQ" alias="执行日期"  type="date" display="0"/>
	<item id="KDRQ" alias="开单日期"  type="date" />
	<item id="HJGH" alias="划价医生"  length="10" type="string">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="FYMC" alias="费用名称"  length="40" type="string" not-null="1" display="0"/>
	<item id="YLXH" alias="医疗序号"  length="18" type="long" not-null="1" display="0"/>
	<item id="YSDM" alias="医生代码"  length="10" type="string" display="0">
	</item>
	<item id="BRID" alias="病人ID号"  length="18" type="long" display="0"/>
	<item id="FPHM" alias="发票号码"  length="20" type="string" display="0"/>
	<item id="KSDM" alias="科室代码"  length="18" type="long" display="0" />
	<item id="ZXYS" alias="执行医生"  length="10" type="string" >
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="ZXKS" alias="执行科室"  length="18" type="long" not-null="1">
		<dic id="phis.dictionary.department_mzyj" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="ZFPB" alias="作废判别"  length="1" type="long" not-null="1" display="0"/>
	<item id="ZXPB" alias="执行判别"  length="1" type="long" not-null="1" display="0"/>
</entry>