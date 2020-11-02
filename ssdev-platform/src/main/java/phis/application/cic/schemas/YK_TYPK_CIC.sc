<?xml version="1.0" encoding="UTF-8"?>

<entry id="YK_TYPK_CIC" tableName="YK_TYPK" alias="药品信息" sort="a.YPXH desc" >
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品内码" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true"  layout="JBXX" >
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="8869" />
		</key>
	</item>
	<item id="TP" alias="" type="string" renderer="onRenderer" width="23" length="20" virtual="true" display="1"/>
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX"/>
	<item id="TYMC" alias="通用名" type="string" display="2"  width="180" anchor="100%"
		length="80" colspan="2"  layout="JBXX"/>
	<item id="YPGG" alias="规格" type="string" length="20"  layout="JBXX"/>
	<item id="YPDW" alias="单位" type="string" length="2" layout="JBXX" not-null="1"/>
	<item id="TYPE" alias="类别" display="2"  not-null="1" defaultValue="1"
		type="int" length="2" layout="JBXX" queryable="true">
		<dic id="phis.dictionary.prescriptionType"/>
	</item>
	<item id="ZBLB" alias="账簿类别" display="0"  not-null="1" 
		type="long" />
	<item id="YPSX" alias="剂型" type="long"  not-null="1" 
		length="18" layout="JBXX">
		<dic id="dosageForm" defaultIndex="0"/>
	</item>
	<item id="YPJL" alias="剂量"  type="double" precision="3"  defaultValue="1" length="10"
		display="2"  not-null="1" layout="JBXX" minValue="0"/>
	<item id="JLDW" alias="剂量单位" type="string" length="8" display="2" layout="JBXX"/>
	<item id="YPDC" alias="档次"   not-null="1" type="int"
		length="2" display="2" layout="JBXX" defaultValue="1">
		<dic id="grade"/>	
	</item>
	<item id="YPXQ" alias="有效期" type="int" length="6" display="0" layout="JBXX"/>
	<item id="ABC" alias="ABC" type="string" defaultValue="C"
		not-null="true" length="1" display="0" layout="JBXX">
		<dic>
			<item key="A" text="A" />
			<item key="B" text="B" />
			<item key="C" text="C" />
		</dic>
	</item>
	<item id="YPDM" alias="药品类型" type="string"  length="10" layout="JBXX" display="2" >
		<dic id="phis.dictionary.medicinesCode" render="Tree"></dic>
	</item>	
	<item id="PSPB" defaultValue="2" alias="皮试判别" not-null="true" type="int"
		length="1" display="2" layout="JBXX">
		<dic id="confirm" defaultIndex="0"/>
	</item>
	<item id="PYDM" alias="拼音码" type="string" length="10" not-null="1" selected="true" target="YPMC" codeType="py"
		queryable="true" layout="JBXX">
	</item>
	<item id="WBDM" alias="五笔码" type="string" length="10" target="YPMC" codeType="wb"
		queryable="true" layout="JBXX">
	
	</item>
	<item id="JXDM" alias="角形码" type="string" length="10" queryable="true" layout="JBXX" target="YPMC" codeType="jx">
	</item>
	<item id="BHDM" alias="笔画码" type="string" length="10" queryable="true" layout="JBXX" target="YPMC" codeType="bh"/>
	<item id="QTDM" alias="其它码" type="string" length="10" display="2" layout="JBXX" queryable="true"/>
	<item id="JBYWBZ" defaultValue="0"  alias="国家基本药物"
		type="int" length="1" display="2"  not-null="1" layout="JBXX">
		<dic id="confirm">			
		</dic>
	</item>
	<item id="JYLX" defaultValue="0"  alias="基药类型"
		type="int" length="1" display="2"  not-null="1" layout="JBXX">
		<dic id="jbywlx">			
		</dic>
	</item>
	<!-- 药品包装 -->
	<item id="ZXDW" alias="最小单位" type="string" length="2" display="2" layout="YPBZ"/>
	<item id="YFGG" alias="药房规格" type="string" length="20" display="2" layout="YPBZ"/>
	<item id="YFDW" alias="药房单位" type="string" length="2" display="2" layout="YPBZ"/>
	<item id="YFBZ" alias="药房包装" type="int" defaultValue="1"
		not-null="true" length="4" display="2" layout="YPBZ" minValue="0"/>
	<item id="ZXBZ" alias="最小包装" type="int" length="4" defaultValue="1" display="2"  not-null="1" layout="YPBZ" minValue="0"/>
	<item id="BFGG" alias="病房规格" type="string" length="20" display="2" layout="YPBZ"/>
	<item id="BFDW" alias="病房单位" type="string" length="2" display="2" layout="YPBZ"/>
	<item id="BFBZ" alias="病房包装" type="int" defaultValue="1" change="onChange()"
		not-null="true" length="4" display="2" layout="YPBZ" minValue="0"/>
	
	<!-- 其他 -->
	<item id="FYFS" type="long" alias="发药方式" 
		length="18" display="2"  not-null="1" layout="QT">
		<dic id="hairMedicineWay" defaultIndex="0">
			
		</dic>
	</item>
	<item id="GYFF" alias="给药方法" type="long"
		length="18" display="2" layout="QT">
		<dic id="drugWay" defaultIndex="0"/>
		
	</item>
	<item id="TSYP" alias="特殊药品" type="int" 
		length="2"  queryable="true"   defaultValue="0" layout="QT">
		<dic id="pecialMedicines" >
			
		</dic>
	</item>
	<item id="YPZC" alias="药品贮藏" display="2" type="int"
		length="8"  queryable="true" layout="QT" >
		<dic id="drugStore" defaultIndex="0"/>
	</item>
	<item id="YBFL" alias="医保分类"  display="2" type="int"
		length="8"  queryable="true"  not-null="1" layout="QT">
		<dic id="medicalInsuranceClassification" defaultIndex="0"/>		
	</item>
	<item id="CFYP" alias="处方药品" display="2"  type="int"
		length="8"  queryable="true" layout="QT">
		<dic id="prescriptionDrugs" defaultIndex="0"/>
	</item>
	<item id="QZCL" alias="取整策略" display="2"  type="int"
		length="1"  defaultValue="1" layout="QT">
		<dic>
	<item key="0" text="每次发药数量取整"/>
	<item key="1" text="每天发药数量取整"/>
	<item key="2" text="不取整"/>
		</dic>
	</item>
    <item id="YCXL" alias="一次限量" type="double" defaultValue="0" precision="2" length="12" layout="QT" minValue="0" display="2"/>
	<item id="KSBZ" defaultValue="0"  alias="是否抗生素" type="int"
		length="1" display="2" layout="KSS">
		<dic id="confirm"/>
	</item>
	<item id="YCYL" alias="一次用量" type="bigDecimal" precision="2" length="12" display="2" layout="KSS" minValue="0"/>
	
	<!--由于该选项就3个 是死属性,故没放到字典中-->
	<item id="KSSDJ" alias="抗生素等级" length="12" type="int"  display="2" layout="KSS">
		<dic>
			<item key="1" text="一级抗生素"></item>
			<item key="2" text="二级抗生素"></item>
			<item key="3" text="三级抗生素"></item>
		</dic>
	</item>
	<item id="DDDZ" alias="DDD值" type="bigDecimal" precision="2" length="11" maxValue="99999999.99"  display="2" layout="KSS" minValue="0"/>
	<item id="XZSJ" alias="新增时间" display="0" type="date" defaultValue="%server.date.date"  update="false"/>
	<item id="ZFPB" alias="作废" type="int"  display="0"  defaultValue="0" update="false"/>
	<item id="MESS" alias="药品说明" type="string" length="524288000" xtype="htmleditor" colspan="3" display="0" anchor="100%"/>
</entry>
