<?xml version="1.0" encoding="UTF-8"?>

<entry id="AMQC_SYSQ01_SQFORM" tableName="AMQC_SYSQ01" alias="抗菌药物申请单">
	<item id="SQDH" alias="申请单号" length="18" type="long" not-null="1"
		generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="10" startPos="1000" />
		</key>
	</item>
	<item id="JZLX" alias="就诊类型" length="1" type="int" not-null="1"
		display="0" defaultValue="1" />
	<item id="JZXH" alias="就诊序号" length="18" not-null="1" display="0" />
	<item id="BRKS" alias="病人科室" length="18" type="long" fixed="true">
		<dic id="phis.dictionary.department_zy" autoLoad="true" />
	</item>
	<item id="BRBQ" alias="病人病区" length="18" type="long" fixed="true">
		<dic id="phis.dictionary.department_bq" autoLoad="true" />
	</item>
	<item id="SQYS" alias="申请医生" length="10" not-null="1"
		defaultValue="%user.userId" fixed="true">
		<dic id="phis.dictionary.doctor" autoLoad="true" />
	</item>
	<item ref="b.ZYHM" alias="住院号码" queryable="true" selected="true"
		fixed="true" />
	<item ref="b.BRXM" alias="病人姓名" queryable="true" fixed="true" />
	<item id="AGE" alias="年龄" virtual="true" fixed="true" />
	<item id="ZSYS" alias="主治医师" virtual="true" fixed="true">
		<dic id="phis.dictionary.doctor" autoLoad="true" />
	</item>
	<item id="JBMC" alias="诊断" width="400" virtual="true" fixed="true" />
	<item id="BRXB" virtual="true" alias="性别" fixed="true">
		<dic id="phis.dictionary.gender" autoLoad="true" />
	</item>
	<item id="BRCH" alias="床号" length="20" queryable="true" fixed="true" />
	<item id="JBBH" alias="疾病编号" length="18" type="long" display="0" />
	<item id="YSYKJY" alias="已使用抗菌药" length="255" display="0" />
	<item id="MQBQ" alias="目前病情" length="255" display="0" />
	<item id="YPXH" alias="药品名称" length="18" type="long" not-null="1"
		display="0" />
	<item ref="c.YPMC" alias="抗菌药物名称" mode="remote" width="150"
		not-null="1" />
	<item id="YFGG" alias="药房规格" width="60" fixed="true" virtual="true" />
	<item id="MRYL" alias="每日用量" width="60" type="double" fixed="true"
		virtual="true" />
	<item id="RZYL" alias="日用量" type="double" length="18" precision="2"
		maxValue="999999999.99" minValue="0.01" width="70" not-null="1" />
	<item id="YYLC" alias="用药天数" type="int" length="4" maxLength="4"
		minValue="1" width="70" not-null="1" />
	<item id="HJYL" alias="合计用量" virtual="true" type="double" fixed="true"
		width="70" />
	<item id="GRBQYZZ" alias="感染病情严重者" length="1" type="int" xtype="checkbox"
		width="200" />
	<item id="MYGNDX" alias="免疫功能低下伴感染者" length="1" type="int" xtype="checkbox"
		width="200" />
	<item id="SQYWMG" alias="使用限制、非限制级抗菌药物超72小时，并经微生物培养和药敏验证实对这抗菌药物耐药，对申请药物敏感."
		width="520" length="1" type="int" xtype="checkbox" />
	<item id="JYBGH" alias="检验报告号" length="50" />
	<item id="QTYY" alias="其它" length="1" type="int" xtype="checkbox"
		width="50" />
	<item id="QTYYXS" alias="其它原因详述" length="255" width="400" />
	<item id="DJRQ" alias="提交日期" type="date" defaultValue="%server.date.date"
		display="0" />
	<item id="SQRQ" alias="申请日期" type="date" defaultValue="%server.date.date"
		fixed="true" />
	<item id="HZKS" alias="会诊科室" length="18" type="long">
		<dic id="phis.dictionary.department_zy" autoLoad="true" />
	</item>
	<item id="SPYL" alias="审批用量" length="18" width="130" type="double"
		precision="2" fixed="true" />
	<item id="DJZT" alias="单据状态" length="1" not-null="1" type="int"
		display="0" defaultValue="0" />
	<item id="SPJG" alias="审批结果" length="1" not-null="1" type="int"
		display="0" defaultValue="0" />
	<item id="ZFBZ" alias="作废标志" length="1" not-null="1" type="int"
		display="0" defaultValue="0" />
	<item id="JGID" alias="机构ID" length="20" type="string" display="0" />
	<relations>
		<relation type="parent" entryName="phis.application.war.schemas.ZY_BRRY_BQ">
			<join parent="ZYH" child="JZXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK">
			<join parent="YPXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>
