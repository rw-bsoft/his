<?xml version="1.0" encoding="UTF-8"?>

<entry  entityName="YK_TYPK_YKYPXX" alias="药品信息" tableName="YK_TYPK">
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品内码" type="long" length="18" not-null="1"
		generator="assigned" pkey="true" hidden="true" layout="JBXX">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX"/>
	<item id="TYMC" alias="通用名" type="string" width="180" anchor="100%" display="2"
		length="80" colspan="2"  layout="JBXX"/>
	<item id="YPGG" alias="规格" type="string" length="20"  layout="JBXX"/>
	<item id="YPDW" alias="单位" type="string" length="4" layout="JBXX"/>
	<item id="TYPE" alias="类别"  not-null="1"
		type="string" length="2" layout="JBXX">
		<dic id="phis.dictionary.storeroomType"/>
	</item>
	<item id="YPSX" alias="剂型"   not-null="1" type="string"
		length="18" layout="JBXX">
		<dic id="phis.dictionary.dosageForm"/>
	</item>
	<item id="YPJL" alias="剂量" type="string"  length="10"
		display="2"  not-null="1" layout="JBXX"/>
	<item id="JLDW" alias="剂量单位" type="string" length="8" display="2" layout="JBXX"/>
	<item id="YPDC" alias="档次"   not-null="1" type="string"
		length="2" display="2" layout="JBXX">
		<dic id="grade"/>	
	</item>
	<item id="YPXQ" alias="有效期" type="string" length="6" display="0" layout="JBXX"/>
	<item id="ABC" alias="ABC" type="string" 
		not-null="true" length="1" display="0" layout="JBXX">
		<dic>
			<item key="A" text="A" />
			<item key="B" text="B" />
			<item key="C" text="C" />
		</dic>
	</item>
	
	<item id="YPDM" alias="药品类型" type="string" length="160" layout="JBXX" display="2" >	
		<dic id="phis.dictionary.medicinesCode" render="Tree"></dic>
	</item>	

	<item id="PSPB"  alias="皮试判别" not-null="true"
		type="string" length="1" display="2" layout="JBXX">
		<dic id="phis.dictionary.pspb"/>
	</item>
	<item id="PYDM" alias="拼音码" type="string" length="160" selected="true"
		queryable="true" layout="JBXX">
		<set type="exp" run="server">['py',['$','r.YPMC']]
		</set>
	</item>
	<item id="WBDM" alias="五笔码" type="string" length="160" 
		queryable="true" layout="JBXX">
		<set type="exp" run="server">['wb',['$','r.YPMC']]
		</set>
	</item>
	<item id="JXDM" alias="角形码" type="string" length="160" queryable="true" layout="JBXX">
		<set type="exp" run="server">['jx',['$','r.YPMC']]
		</set>
	</item>
	<item id="BHDM" alias="笔画码" type="string" length="10" queryable="true" layout="JBXX" target="YPMC" codeType="bh"/>
	<item id="QTDM" alias="其它码" type="string" length="160" display="2" layout="JBXX" queryable="true"/>
	<item id="JBYWBZ"   alias="国家基本药物"
		type="string" length="1" display="2"  not-null="1" layout="JBXX">
		<dic id="phis.dictionary.confirm">			
		</dic>
	</item>
	<item id="JYLX" defaultValue="1"  alias="基药类型"
		type="int" length="1" display="2"  not-null="1" layout="JBXX">
		<dic id="phis.dictionary.jylx"/>			
	</item>
	<!-- 药品包装 -->
	<item id="ZXDW" alias="最小单位" type="string" length="4" display="2" layout="YPBZ"/>
	<item id="YFGG" alias="药房规格" type="string" length="4" display="2" layout="YPBZ"/>
	<item id="YFDW" alias="药房单位" type="string" length="4" display="2" layout="YPBZ"/>
	<item id="YFBZ" alias="药房包装" type="string" 
		not-null="true" length="4" display="2" layout="YPBZ"/>
	<item id="ZXBZ" alias="最小包装" type="string" length="4" display="2"  not-null="1" layout="YPBZ"/>
	<item id="BFGG" alias="病房规格" type="string" length="4" display="2" layout="YPBZ"/>
	<item id="BFDW" alias="病房单位" type="string" length="4" display="2" layout="YPBZ"/>
	<item id="BFBZ" alias="病房包装" type="string"  change="onChange()"
		not-null="true" length="4" display="2" layout="YPBZ"/>
	
	<!-- 其他 -->
	<!-- 建表后换成从数据库查 -->
	<item id="FYFS"  alias="发药方式" 
		type="string" length="18" display="2"  not-null="1" layout="QT">
		<dic id="phis.dictionary.hairMedicineWay">
			
		</dic>
	</item>
	<!-- 建表后换成从数据库查 -->
	<item id="GYFF"  alias="给药方法" type="string"
		length="18" display="2" layout="QT">
		<dic id="phis.dictionary.drugWay" />
		
	</item>
	<!-- 建表后换成从数据库查 -->
	<item id="TSYP" alias="特殊药品"  type="string"
		length="2"  queryable="true"  not-null="1" layout="QT">
		<dic id="phis.dictionary.pecialMedicines">
			
		</dic>
	</item>
	<!-- 建表后换成从数据库查 -->
	<item id="YPZC" alias="药品贮藏"  display="2" type="string"
		length="8"  queryable="true" layout="QT">
		<dic id="phis.dictionary.drugStore" />
	</item>
	<!-- 建表后换成从数据库查 -->
	<item id="YBFL" alias="医保分类"  display="2" type="string"
		length="8"  queryable="true"  not-null="1" layout="QT">
		<dic id="phis.dictionary.medicalInsuranceClassification"/>		
	</item>
	<!-- 建表后换成从数据库查 -->
	<item id="CFYP" alias="处方药品" display="2"   type="string"
		length="8"  queryable="true" layout="QT">
		<dic id="phis.dictionary.prescriptionDrugs"/>
	</item>
	<item id="QZCL" alias="取整策略" display="2"  type="int"
		length="1"   layout="QT">
		<dic>
			<item key="0" text="每次发药数量取整"/>
			<item key="1" text="每天发药数量取整"/>
			<item key="2" text="不取整"/>
		</dic>
	</item>
    <item id="YCXL" alias="一次限量" type="double" defaultValue="0" precision="2" length="12" layout="QT" minValue="0" display="2"/>
	<!-- 和一次用量增加js关联 -->
	<item id="KSBZ"   alias="是否抗生素" type="string"
		length="1" display="2" layout="KSS">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="YCYL" alias="一次用量" type="number" length="12" display="2" layout="KSS"/>
	
	<!--友谊该选项就3个 是死属性,故没放到字典中-->
	<item id="KSSDJ" alias="抗生素等级" length="12" display="2" layout="KSS"  type="int">
		<dic>
			<item key="1" text="一级抗生素"></item>
			<item key="2" text="二级抗生素"></item>
			<item key="3" text="三级抗生素"></item>
		</dic>
	</item>
	<item id="DDDZ" alias="DDD值" type="number"  display="2" layout="KSS"/>
</entry>
