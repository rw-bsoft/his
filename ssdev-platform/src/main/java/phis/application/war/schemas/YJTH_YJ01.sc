<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YJTH_YJ01" tableName="YJ_ZY01" alias="医技退回主表数据">
	<item id="YJXH" alias="记录序号" type="long" display="0" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="ZYHM" alias="住院号码" type="string" length="10"  />
	<item ref="d.BRXM" />
	<item id="TJSJ" alias="申检时间" type="date"  />
	<item ref="c.PERSONNAME" alias="申检医生" queryable="false"/>
	<item ref="b.OFFICENAME" alias="申检科室" queryable="false"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.SYS_Office" >
			<join parent="ID" child="KSDM"></join>
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.SYS_Personnel" >
			<join parent="PERSONID" child="YSDM"></join>
		</relation>
		<relation type="parent" entryName="phis.application.hos.schemas.ZY_BRRY" >
			<join parent="ZYH" child="ZYH"></join>
		</relation>
	</relations>
</entry>
