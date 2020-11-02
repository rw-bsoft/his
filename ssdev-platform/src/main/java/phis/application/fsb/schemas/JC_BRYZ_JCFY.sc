<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JC_BRYZ_JCFY" tableName="JC_BRYZ" alias="病区医嘱">
  <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
  <item id="JGID" alias="机构ID" type="string" length="20" not-null="1"/>
  <item id="ZYH" alias="住院号" type="long" length="18" not-null="1"/>
  <item id="YZMC" alias="药嘱名称" length="100" type="string"/>
  <item id="YPXH" alias="药品序号" type="long" length="18" not-null="1"/>
  <item id="YPCD" alias="药品产地" type="long" length="18" not-null="1"/>
  <item id="XMLX" alias="项目类型" type="int" length="2" not-null="1"/>
  <item id="YPLX" alias="药品类型" type="int" length="1" not-null="1"/>
  <item id="MRCS" alias="每日次数" type="int" length="2" not-null="1"/>
  <item id="YCJL" alias="一次剂量" type="double" length="10" precision="3" not-null="1"/>
  <item id="YCSL" alias="一次数量" type="double" length="8" precision="4" not-null="1"/>
  <item id="MZCS" alias="每周次数" type="int" length="1" not-null="1"/>
  <item id="KSSJ" alias="开始时间" type="date"/>
  <item id="QRSJ" alias="确认时间" type="date"/>
  <item id="TZSJ" alias="停止时间" type="date"/>
  <item id="YPDJ" alias="药品单价" type="double" length="12" precision="4" not-null="1"/>
  <item id="YPYF" alias="药品用法" type="long" length="18" not-null="1"/>
  <item id="YSGH" alias="开嘱医生" length="10" type="string"/>
  <item id="TZYS" alias="停嘱医生" length="10" type="string"/>
  <item id="CZGH" alias="操作工号" length="10" type="string"/>
  <item id="FHGH" alias="复核工号" length="10" type="string"/>
  <item id="SYBZ" alias="使用标志" type="int" length="1" not-null="1"/>
  <item id="SRKS" alias="输入科室" type="long" length="18"/>
  <item id="ZFPB" alias="自负判别" type="int" length="1" not-null="1"/>
  <item id="YJZX" alias="医技主项" type="int" length="1" not-null="1"/>
  <item id="YJXH" alias="医技序号" type="long" length="18" not-null="1"/>
  <item id="TJHM" alias="特检号码" length="10" type="string"/>
  <item id="ZXKS" alias="执行科室" type="long" length="18"/>
  <item id="APRQ" alias="安排日期" type="date"/>
  <item id="YZZH" alias="医嘱组号" type="long" length="18" not-null="1"/>
  <item id="SYPC" alias="使用频次" length="6" type="string"/>
  <item id="FYSX" alias="发药属性" type="int" length="1" not-null="1"/>
  <item id="YEPB" alias="婴儿判别" type="int" length="1" not-null="1"/>
  <item id="YFSB" alias="药房识别" type="long" length="18" not-null="1"/>
  <item id="LSYZ" alias="临时医嘱" type="int" length="1" not-null="1"/>
  <item id="LSBZ" alias="历史标志" type="int" length="1" not-null="1"/>
  <item id="YZPB" alias="医嘱判别" type="int" length="1" not-null="1"/>
  <item id="JFBZ" alias="记费标志" type="int" length="1" not-null="1"/>
  <item id="BZXX" alias="备注" length="250" type="string"/>
  <item id="HYXM" alias="化验项目" length="250" type="string"/>
  <item id="FYFS" alias="发药方式" type="long" length="18"/>
  <item id="TPN" alias="TPN" type="int" length="1" not-null="1"/>
  <item id="YSBZ" alias="医生医嘱标志" type="int" length="1" not-null="1"/>
  <item id="YSTJ" alias="医生提交标志" type="int" length="1" not-null="1"/>
  <item id="FYTX" alias="发药提醒" type="date"/>
  <item id="YZPX" alias="医嘱排序" type="int" length="2"/>
  <item id="SQWH" alias="申请文号" type="long" length="18"/>
  <item id="YSYZBH" alias="医生医嘱编号" type="long" length="18"/>
  <item id="SQID" alias="申请ID" type="long" length="18"/>
  <item id="ZFBZ" alias="作废标志" type="int" length="1" not-null="1"/>
  <item id="XML" alias="申请单XML" length="1024" type="string"/>
  <item id="SQDMC" alias="申请单名称" length="40" type="string"/>
  <item id="SSBH" alias="手术编号" type="long" length="18"/>
  <item id="YEWYH" alias="婴儿唯一号" type="long" length="18"/>
  <item id="SRCS" alias="首日次数" type="int" length="6"/>
  <item id="PZPC" alias="配置批次" type="string" length="255"/>
  <item id="SFJG" alias="审方结果" type="string" not-null="1"/>
  <item id="YYTS" alias="用药天数" type="int" length="4"/>
  <item id="YFGG" alias="药房规格" length="20" type="string"/>
  <item id="YFDW" alias="药房单位" length="4" type="string"/>
  <item id="YFBZ" alias="药房包装" type="int" length="4"/>
  <item id="BRKS" alias="病人科室" type="long" length="18" not-null="1"/>
  <item id="BRBQ" alias="病人病区" type="long" length="18" not-null="1"/>
  <item id="BRCH" alias="病人床号" length="12" type="string"/>
  <item id="YZZXSJ" alias="医嘱执行时间" length="80" type="string"/>
  <item id="FHBZ" alias="复核标志" type="int" length="8" not-null="1"/>
  <item id="FHSJ" alias="复核时间" type="date"/>
  <item id="TZFHBZ" alias="停嘱复核标志" type="int" length="8" not-null="1"/>
  <item id="TZFHR" alias="停嘱复核人" length="10" type="string"/>
  <item id="TZFHSJ" alias="停嘱复核时间" type="date" />
  <item id="SFGH" alias="审方工号" length="10" type="string"/>
  <item id="SFYJ" alias="审方意见" length="255" type="string"/>
  <item id="PSPB" alias="皮试判别" type="int" length="1" not-null="1"/>
  <item id="PSJG" alias="皮试结果" type="int" length="2"/>
  <item id="PSSJ" alias="皮试时间" type="date"/>
  <item id="PSGH" alias="皮试工号" length="10" type="string"/>
  <item id="YYPS" alias="原液皮试" type="int" length="1"/>
  <item id="PSGL" alias="皮试关联" type="long" length="18"/>
  <item id="PSFH" alias="皮试复核" length="10" type="string"/>
</entry>
