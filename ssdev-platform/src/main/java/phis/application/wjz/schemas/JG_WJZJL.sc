<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JG_WJZJL" alias="危机值记录">
	<item id="JLXH" alias="记录序号" length="18" not-null="1"  pkey="true" type="long"/>
	<item id="JZLB" alias="就诊类别" length="2"  type="int"/>
	<item id="JZXH" alias="就诊序号" length="20"  type="string"/>
	<item id="CARDID" alias="病人卡号" length="20"  type="string"/>
	<item id="ZYHM" alias="住院号码" length="20"  type="string"/>
	<item id="BRXM" alias="病人姓名" length="20"  type="string"/>
	<item id="BRXB" alias="病人性别" length="1"  type="string"/>
	<item id="CSRQ" alias="出生日期"   type="datetime"/>
	<item id="BRKS" alias="病人科室" length="10" type="string"/>
	<item id="BRBQ" alias="病人病区" length="10" type="string"/>
	<item id="BRCH" alias="病人床号" length="10" type="string"/>
	<item id="ZRYS" alias="责任医生" length="10" type="string"/>
	<item id="ZRYSXM" alias="责任医生姓名" length="20" type="string"/>
	<item id="JTYS" alias="家庭医生" length="10" type="string"/>
	<item id="JTYSXM" alias="家庭医生姓名" length="10" type="string"/>
	<item id="XMLB" alias="项目类别" length="2"  type="int"/>
	<item id="XMDM" alias="项目代码" length="20" type="string"/>
	<item id="XMMC" alias="项目名称" length="100" type="string"/>
	<item id="XMJG" alias="项目结果" length="100" type="string"/>
	<item id="XMDW" alias="项目单位" length="10" type="string"/>
	<item id="CKFW" alias="参考范围" length="50" type="string"/>
	<item id="WJZFW" alias="危急值范围" length="50" type="string"/>
	<item id="SHKS" alias="审核科室" length="10" type="string"/>
	<item id="SHSJ" alias="审核时间"   type="datetime"/>
	<item id="SHRY" alias="审核人员" length="10" type="string"/>
	<item id="SHRYXM" alias="审核人员姓名" length="20" type="string"/>
	<item id="FBZT" alias="发布状态" length="1"  type="int"/>
	<item id="FBSJ" alias="发布时间"   type="datetime"/>
	<item id="FBRY" alias="发布人员" length="10" type="string"/>
	<item id="FBRYXM" alias="发布人员姓名" length="20" type="string"/>
	<item id="CLZT" alias="处理状态" length="1"  type="int"/>
	<item id="CLSJ" alias="处理时间"   type="datetime"/>
	<item id="CLRY" alias="处理人员" length="10" type="string"/>
	<item id="CLRYXM" alias="处理人员姓名" length="20" type="string"/>
	<item id="CLQK" alias="处理情况" length="250" type="string"/>
	<item id="BGDH" alias="报告单号" length="20" type="string"/>
	<item id="TXNR" alias="提醒内容" length="200" type="string"/>
	<item id="ZDID" alias="诊断ID" length="10" type="string"/>
	<item id="LCZD" alias="临床诊断" length="80" type="string"/>
	<item id="TIMESTAMP" alias="时间戳"   type="datetime"/>
</entry>
