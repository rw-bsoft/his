<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_GHYJ" alias="复诊预约">
	<item id="YJXH" alias="记录编号" display="0" type="long" length="18" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="10"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构编码" display="0" type="string" length="20" />
	<item id="KSDM" alias="挂号科室" type="long" length="18" >
		<dic id="phis.dictionary.department_reg" autoLoad="true"/>
	</item>
	<item ref="b.PYDM" alias="拼音代码"  display="0" width="60"/>
	<item id="YSDM" alias="挂号医生" type="string" length="10" >
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="BRID" alias="病人ID" display="0" type="long" length="20"/>
	<item id="YJRQ" alias="预约日期" display="0" type="date" />
	<item id="ZBLB" alias="值班类别" display="0" type="int" defaultValue="1" length="1"/>
	<item id="GHBZ" alias="挂号标志" display="0" type="int" length="1"/>
	<item id="CZGH" alias="操作工号" display="0" type="string" length="10" />
	<relations>
		<relation type="parent" entryName="phis.application.reg.schemas.MS_GHKS">
			<join parent="KSDM" child="KSDM" />
		</relation>
	</relations>
</entry>
