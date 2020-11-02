<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_TJ02" alias="病区提交明细表">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
	<item id="TJXH" alias="提交序号" type="long" length="18" not-null="1"/>
	<item id="YZXH" alias="医嘱序号" type="long" length="18" not-null="1"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1"/>
	<item id="YFGG" alias="药房规格" length="20"/>
	<item id="YFDW" alias="药房单位" length="4"/>
	<item id="YFBZ" alias="药房包装" type="int" length="4" not-null="1"/>
	<item id="KSSJ" alias="开始时间" type="timestamp" not-null="1"/>
	<item id="YCSL" alias="一次数量" type="double" length="8" precision="2" not-null="1"/>
	<item id="YTCS" alias="一天次数" type="int" length="2" not-null="1"/>
	<item id="FYSL" alias="发药数量" type="double" length="8" precision="2" not-null="1"/>
	<item id="JFRQ" alias="记费日期" type="timestamp" not-null="1"/>
	<item id="QRRQ" alias="确认日期" type="timestamp" not-null="1"/>
	<item id="SYPC" alias="使用频次" length="6"/>
	<item id="FYJE" alias="发药金额" type="double" length="12" precision="2" not-null="1"/>
	<item id="YPDJ" alias="药品单价" type="double" length="12" precision="4" not-null="1"/>
	<item id="FYBZ" alias="发药标志" type="int" length="1" not-null="1"/>
	<item id="FYGH" alias="发药工号" length="10"/>
	<item id="FYRQ" alias="发药日期" type="timestamp"/>
	<item id="LSYZ" alias="临时医嘱" type="int" length="1"/>
	<item id="QZCL" alias="取整策略" type="int" not-null="1"/>
	<item id="YEPB" alias="婴儿判别" type="int" length="1"/>
	<item id="FYKS" alias="费用科室" type="long" length="18"/>
	<item id="SJFYBZ" alias="实际发药标志" type="int" length="1" not-null="1"/>
	<item id="SJFYR" alias="实际发药人" length="10"/>
	<item id="SJFYSJ" alias="实际发药时间" type="timestamp"/>
	<item id="YYTS" alias="用药天数" type="int" length="4"/>
</entry>
