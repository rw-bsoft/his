<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_MZXX" alias="门诊收费信息">
	<item id="MZXH" alias="门诊序号" length="18" not-null="1" type="long" generator="assigned" pkey="true">
	</item>
	<item id="JGID" alias="机构ID" length="20" type="string" not-null="1"/>
	<item id="FPHM" alias="发票号码" type="string" length="20" not-null="1"/>
	<item id="SFRQ" alias="收费日期" type="date" defaultValue="%server.date.datetime"/>
	<item id="BRID" alias="病人ID号" length="18" type="long"/>
	<item id="BRXM" alias="病人姓名" type="string" length="40"/>
	<item id="BRXB" alias="病人性别" length="4" type="int"/>
	<item id="BRXZ" alias="病人性质" length="18" type="long">
		<dic id="phis.dictionary.patientProperties_MZ" autoLoad="true"/>
	</item>
	<item id="FYZH" alias="医疗证号" type="string" length="10"/>
	<item id="DWXH" alias="单位序号" length="18"  type="long"/>
	<item id="XJJE" alias="现金金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZPJE" alias="支票金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZHJE" alias="帐户金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="HBWC" alias="货币误差" length="12" type="double" precision="2" not-null="1"/>
	<item id="QTYS" alias="其他应收" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZJJE" alias="总计金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZFJE" alias="自负金额" length="12" type="double" precision="2" not-null="1"/>
	<item id="ZHLB" alias="帐户类别" length="2" type="int"/>
	<item id="ZFPB" alias="作废判别" length="1" type="int" not-null="1"/>
	<item id="THPB" alias="退号判别" length="1" type="int" not-null="1"/>
	<item id="FPGL" alias="发票关联" type="string" length="12"/>
	<item id="MZGL" alias="门诊关联" length="18" type="long"/>
	<item id="MZLB" alias="门诊类别" length="18" type="long" not-null="1"/>
	<item id="GHGL" alias="挂号关联" length="18" type="long"/>
	<item id="CZGH" alias="操作工号" type="string" length="10">
		<dic id="phis.dictionary.doctor"></dic>
	</item>
	<item id="JZRQ" alias="结帐日期" type="date" defaultValue="%server.date.datetime"/>
	<item id="HZRQ" alias="汇总日期" type="date" defaultValue="%server.date.datetime"/>
	<item id="SFFS" alias="收费方式" length="1" type="int" not-null="1"/>
	<item id="HBBZ" alias="合并标志" length="1" type="int" not-null="1"/>
	<item id="XNFP" alias="虚拟发票" type="string" length="12"/>
	<item id="JKJE" alias="交款金额" length="12" type="double" precision="2"/>
	<item id="TZJE" alias="退找金额" length="12" type="double" precision="2"/>
	<item id="HZJE" alias="核准金额" length="12" type="double" precision="2" />
	<item id="JJZFJE" alias="基金支付" length="18" type="double" precision="2" />
	<item id="MZJZJE" alias="支付" length="18" type="double" precision="2" />
	<item id="FFFS" alias="付款方式" length="2" type="int"/>
	<item id="YDCZSF" alias="移动出诊收费" type="string" length="18" display="0"/>
	<item id="YDCZFPBD" alias="移动出诊发票补打" type="string" length="18" display="0"/>
	<item id="FPZS" alias="发票张数" type="int" length="2" display="0"/>
	<item id="YLJZJE" alias="医疗救助金额" length="12" type="double" precision="2"/>
	<item id="BXID" alias="农合报销ID" length="12" type="string"/>
	<item id="JYLYJMJE" alias="家医履约减免金额" length="12" type="double" display="0"/>
</entry>
