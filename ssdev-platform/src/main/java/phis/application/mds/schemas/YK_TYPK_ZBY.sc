<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_TYPK" alias="药品信息_自备药" sort="a.YPXH desc">
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品内码" type="long" length="18" not-null="1"
		display="0" generator="assigned" pkey="true" layout="JBXX">
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="0" />
		</key>
	</item>
	<item id="TP" alias="" type="string" renderer="onRenderer" width="23"
		length="20" virtual="true" display="1" />
	<item id="YPMC" alias="药品通用名" type="string" width="180" length="80"
		colspan="2" not-null="true" layout="JBXX" />
	<item id="TYMC" alias="通用名" type="string" display="0" width="180"
		anchor="100%" length="80" colspan="2" layout="JBXX" />
	<item id="YPGG" alias="规格" type="string" length="20" layout="JBXX" />
	<item id="YPDW" alias="单位" type="string" length="4" layout="JBXX"
		not-null="1" />
	<item id="TYPE" alias="类别" display="2" not-null="1" defaultValue="1"
		type="int" length="2" layout="JBXX" queryable="true">
		<dic id="phis.dictionary.prescriptionType" />
	</item>
	<item id="ZBLB" alias="账簿类别" display="2" not-null="1" type="long"
		layout="JBXX">
		<dic id="phis.dictionary.booksCategories" defaultIndex="0" />
	</item>
	<item id="YPSX" alias="剂型" type="long" not-null="1" length="18"
		layout="JBXX">
		<dic id="phis.dictionary.dosageForm" defaultIndex="0" autoLoad="true" />
	</item>
	<item id="YPJL" alias="包装剂量" type="double" precision="3"
		defaultValue="1" length="10" display="2" not-null="1" layout="JBXX"
		minValue="0" />
	<item id="JLDW" alias="剂量单位" type="string" length="8" display="2"
		layout="JBXX" />
	<item id="YCJL" alias="一次剂量" type="double" precision="3"
		defaultValue="1" length="10" display="2" not-null="1" layout="JBXX"
		minValue="0" />	
	<item id="YPDC" alias="档次" not-null="1" type="int" length="2"
		display="2" layout="JBXX" defaultValue="1">
		<dic id="phis.dictionary.grade" />
	</item>
	<item id="YPXQ" alias="有效期" type="int" length="6" display="0"
		layout="JBXX" />
	<item id="ABC" alias="ABC" type="string" defaultValue="C"
		not-null="true" length="1" display="0" layout="JBXX">
		<dic>
			<item key="A" text="A" />
			<item key="B" text="B" />
			<item key="C" text="C" />
		</dic>
	</item>
	<item id="YPDM" alias="药品类型" type="string" length="16" layout="JBXX"
		display="2">
		<dic id="phis.dictionary.medicinesCode" render="Tree" />
	</item>
	<item id="PSPB" defaultValue="2" alias="皮试判别" not-null="true"
		type="int" length="1" display="2" layout="JBXX">
		<dic id="phis.dictionary.pspb" defaultIndex="0" />
	</item>
	<item id="PYDM" alias="拼音码" type="string" length="10" not-null="1"
		selected="true" target="YPMC" codeType="py" queryable="true" layout="JBXX">
	</item>
	<item id="WBDM" alias="五笔码" type="string" length="10" target="YPMC"
		codeType="wb" queryable="true" layout="JBXX">
	</item>
	<item id="JXDM" alias="角形码" type="string" length="10" queryable="true"
		layout="JBXX" target="YPMC" codeType="jx">
	</item>
	<item id="BHDM" alias="笔画码" type="string" length="10" queryable="true"
		layout="JBXX" target="YPMC" codeType="bh" />
	<item id="QTDM" alias="其它码" type="string" length="10" display="2"
		layout="JBXX" queryable="true" />
