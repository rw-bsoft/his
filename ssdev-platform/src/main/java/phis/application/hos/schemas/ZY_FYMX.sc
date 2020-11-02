<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_FYMX" alias="费用明细表">
  <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
  	<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
  </item>
  <item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
  <item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
  <item id="FYRQ" alias="费用日期" type="timestamp" not-null="1"/>
  <item id="FYXH" alias="费用序号" type="long" length="18" not-null="1"/>
  <item id="FYMC" alias="费用名称" length="80"/>
  <item id="YPCD" alias="药品产地" type="long" length="18" not-null="1"/>
  <item id="FYSL" alias="费用数量" type="double" length="10" precision="2" not-null="1"/>
  <item id="FYDJ" alias="费用单价" type="double" length="10" precision="4" not-null="1"/>
  <item id="ZJJE" alias="总计金额" type="double" length="12" precision="2" not-null="1"/>
  <item id="ZFJE" alias="自负金额" type="double" length="12" precision="2" not-null="1"/>
  <item id="YSGH" alias="医生工号" length="10"/>
  <item id="SRGH" alias="输入工号" length="10"/>
  <item id="QRGH" alias="确认工号" length="10"/>
  <item id="FYBQ" alias="费用病区" type="long" length="18" not-null="1"/>
  <item id="FYKS" alias="费用科室" type="long" length="18" not-null="1"/>
  <item id="ZXKS" alias="执行科室" type="long" length="18"/>
  <item id="JFRQ" alias="记费日期" type="timestamp" not-null="1"/>
  <item id="XMLX" alias="项目类型" type="int" length="2" not-null="1"/>
  <item id="YPLX" alias="药品类型" type="int" length="1" not-null="1"/>
  <item id="FYXM" alias="费用项目" type="long" length="18" not-null="1"/>
  <item id="JSCS" alias="结算次数" type="int" length="3" not-null="1"/>
  <item id="ZFBL" alias="自负比例" type="double" length="4" precision="3" not-null="1"/>
  <item id="YZXH" alias="医嘱序号" type="long" length="18"/>
  <item id="HZRQ" alias="汇总日期" type="timestamp"/>
  <item id="YJRQ" alias="月结日期" length="8"/>
  <item id="ZLJE" alias="自理金额" type="double" length="12" precision="2" not-null="1"/>
  <item id="ZLXZ" alias="诊疗小组" type="int" length="4"/>
  <item id="YEPB" alias="婴儿判别" type="int" length="1"/>
  <item id="DZBL" alias="打折比例" type="double" length="6" precision="3" not-null="1"/>
  <item id="SCBZ" alias="上传标志" type="int" length="1"/>
  <item id="NHSCBZ" alias="上传标志(农合)" type="string" length="1" default="0" />
</entry>
