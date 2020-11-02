<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BQ_TYMX" alias="退药明细">
	<item id="JLXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="TYBQ" alias="开嘱病区" type="string" length="20">
		<dic id="phis.dictionary.department_bq" autoLoad="true"/>
	</item> 	
	<item id="TJBZ" alias="状态" type="int">
		<dic id="phis.dictionary.submit" autoLoad="true"/>
	</item>
	<item ref="b.YPMC" alias="名称" />
	<item ref="c.CDMC" alias="产地" />
	<item id="SQRQ" alias="申请日期" type="date" not-null="1"/>
	<item id="YPGG" alias="规格" length="20" width="50"/>
	<item id="TYLX" alias="类型" type="int" length="1">
		<dic autoLoad="true">
			<item key="1" text="自动" />
			<item key="2" text="手动" />
		</dic>
	</item>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH"></join>
		</relation>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_CDDZ" >
			<join parent="YPCD" child="YPCD"></join>
		</relation>
	</relations>
</entry>
