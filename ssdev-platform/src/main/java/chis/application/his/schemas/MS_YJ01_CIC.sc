<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_YJ01_CIC" tableName="MS_YJ01" alias="门诊医技单01表">
	<item id="YJXH" alias="记录编号" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"  />
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="25" not-null="1" display="0" type="string"/>
	<item id="TJHM" alias="特检号码" type="string" display="0" length="10" />
	<item id="FPHM" alias="发票号码" type="string" length="12" display="0"/>
	<item id="MZXH" alias="门诊序号" type="long" length="18" display="0"/>
	<item ref="b.MZHM" alias="门诊号码"  />
	<item ref="b.BRXZ" alias="病人性质"  display="2"/>
	<item id="BRID" alias="病人ID号" type="long" display="0" length="18"/>
	<item id="BRXM" alias="病人姓名" type="string" length="40" />
	<item id="HJGH" alias="划价医生" type="string" display="1"  length="10" />
	<item id="KDRQ" alias="申检时间" type="timestamp" display="1"  />
	
	<item  id="YSDM" alias="申检医生" length="10" type="string">
		<dic id="chis.dictionary.doctor" searchField="PYCODE" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.id']]"/>
	</item>
	<item id="KSDM" alias="申检科室" type="long" length="18" >
		<dic id="chis.dictionary.department_mzyj" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.id']]" searchField="PYCODE"/>
	</item>
	
	<item id="ZXKS" alias="执行科室" type="long" width="150" display="0"  length="18" not-null="1" >
		<dic id="chis.dictionary.department_mzyj" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.id']]" searchField="PYCODE" />
	</item>
	<item id="ZXYS" alias="检查医生" type="string" length="10"  >
		<dic id="chis.dictionary.doctor"/>
	</item>
	<item id="ZXRQ" alias="检查日期" type="timestamp" display="2"/>
	
	<item id="ZXPB" alias="执行判别" type="int" length="1" not-null="1" display="0"/>
	
	<item id="ZYSX" alias="注意事项" type="string" length="250" display="0"/>
	<item id="YJGL" alias="医技关联" type="long" length="18" display="0"/>
	<item id="ZFPB" alias="作废判别" type="int" length="1" not-null="1" display="0"/>
	<item id="CFBZ" alias="处方标志" type="int" length="1" display="0"/>
	<item id="HYMX" alias="化验明细" type="string" length="250" display="0"/>
	<item id="YJPH" alias="医技片号" type="string" length="20" display="0"/>
	<item id="SQWH" alias="申请文号" type="long" length="18" display="0"/>
	<item id="JZXH" alias="门诊就诊" type="long" length="18" display="0"/>
	<item id="FJGL" alias="附加处方" type="long" length="18" display="0"/>
	<item id="FJLB" alias="附加类别" type="long" length="18" display="0"/>
	<item id="BWID" alias="部位编号" type="long" length="9" display="0"/>
	<item id="JBID" alias="疾病编号" type="long" length="18" display="0"/>
	<item id="DJZT" alias="单据状态" type="int" length="1" not-null="1" display="0"/>
	<item id="JZKH" alias="就诊卡号" type="string" length="20" display="0"/>
	<item id="SQID" alias="申请ID"  type="long" length="18" display="0"/>
	<item id="XML" alias="申请单XML" type="string" length="1024" display="0"/>
	<item id="SQDMC" alias="申请单名称" type="string" length="40" display="0"/>
	<item id="FYLY" alias="费用来源" type="string" length="4" display="0"/>
	<item id="TJFL" alias="特检分类" type="int" length="2" display="0"/>
	<item id="ZFSJ" alias="作废时间" type="timestamp" display="0"/>
	<item id="DJLY" alias="单据来源" type="int" length="8" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_BRDA_CIC">
			<join parent="BRID" child="BRID"></join>
		</relation>
	</relations>
</entry>
