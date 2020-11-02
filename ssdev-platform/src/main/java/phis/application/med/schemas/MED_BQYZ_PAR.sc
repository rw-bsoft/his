<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="按项目提交病人集合">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
	<item ref="b.BRCH" alias="病人床号" fixed="true" />
	<item ref="b.BRXZ" alias="病人性质" fixed="true" display="0"/>
	<item ref="b.GZDW" alias="工作单位" fixed="true" display="0"/>
	<item ref="b.ZYHM" alias="住院号码" fixed="true" display="0"/>
	<item ref="b.BRXM" alias="病人姓名" fixed="true" display="0" />
	
	<item id="YZMC" alias="医嘱名称" type="string" length="180"  fixed="true"/>
	<item id="YSGH" alias="开嘱医生" type="string" length="10" fixed="true" display="0"/>
	<item id="YPDJ" alias="药品单价" type="long" length="10" not-null="1" fixed="true" display="0"/>
	<item id="MRCS" alias="每日次数" type="long" length="2" not-null="1" fixed="true"/>
	<item id="YCSL" alias="一次数量" type="long" length="8" not-null="1" fixed="true"/>
	<item id="JE" virtual="true" defaultValue="" type="string" alias="金额x次数" length="10" fixed="true"/>
	<item id="ZXKS" alias="执行科室" type="long" length="18" >
		<dic id="phis.dictionary.department_yj" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="OK" virtual="true" defaultValue="0" type="string" display="0" alias="OK" length="10" fixed="true"/>
	<item id="SYBZ" alias="使用标志" type="long" length="1" not-null="1" fixed="true" display="0"/>
	<item id="SRKS" alias="输入科室" type="long" length="18" fixed="true" display="0"/>
	<item id="QRSJ" alias="确认时间" type="date" fixed="true" display="0"/>
	<item id="YPXH" alias="药品序号" type="long" length="18" not-null="1" fixed="true" display="0"/>
	<item id="YPLX" alias="药品类型" type="long" length="1" not-null="1" fixed="true" display="0"/>
	<item id="ZYH" alias="住院号" type="long" length="18" not-null="1" fixed="true" display="0"/>
	<item id="KSSJ" alias="开始时间" type="date" fixed="true" display="0"/>
	<item id="TZSJ" alias="停止时间" type="date" fixed="true" display="0"/>

	<item id="MZCS" alias="每周次数" type="long" length="1" not-null="1" fixed="true" display="0"/>
	<item id="XMLX" alias="项目类型" type="long" length="2" not-null="1" fixed="true" display="0"/>
	<item id="YZZH" alias="医嘱组号" type="long" length="18" not-null="1" fixed="true" display="0"/>
	<item id="CZGH" alias="操作工号" type="string" length="10" fixed="true" display="0"/>
	<item id="JFBZ" alias="计费标志" type="long" length="1" not-null="1" fixed="true" display="0"/>
	<item id="YJZX" alias="医技主项" type="long" length="1" not-null="1" fixed="true" display="0"/>
	<item id="HYXM" alias="化验项目" type="string" length="250" fixed="true" display="0"/>
	<item id="SQWH" alias="申请文号" type="long" length="18" fixed="true" display="0"/>
	
	
	<item id="BRKS" alias="病人科室" type="long" length="18" not-null="1" fixed="true" display="0"/>
	<item id="YBBH" virtual="true" defaultValue="" type="string" alias="YBBH" length="10" fixed="true" display="0"/>
	
	<item id="SYPC" alias="使用频次" type="string" length="6"  fixed="true" display="0"/> 
	<item ref="b.DJID" alias="冻结ID号" fixed="true" display="0"/>
	<item id="YSTJ" alias="医生提交标志" type="long" length="1" not-null="1" fixed="true" display="0"/>
	<item id="SQID" alias="申请ID" type="long" length="18" fixed="true" display="0"/>
	<item id="SQDMC" alias="申请单名称" type="string" length="40"  fixed="true" display="0"/>
	<item id="YEPB" alias="婴儿判别" type="long" length="1" fixed="true" display="0"/>
	<item id="YZZXSJ" alias="医嘱执行时间" type="string" length="150" fixed="true" display="0"/>
	<item id="LSYZ" alias="临时医嘱" type="long" length="1" fixed="true" display="0"/>
	<item id="SRCS" alias="首日次数" type="long" length="6" fixed="true" display="0"/>
	<item id="C" virtual="true" defaultValue="0" type="int" display="0" alias="C" length="10" fixed="true"/>
	<item ref="c.JCDL" alias="检查大类" display="0" />
	<relations>	    
		<relation type="parent" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
			<join parent="JGID" child="JGID"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.GY_YLSF_CIC">
			<join parent="FYXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>