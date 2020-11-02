<?xml version="1.0" encoding="UTF-8"?>
<entry id="MED_JC_PATIENT" tableName="JC_BRRY" alias="医技项目取消家床病人集合" >
	<item id="YJXH" alias="医技序号"  length="18" type="long" not-null="1" display="0"/>
	<item id="TJHM" alias="特检号码"  length="10" type="string" display="0" />
	<item id="ZYHM" alias="家床号码"  length="10" type="string" />
	<item id="BRXM" alias="病人姓名"  length="40" type="string" />
	<item id="HJGH" alias="划价医生"  length="10" type="string">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="KDRQ" alias="开单日期"  type="date" />
	<item id="KSDM" alias="科室代码"  length="18" type="long" display="0"/>
	<item id="YSDM" alias="医生代码"  length="10" type="string" display="0"/>
	<item id="ZXRQ" alias="执行日期"  type="date" display="0"/>
	<item id="ZXKS" alias="执行科室"  length="18" type="long" >
		<dic id="phis.dictionary.department_mzyj" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="ZXYS" alias="执行医生"  length="10" type="string" display="0">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="ZXPB" alias="执行判别"  length="1" type="long" not-null="1" display="0"/>
	<item id="ZYSX" alias="注意事项"  length="250" type="string" display="0"/>
	<item id="ZFPB" alias="作废判别"  length="1" type="long" not-null="1" display="0"/>
	<item id="ZYH" alias="住院号"  length="18" type="long" display="0"/>
	<item id="YZXH" alias="医嘱序号"  length="18" type="long" display="0"/>
	<item id="YLXH" alias="医疗序号"  length="18" type="long" not-null="1" display="0"/>
	<item id="YJZX" alias="医技主项"  length="1" type="long" display="0"/>
	<item id="BRID" alias="病人ID号" display="0"/>
	
</entry>