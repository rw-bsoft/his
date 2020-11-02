<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_YJ02_CIC" tableName="MS_YJ02" alias="门诊医技单02表" sort="YJZH ,SBXH asc">
	<item id="SBXH" alias="记录编号" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1" />
		</key>
	</item>
	<item id="YJZH" type="int" length="1" fixed="true" width="30" renderer="showColor" />
	<item id="JGID" alias="机构ID" length="8" display="0" not-null="1" defaultValue="%user.manageUnit.id" type="string"/>
	<item id="YJXH" alias="医技序号" type="long" display="0" length="18" not-null="1" defaultValue="0"/>
	<item id="YLXH" alias="医疗序号" type="int" display="0"  length="18" not-null="1"/>
	<item id="XMLX" alias="项目类型" type="int" display="0" length="2" defaultValue="0"/>
	<item id="YJZX" alias="医技主项" type="int" display="0" length="1" defaultValue="0"/>
	<item ref="b.FYMC" alias="项目名称" mode="remote" width="160"/>
	<item ref="b.FYDW" fixed="true"/>
	<item id="YLDJ" alias="医疗单价" type="double" length="10" precision="2" defaultValue="0" nullToValue="0"/>
	<item id="YLSL" alias="医疗数量" type="double" length="8" precision="2" max="999999.99" min="0.01" defaultValue="1"/>
	<item id="HJJE" alias="划价金额" type="double" fixed="true" length="12" precision="2" not-null="1"/>
	
	<item ref="c.ZXRQ" alias="执行时间" type="date" fixed="true" length="20" width="140" display="0"/>
	<item ref="c.YSDM" alias="申请医生" type="string" length="20" />
	<item ref="c.KSDM" alias="开单科室" display="1"/>
	<item ref="c.ZXKS" alias="执行科室" display="1"/>
	<item ref="c.MZXH" alias="门诊序号" type="string" display="0" length="20" fixed="true"/>
	<item ref="c.FPHM" alias="发票号码" type="string" display="0" length="20" fixed="true"/>
	<item id="FYGB" alias="费用归并" type="int"  display="0" length="18" not-null="1" defaultValue="0"/>
	<item id="ZFBL" alias="自负比例" type="double" length="6" precision="3" nullToValue="0" defaultValue="1" fixed="true"/>
	<item id="BZXX" alias="备注信息" type="string" display="0" length="255"/>
	<item id="DZBL" alias="打折比例" type="double" display="0" length="6" precision="3" defaultValue="1"/>
	<item id="ZFPB" alias="自负判别" type="int" length="1" display="0"/>
	<item id="SPBH" alias="审批编号" display="0" type="long" length="15"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.GY_YLSF_CIC">
			<join parent="FYXH" child="YLXH"></join>
		</relation>
		<relation type="parent" entryName="chis.application.his.schemas.MS_YJ01_CIC"/>
	</relations>
</entry>
