<?xml version="1.0" encoding="UTF-8"?>

<entry id="AMQC_SYSQ01_SQ" tableName="AMQC_SYSQ01" alias="抗菌药物申请单">
	<item id="SQDH" alias="申请单号" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="10"
				startPos="1000" />
		</key>
	</item>
	<item id="BRBQ" alias="病人病区" length="6" renderTo="render_BRBQ" display="2">
	</item>
	<item id="SQRQ" alias="申请日期" type="date"/>
	<item ref="c.YPMC" alias="抗菌药物名称" width="150" />
	<item id="RZYL" alias="日总用量" length="18" type="double" precision="2"/>
	<item id="YYLC" alias="用药疗程(天)" type="double" width="100" length="4"/>
	<item id="HJYL" alias="合计用量" virtual="true" type="double" precision="2" renderer="totalHJYL"/>
	<item id="SQYS" alias="申请医生" length="10" not-null="1">
		<dic id="phis.dictionary.doctor"/>
	</item>
	<item id="SPJG" alias="审批结果" length="1" not-null="1">
		<dic id="phis.dictionary.approveResult" />
	</item>
	<item id="SPYL" alias="审批用量" length="18" type="double" precision="2" />
	<item id="DJZT" alias="状态" defalutValue="0" length="1">
		<dic id="phis.dictionary.billStatus" />
	</item>
	<item id="ZFBZ" alias="作废" length="1">
		<dic alias="" >
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>
	<relations>
		<relation type="parent" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="ZYH" child="JZXH"></join>
		</relation>
		<relation type="child" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
	</relations>
</entry>
