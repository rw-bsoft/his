<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BQ_TJ01" alias="病区提交记录表"   sort="a.TJSJ desc">
	
	<item id="YZLX" alias="医嘱类型" type="int" length="2" not-null="1" display="0"/>
	<item id="XMLX" alias="项目类型" type="int" length="2" not-null="1" display="0"/>
	<item id="TJSJ" alias="提交时间" type="datetime" width="140"/>
	<item id="FYFS" alias="发药方式" type="long" length="18" >
		<dic id="phis.dictionary.hairMedicineWay"/>
	</item>
	<item id="TJBQ" alias="提交病区" type="int" length="8" not-null="1"  selected="true">
	      <dic id="phis.dictionary.department_bq" filter="['eq',['$','item.properties.JGID'],['$','%user.manageUnit.id']]" searchField="PYDM" autoLoad="true"/>
	</item>
	<item ref="c.PERSONNAME" alias="操作工号"/>
	<item ref="b.LSYZ" display="0"/>
	<item id="TJGH" alias="操作工号" length="10" not-null="1" display="0"/>
	<item id="FYBZ" alias="发药标志" type="int" length="1" not-null="1" display="0"/>
	<item id="JGID" alias="机构ID" type="string" length="20" not-null="1" display="0"/>
	<item id="TJXH" alias="序号" type="long" length="18" not-null="1" generator="assigned" pkey="true" />
	<relations>
	<relation type="child" entryName="phis.application.war.schemas.BQ_TJ02" >
		<join parent="TJXH" child="TJXH"></join>
	</relation>
	<relation type="child" entryName="phis.application.cic.schemas.SYS_Personnel" >
		<join parent="TJGH" child="PERSONID"></join>
	</relation>
	</relations>
</entry>