<!--	<item id="JBYWBZ" defaultValue="0" alias="国家基本药物" type="int"
		length="1" display="2" not-null="1" layout="JBXX">
		<dic id="phis.dictionary.confirm" />
	</item>-->
	<item id="JYLX" defaultValue="1" alias="基药类型" type="int" length="1"
		display="2" not-null="1" layout="JBXX">
		<dic id="phis.dictionary.jylx" defaultIndex="0" />
	</item>
	<!-- 药品包装 -->
	<item id="ZXDW" alias="最小单位" type="string" length="4" display="2"
		layout="YPBZ" />
	<item id="YFGG" alias="药房规格" type="string" length="20" display="2"
		layout="YPBZ" />
	<item id="YFDW" alias="药房单位" type="string" length="4" display="2"
		layout="YPBZ" />
	<item id="YFBZ" alias="药房包装" type="int" defaultValue="1" not-null="true"
		length="4" display="2" layout="YPBZ" minValue="0" />
	<item id="ZXBZ" alias="最小包装" type="int" length="4" defaultValue="1"
		display="2" not-null="1" layout="YPBZ" minValue="0" />
	<item id="BFGG" alias="病房规格" type="string" length="20" display="2"
		layout="YPBZ" />
	<item id="BFDW" alias="病房单位" type="string" length="4" display="2"
		layout="YPBZ" />
	<item id="BFBZ" alias="病房包装" type="int" defaultValue="1" not-null="true"
		length="4" display="2" layout="YPBZ" minValue="0" />

	<!-- 其他 -->
	<item id="FYFS" type="long" alias="发药方式" length="18" display="2"
		not-null="1" layout="QT">
		<dic id="phis.dictionary.hairMedicineWay" defaultIndex="0"
			autoLoad="true" />
	</item>
	<item id="GYFF" alias="给药方法" type="long" length="18" display="2"
		layout="QT">
		<dic id="phis.dictionary.drugWay" defaultIndex="0" />

	</item>
	<item id="TSYP" alias="特殊药品" type="int" length="2" queryable="true"
		defaultValue="0" layout="QT">
		<dic id="phis.dictionary.pecialMedicines">

		</dic>
	</item>
	<item id="YPZC" alias="药品贮藏" display="2" type="int" length="8"
		queryable="true" layout="QT">
		<dic id="phis.dictionary.drugStore" defaultIndex="0" />
	</item>
	<item id="YBFL" alias="医保分类" display="2" type="int" length="8"
		queryable="true" not-null="1" layout="QT">
		<dic id="phis.dictionary.medicalInsuranceClassification"
			defaultIndex="0" />
	</item>
	<item id="CFYP" alias="处方药品" display="2" type="int" length="8"
		queryable="true" layout="QT" not-null="1">
		<dic id="phis.dictionary.prescriptionDrugs" defaultIndex="0" />
	</item>
	<item id="QZCL" alias="取整策略" display="2" type="int" length="1"
		defaultValue="1" layout="QT">
		<dic>
			<item key="0" text="每次发药数量取整" />
			<item key="1" text="每天发药数量取整" />
			<item key="2" text="不取整" />
		</dic>
	</item>
	<item id="JSBZ" alias="激素药品" display="2" type="int" length="1"
		defaultValue="0" layout="QT">
		<dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>
	</item>
	<item id="XNXGBZ" alias="心脑血管药品" display="2" type="int" length="1"
		defaultValue="0" layout="QT">
		<dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>
	</item>
	<item id="ZYJMBZ" alias="中药静脉注射药品" display="2" type="int" length="1"
		defaultValue="0" layout="QT">
		<dic>
			<item key="0" text="否" />
			<item key="1" text="是" />
		</dic>
	</item>
	<item id="KSBZ" defaultValue="0" alias="是否抗生素" type="int" length="1"
		display="2" layout="KSS">
		<dic id="phis.dictionary.confirm" />
	</item>
	<item id="YCYL" alias="一日限量" type="bigDecimal" precision="2"
		length="12" display="2" layout="KSS" minValue="0" />

	<!--由于该选项就3个 是死属性,故没放到字典中 -->
	<item id="KSSDJ" alias="抗生素等级" length="12" type="int" display="2"
		layout="KSS">
		<dic>
			<item key="1" text="一级抗生素" />
			<item key="2" text="二级抗生素" />
			<item key="3" text="三级抗生素" />
		</dic>
	</item>
	<item id="DDDZ" alias="DDD值" type="bigDecimal" precision="2"
		length="11" maxValue="99999999.99" display="2" layout="KSS" minValue="0" />
	<item id="YQSYFS" alias="越权使用方式" type="int" length="1" display="2"
		layout="KSS" defaultValue="2">
		<dic>
			<item key="1" text="提醒"></item>
			<item key="2" text="禁止"></item>
		</dic>
	</item>
	<item id="SFSP" alias="是否审批" type="int" length="1" display="2"
		layout="KSS" defaultValue="2">
		<dic>
			<item key="1" text="需要"></item>
			<item key="2" text="不需要"></item>
		</dic>
	</item>
	<item id="ZSSF" defaultValue="0" alias="注射药品" type="int" length="1"
		display="2" layout="KSS">
		<dic id="phis.dictionary.confirm" />
	</item>
	<item id="ZFYP" defaultValue="1" alias="转方药品" type="int" length="1"
		display="2" layout="KSS" fixed="true">
		<dic id="phis.dictionary.confirm" />
	</item>
	<item id="XZSJ" alias="新增时间" display="0" type="datetime"
		defaultValue="%server.date.date" update="false" />
	<item id="ZFPB" alias="作废" type="int" display="0" defaultValue="0"
		update="false" />
	<item id="MESS" alias="药品说明" type="string" length="524288000"
		xtype="htmleditor" colspan="3" display="0" anchor="100%" />

</entry>
