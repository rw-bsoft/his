<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="WL_SLXX" alias="申领信息(WL_SLXX)">
  <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
    <key>
		<rule name="increaseId" type="increase" startPos="24" />
	</key>
  </item>
  <item id="JGID" alias="机构ID" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
  
  <item id="SLSJ" alias="申领时间" type="date"/>
  <item id="LZZH" alias="流转单号" length="30" display="0"/>
  
  <item id="SLKS" alias="申领科室" type="long" length="18" >
     <dic id="phis.dictionary.department" filter="['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]" autoLoad="true" searchField="PYCODE" /> 
  </item>
  
  <item ref="b.WZMC" alias="物资名称" length="60" display="0" />
  <item id="WZMC" alias="物资名称" length="60" width="150"/>
  <item id="WZGG" alias="物资规格" length="60"/>
  <item id="WZDW" alias="物资单位" length="10"/>
  
  <item id="KFXH" alias="所属库房" type="int" length="8" >
  	<dic id="phis.dictionary.treasury"  autoLoad="true" />
  </item>
  <item id="CKKF" alias="出库库房" type="int" length="8" display="0" />
  
  <item id="ZBLB" alias="账簿类别" type="int" length="8">
    <dic id="phis.dictionary.booksCategory"  autoLoad="true" />
  </item>
  <item id="WZSL" alias="物资数量" type="double" length="18" precision="2"/>
  <item id="SLZT" alias="申领状态" type="int" length="1">
  	<dic>
        <item key="-1" text="新增"/>
        <item key="0"  text="提交"/>
        <item key="1"  text="制单"/>
        <item key="-9"  text="退回"/>
    </dic>
  </item>
  <item id="DJXH" alias="单据序号" type="long" length="18" display="0"/>
  
  <item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
  <item id="SLGH" alias="申领工号" length="10" display="0"/>
  <item id="BZXX" alias="备注信息" length="250" display="0"/>
  <item id="ZDBZ" alias="字典标志" type="int" length="1" display="0"/>
  <item id="JHBZ" alias="计划标志" type="int" length="1" display="0"/>
  <relations>
	<relation type="child" entryName="phis.application.cfg.schemas.WL_WZZD" >
		<join parent="WZXH" child="WZXH"></join>
	</relation>
  </relations>
</entry>
