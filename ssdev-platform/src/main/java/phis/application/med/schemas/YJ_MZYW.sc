<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YJ_MZYW" alias="门诊医技单01表">
	<item id="YJXH" alias="记录编号" generator="assigned" display="0" pkey="true"/>
	<item id="TJHM" alias="特检号码" display="0" />
	<item id="JZKH" alias="卡号" fixed="true" />
	<item id="MZHM" alias="门诊号码"  fixed="true"/>
	<item id="BRXM" alias="病人姓名" fixed="true"/>
	<item id="HJGH" alias="划价医生"  fixed="true">
		<dic id="phis.dictionary.doctor" autoLoad = "true"/>
	</item>
	<item id="KDRQ" alias="申检时间" width="150"  fixed="true" defaultValue="%server.date.date"/>
	
	<item id="YSDM" autoLoad = "true" alias="申检医生"  fixed="true">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="KSDM" alias="申检科室"  fixed="true">
		<dic id="phis.dictionary.department"  />
	</item>
	<item id="ZXYS" alias="执行医生"  fixed="true">
		<dic id="phis.dictionary.doctor_yjqx"  filter="['and',['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]]" autoLoad="true"/>
	</item>
	<item id="ZXRQ" alias="检查日期" display="0"/>
	<item id="FYMC" alias="费用名称" display="0"/>
	<item id="YLXH" alias="医疗序号" display="0"/>
	<item id="BRID" alias="病人ID号" display="0"/>
	<item id="ZXKS" alias="执行科室" display="0">
		<dic id="phis.dictionary.department" />
	</item>
	<item id="YJXH02" alias="记录编号" display="0"/>
	<item id="FPHM" alias="发票号码" display="0"/>
	<item id="ZFPB" alias="作废判别" display="0"/>
	<item id="ZXPB" alias="执行判别" display="0"/>
	<item id="CSNY" alias="出生年月" display="0"/>
	<item id="BRXB" alias="病人性别" display="0"/>
	<item id="ZJJE" alias="总计金额" display="0"/>
	<item id="ZDMC" alias="诊断名称" display="0"/>
	<item id="AGE" alias="年龄" display="0"/>
	<item id="DJLY" alias="单据来源" display="0"/>
</entry>
