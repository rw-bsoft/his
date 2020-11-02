<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_YK01" alias="药房盈亏01">
  <item id="JGID" alias="机构ID" length="20" not-null="1" type="string"  defaultValue="%user.manageUnit.id"/>
  <item id="YFSB" alias="药房识别" length="18" not-null="1" generator="assigned" pkey="true" type="long"/>
  <item id="CKBH" alias="窗口编号" length="2" not-null="1" type="int" pkey="true" />
  <item id="PDDH" alias="盘点单号" length="8" not-null="1" type="int" generator="assigned" pkey="true">
  	<key>
			<rule name="increaseId" type="increase" length="8"
				startPos="1" />
		</key>
  </item>
  <item id="PDRQ" alias="盘点日期" type="datetime" not-null="1"/>
  <item id="CZGH" alias="操作工号" type="string" length="10" not-null="1"/>
  <item id="PDWC" alias="盘点完成" length="1" not-null="1" type="int"/>
  <item id="WCRQ" alias="完成日期" type="datetime"/>
  <item id="HZWC" alias="汇总完成" length="1" type="int"/>
  <item id="BZ" alias="备注" type="string" length="250"/>
</entry>
