<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="病区医嘱(数据盒)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" display="0"/>
  
	<item id="KSSJ" alias="开嘱时间" type="date" width="150"/>
	<item id="YZMC" alias="医嘱名称" length="100" width="200"/>
	<item id="YCJL" alias="剂量" type="double" length="10" precision="3" not-null="1"/>
	<item id="JLDW" alias="剂量单位" display="1" width="100" fixed="true" virtual="true"/>
	<item id="YCSL" alias="数量" type="double" length="8" precision="4" not-null="1"/>
	<item id="SYPC" alias="频次" length="6">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="YPYF" alias="途径" type="long" length="18" not-null="1" >
		<dic id="phis.dictionary.drugWay" searchField="PYDM" fields="key,text,PYDM,FYXH" autoLoad="true"/>
	</item>
	<item id="YSGH" alias="开嘱医生" length="10">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="TZYS" alias="停嘱医生" length="10">
		<dic id="phis.dictionary.doctor_cfqx" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="TZSJ" alias="停嘱时间" type="date" width="150"/>
	<item id="BZXX" alias="备注" length="250" width="250"/>
   
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1" display="0"/>
	<item id="YPCD" alias="药品产地" type="long" length="18" not-null="1" display="0"/>
	<item id="XMLX" alias="项目类型" type="int" length="2" not-null="1" display="0"/>
	<item id="YPLX" alias="药品类型" type="int" length="1" not-null="1" display="0"/>
	<item id="MRCS" alias="每日次数" type="int" length="2" not-null="1" display="0"/>
	<item id="MZCS" alias="每周次数" type="int" length="1" not-null="1" display="0"/>
	<item id="QRSJ" alias="确认时间" type="date" display="0"/>
	<item id="YPDJ" alias="药品单价" type="double" length="12" precision="4" not-null="1" display="0"/>
	<item id="CZGH" alias="操作工号" length="10" display="0"/>
	<item id="FHGH" alias="复核工号" length="10" display="0"/>
	<item id="SYBZ" alias="使用标志" type="int" length="1" not-null="1" display="0"/>
	<item id="SRKS" alias="输入科室" type="long" length="18" display="0"/>
	<item id="ZFPB" alias="自负判别" type="int" length="1" not-null="1" display="0"/>
	<item id="YJZX" alias="医技主项" type="int" length="1" not-null="1" display="0"/>
	<item id="YJXH" alias="医技序号" type="long" length="18" not-null="1" display="0"/>
	<item id="TJHM" alias="特检号码" length="10" display="0"/>
	<item id="ZXKS" alias="执行科室" type="long" length="18" display="0"/>
	<item id="APRQ" alias="安排日期" type="date" display="0"/>
	<item id="YZZH" alias="医嘱组号" type="long" length="18" not-null="1" display="0"/>
	<item id="FYSX" alias="发药属性" type="int" length="1" not-null="1" display="0"/>
	<item id="YEPB" alias="婴儿判别" type="int" length="1" not-null="1" display="0"/>
	<item id="YFSB" alias="药房识别" type="long" length="18" not-null="1" display="0"/>
	<item id="LSYZ" alias="临时医嘱" type="int" length="1" not-null="1" display="0"/>
	<item id="LSBZ" alias="历史标志" type="int" length="1" not-null="1" display="0"/>
	<item id="YZPB" alias="医嘱判别" type="int" length="1" not-null="1" display="0"/>
	<item id="JFBZ" alias="记费标志" type="int" length="1" not-null="1" display="0"/>
	<item id="HYXM" alias="化验项目" length="250" display="0"/>
	<item id="FYFS" alias="发药方式" type="long" length="18" display="0"/>
	<item id="TPN" alias="TPN" type="int" length="1" not-null="1" display="0"/>
	<item id="YSBZ" alias="医生医嘱标志" type="int" length="1" not-null="1" display="0"/>
	<item id="YSTJ" alias="医生提交标志" type="int" length="1" not-null="1" display="0"/>
	<item id="FYTX" alias="发药提醒" type="date" display="0"/>
	<item id="YZPX" alias="医嘱排序" type="int" length="2" display="0"/>
	<item id="SQWH" alias="申请文号" type="long" length="18" display="0"/>
	<item id="YSYZBH" alias="医生医嘱编号" type="long" length="18" display="0"/>
	<item id="SQID" alias="申请ID" type="long" length="18" display="0"/>
	<item id="ZFBZ" alias="作废标志" type="int" length="1" not-null="1" display="0"/>
	<item id="XML" alias="申请单XML" length="1024" display="0"/>
	<item id="SQDMC" alias="申请单名称" length="40" display="0"/>
	<item id="SSBH" alias="手术编号" type="long" length="18" display="0"/>
	<item id="YEWYH" alias="婴儿唯一号" type="long" length="18" display="0"/>
	<item id="SRCS" alias="首日次数" type="int" length="6" display="0"/>
	<item id="PZPC" alias="配置批次" length="255" display="0"/>
	<item id="SFJG" alias="审方结果" not-null="1" display="0"/>
	<item id="YYTS" alias="用药天数" type="int" length="4" display="0"/>
	<item id="YFGG" alias="药房规格" length="20" display="0"/>
	<item id="YFDW" alias="药房单位" length="4" display="0"/>
	<item id="YFBZ" alias="药房包装" type="int" length="4" display="0"/>
	<item id="BRKS" alias="病人科室" type="long" length="18" not-null="1" display="0"/>
	<item id="BRBQ" alias="病人病区" type="long" length="18" not-null="1" display="0"/>
	<item id="BRCH" alias="病人床号" length="12" display="0"/>
	<item id="YZZXSJ" alias="医嘱执行时间" length="80" display="0"/>
	<item id="FHBZ" alias="复核标志" type="int" length="8" not-null="1" display="0"/>
	<item id="FHSJ" alias="复核时间" type="date" display="0"/>
	<item id="TZFHBZ" alias="停嘱复核标志" type="int" length="8" not-null="1" display="0"/>
	<item id="TZFHR" alias="停嘱复核人" length="10" display="0"/>
	<item id="TZFHSJ" alias="停嘱复核时间" type="date" display="0"/>
	<item id="SFGH" alias="审方工号" length="10" display="0"/>
	<item id="SFYJ" alias="审方意见" length="255" display="0"/>
	<item id="PSPB" alias="皮试判别" type="int" length="1" not-null="1" display="0"/>
	<item id="PSJG" alias="皮试结果" type="int" length="2" display="0"/>
	<item id="PSSJ" alias="皮试时间" type="date" display="0"/>
	<item id="PSGH" alias="皮试工号" length="10" display="0"/>
	<item id="YYPS" alias="原液皮试" type="int" length="1" display="0"/>
	<item id="PSGL" alias="皮试关联" type="long" length="18" display="0"/>
	<item id="PSFH" alias="皮试复核" length="10" display="0"/>
</entry>
