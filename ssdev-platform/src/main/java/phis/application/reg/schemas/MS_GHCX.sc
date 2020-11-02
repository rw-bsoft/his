<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_GHCX" alias="挂号查询" sort="a.GHSJ">
	<item id="SBXH" alias="识别序号" display="0" type="string" width="120"/>
	<item id="JZHM" alias="就诊号码" type="string" width="120"/>
	<item id="JZKH" alias="卡号" type="string" width="120"/>
	<item id="MZHM" alias="门诊号码" type="string" width="120"/>
	<item id="BRXZ" alias="病人性质" length="18"  type="string">
		<dic id="phis.dictionary.patientProperties"/>
	</item>
	<item id="BRXM" alias="病人姓名" type="string"/>
	<item id="KSMC" alias="挂号科室" type="string">
		<dic id="phis.dictionary.ghdepartment"/>
	</item>
	<item id="YSXM" alias="挂号医生" type="string">
		<dic id="phis.dictionary.doctor"/>
	</item>
	
	<item id="CZGH" alias="挂号员" type="string">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="GHSJ" alias="挂号时间" type="timestamp" width="130"/>
	<item id="GHF" alias="挂号费" type="double"/>
	<item id="ZLF" alias="诊疗费" type="double"/>
	<item id="ZJF" alias="专家费" type="double"/>
	<item id="BLF" alias="病历费" type="double"/>
	<item id="HJJE" alias="合计" type="double"/>
	<item id="XJJE" alias="现金" type="double"/>
	<item id="WXJE" alias="微信" type="double"/>
	<item id="ZFBJE" alias="支付宝" type="double"/>
	<!--<item id="ZPJE" alias="支票" type="double"/>
		<item id="ZFJE" alias="账户" type="double"/> -->
	<item id="QTYS" alias="其他应收" type="double"/>
	<item id="YZJM" alias="义诊减免" type="double"/>
	<item id="JYJMBZ" alias="医保家医减免" type="string"/>
	<item id="THBZ" alias="状态" type="string">
		<dic>
			<item key="0" text="正常挂号"/>
			<item key="1" text="退号"/>
			<item key="2" text="废号"/>
		</dic>
	</item>
	<item id="THGH" alias="退号人" type="string">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="THRQ" alias="退号日期" type="timestamp" width="120"/>
</entry>
