<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YF_DB01" alias="药房调拔01" >
  <item id="JGID" alias="机构ID" length="20" type="string" not-null="1" display="0"/>
  <item id="SQYF" alias="申请药房" length="18" not-null="1" generator="assigned" pkey="true" type="long" display="0"/>
  <item id="MBYF" alias="目标药房" length="18" type="long" display="0"/>
  <item id="SQRQ" alias="申请日期" type="datetime" display="0"/>
  <item id="CZGH" alias="操作工号" type="string" length="10" display="0"/>
  <item id="TJBZ" alias="提交标志" length="1" type="int" defaultValue="0" display="0"/>
  <item id="CKBZ" alias="出库标志" length="1" type="int" defaultValue="0" display="0"/>
  <item id="CKGH" alias="出库工号" type="string" length="10" display="0"/>
  <item id="CKRQ" alias="出库日期" type="datetime" display="0"/>
  <item id="RKBZ" alias="入库标志" length="1" type="int" defaultValue="0" display="0"/>
  <item id="RKGH" alias="入库工号" type="string" length="10" display="0"/>
  <item id="RKRQ" alias="入库日期" type="datetime" display="0"/>
  <item id="TYPB" alias="退药判别" length="1" not-null="1" type="int" defaultValue="0" display="0"/>
  <item id="BZXX" alias="备注信息" type="string" length="100" display="0"/>
  <item id="SQDH" alias="单号" length="6" not-null="1" pkey="true" type="int"/>
  <item ref="b.PERSONNAME" alias="操作员" queryable="false"/>
	<relations>
	<relation type="child" entryName="phis.application.cic.schemas.SYS_Personnel" >
		<join parent="CZGH" child="PERSONID"></join>
	</relation>
	</relations>
</entry>
