<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_GHRB" alias="挂号日报">
	<item id="CZGH" alias="操作工号" type="string" length="10" not-null="1" generator="assigned" pkey="true"/>
	<item id="JZRQ" alias="结帐日期" type="timestamp" not-null="1" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="QZPJ" alias="起止票据" type="string" length="255"/>
	<item id="ZJJE" alias="总计金额" type="double" length="12" precision="2"/>
	<item id="XJJE" alias="现金金额" type="double" length="10" precision="2"/>
	<item id="ZPJE" alias="支票金额" type="double" length="12" precision="2"/>
	<item id="ZHJE" alias="帐户金额" type="double" length="12" precision="2"/>
	<item id="QTYS" alias="其他应收" type="double" length="12" precision="2"/>
	<item id="SZYB" alias="省医保" length="12" type="double" precision="2" not-null="1"/>
	<item id="SYB" alias="市医保" length="12" type="double" precision="2" not-null="1"/>
	<item id="YHYB" alias="余杭医保" length="12" type="double" precision="2" not-null="1"/>
	<item id="SMK" alias="市民卡" length="12" type="double" precision="2" not-null="1"/>
	<item id="HBWC" alias="货币误差" type="double" length="12" precision="2"/>
	<item id="HZRQ" alias="汇总日期" type="timestamp"/>
	<item id="FPZS" alias="发票张数" type="int" length="8" not-null="1"/>
	<item id="MZLB" alias="门诊类别" type="long" length="18"/>
	<item id="THFP" alias="退号发票" type="string" length="255"/>
	<item id="THSL" alias="退号数量" type="int" length="8" not-null="1"/>
	<item id="THJE" alias="退号金额" type="double" length="12" precision="2"/>
	<item id="WXHJ" alias="微信合计" type="double" length="12" precision="2"/>
	<item id="ZFBHJ" alias="支付宝合计" type="double" length="12" precision="2"/>
	<item id="ZFFSHJ" alias="自费现金合计" type="double" length="12" precision="2"/>
	<item id="YBZFHJ" alias="医保自费合计" type="double" length="12" precision="2"/>
	<item id="YZJM" alias="义诊减免" type="double" length="12" precision="2"/>
	<item id="YHJE" alias="优惠金额" type="double" length="12" precision="2"/>
</entry>
