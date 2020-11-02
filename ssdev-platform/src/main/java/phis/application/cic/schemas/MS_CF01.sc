<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF01" alias="门诊处方01表">
	<item id="CFSB" alias="处方识别" display="1" type="long" length="18" not-null="1" isGenerate="false" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="10"
				startPos="1000" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" />
	<item id="CFHM" alias="处方号码" fixed="true" generator="assigned" type="string" length="10">
	</item>
	<item id="FPHM" alias="发票号码" type="string" length="20" display="0"/>
	<item id="KFRQ" alias="开方日期" xtype="datetimefield" type="date" not-null="1" />
	<item id="CFLX" alias="处方类型" type="int" length="1" not-null="1" defaultValue="1" >
		<dic id="phis.dictionary.prescriptionType" editable="false"/>
	</item>
	<item id="KSDM" alias="就诊科室" type="long" fixed="true" length="18">
		<dic id="phis.dictionary.department_leaf" autoLoad="true" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="YSDM" alias="开方医生" length="10" type="string" defaultValue="%user.userId" fixed="true">
		<dic id="phis.dictionary.doctor_cfqx" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]"/>
	</item>
	<item id="CFTS" alias="草药帖数" defaultValue="1" type="int" minValue="1" maxValue="99" not-null="1"/>
	<item id="YFSB" alias="发药药房" display="0"  type="long" length="18" />
	<item id="JZXH" alias="就诊序号" display="0"  type="long" length="18" />
	<item id="BRID" alias="病人ID" display="0"  type="long" length="18" />
	<item id="DJLY" alias="单据来源" display="0" type="int" length="8" />
	<item id="YFSB" alias="药房识别" display="0" type="long" length="18" />
	<item id="DJYBZ" alias="代煎药标志" display="0" type="int" length="1" />
	<item id="TYSM" alias="退药说明" display="0" type="int" length="2" >
		<dic id="phis.dictionary.tysm" />
	</item>
	<item id="MZXH" alias="门诊序号" display="0" type="long" />
	
	<item id="BRXM" alias="病人姓名" type="string" display="0" length="40" />
	<item id="FYRQ" alias="发药日期" type="timestamp"  display="0" />
	<item id="FYCK" alias="发药窗口" type="int" display="0" length="2" />
	<item id="HJGH" alias="划价工号" type="string" display="0" length="10" />
	<item id="PYGH" alias="配药工号" type="string" display="0" length="10" />
	<item id="FYGH" alias="发药工号" type="string" display="0" length="10" />
	<item id="PYBZ" alias="配药标志" type="int" display="0" length="1" />
	<item id="FYBZ" alias="发药标志" type="int" display="0" length="1" />
	<item id="CFGL" alias="处方关联" type="long" display="0" length="18" />
	<item id="ZFPB" alias="作废判别" type="int" display="0" length="1" />
	<item id="DYBZ" alias="打印标志" type="int" display="0" length="1" />
	<item id="TSCF" alias="特殊处方" type="int" display="0" length="2" />
	<item id="TSLX" alias="特殊类型" type="int" display="0"  />
	<item id="TYBZ" alias="退药标志" type="int" display="0" length="1" />
	<item id="CFBZ" alias="处方标志" type="int" display="0" length="1" />
	<item id="YXPB" alias="优先处方" type="int" display="0" length="1" />
	<item id="JZKH" alias="就诊卡号" type="string" display="0" length="20" />
	<item id="ZFSJ" alias="作废时间" type="string" display="0"  />
	<item id="HDGH" alias="核对工号" type="string" display="0" length="10" />
	<item id="HDRQ" alias="核对日期" type="string" display="0"  />
	<item id="SJJG" alias="上级发药机构" type="string" display="0" length="20" />
	<item id="SJYF" alias="上级发药药房" type="long" display="0" length="18" />
	<item id="SJFYBZ" alias="上级发药标志" type="int" display="0" length="1"/>
</entry>
