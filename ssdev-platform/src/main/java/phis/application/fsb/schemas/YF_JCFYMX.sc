<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_JCFYMX" alias="家床病人发药明细" sort="a.YPSL desc">
  <item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
  <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
  	<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
  </item>
  <item id="YFSB" alias="药房识别" type="long" length="18" not-null="1"/>
  <item id="CKBH" alias="窗口编号" type="int" length="2" not-null="1"/>
  <item id="FYLX" alias="发药类型" type="int" length="1"/>
  <item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
  <item id="FYRQ" alias="费用日期" type="timestamp" not-null="1"/>
  <item id="YPXH" alias="费用序号" type="long" length="18" not-null="1"/>
  <item id="YPCD" alias="药品产地" type="long" length="18" not-null="1"/>
  <item id="YPGG" alias="药品规格" length="20"/>
  <item id="YFDW" alias="药房单位" length="4"/>
  <item id="YFBZ" alias="药房包装" type="int" length="4" not-null="1"/>
  <item id="YPSL" alias="费用数量" type="double" length="10" precision="4" not-null="1"/>
  <item id="YPDJ" alias="费用单价" type="double" length="12" precision="6" not-null="1"/>
  <item id="ZFBL" alias="自负比例" type="double" length="4" precision="3"/>
  <item id="QRGH" alias="确认工号" length="10"/>
  <item id="JFRQ" alias="记费日期" type="timestamp" not-null="1"/>
  <item id="YPLX" alias="药品类型" type="int" length="1" not-null="1"/>
  <item id="FYKS" alias="费用科室" type="long" length="18" not-null="1"/>
  <item id="LYBQ" alias="领药病区" type="long" length="18" not-null="1"/>
  <item id="ZXKS" alias="执行科室" type="long" length="18" not-null="1"/>
  <item id="YZXH" alias="医嘱序号" type="long" length="18"/>
  <item id="YEPB" alias="婴儿判别" type="int" length="1"/>
  <item id="ZFPB" alias="自负判别" type="int" length="2"/>
  <item id="FYFS" alias="发药方式" type="long" length="18" not-null="1"/>
  <item id="JLID" alias="记录ID" type="long" length="18" not-null="1"/>
  <item id="LSJG" alias="零售价格" type="double" length="12" precision="6" not-null="1"/>
  <item id="PFJG" alias="批发价格" type="double" length="12" precision="6" not-null="1"/>
  <item id="JHJG" alias="进货价格" type="double" length="12" precision="6" not-null="1"/>
  <item id="FYJE" alias="费用金额" type="double" length="12" precision="4" not-null="1"/>
  <item id="LSJE" alias="零售金额" type="double" length="12" precision="4" not-null="1"/>
  <item id="PFJE" alias="批发金额" type="double" length="12" precision="4" not-null="1"/>
  <item id="JHJE" alias="进货金额" type="double" length="12" precision="4" not-null="1"/>
  <item id="YPPH" alias="药品批号" length="20"/>
  <item id="YPXQ" alias="药品效期" type="timestamp"/>
  <item id="TYGL" alias="退药关联" type="long" length="18" not-null="1"/>
  <item id="JFID" alias="记费ID" type="long" length="18" not-null="1"/>
  <item id="YKJH" alias="药库进货" type="double" length="12" precision="4"/>
  <item id="JBYWBZ" alias="基本药物标志" type="int" length="1" not-null="1"/>
  <item id="KCSB" alias="库存识别" type="long" length="18" not-null="1"/>
  <item id="TJXH" alias="提交序号" type="long" length="18"/>
  <item id="TYXH" alias="退药序号" type="long" length="18"/>
</entry>
