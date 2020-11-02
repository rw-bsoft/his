<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_SLXX_FORM" tableName="WL_SLXX" alias="申领信息(WL_SLXX)">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" startPos="24" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
  
	<item id="LZZH" alias="流转单号" length="30" display="0"/>
  
  
	<item id="WZMC" alias="物资名称" length="60" mode="remote" not-null="1" />
	<item id="WZGG" alias="物资规格" length="60" fixed="true"/>
	<item id="WZDW" alias="物资单位" length="10" fixed="true"/>
	<item id="KFXH" alias="物资库房" type="int" length="8" fixed="true" queryable="true" > 
		<dic id="phis.dictionary.treasury"  autoLoad="true" />
	</item>
	<item id="CKKF" alias="出库库房" type="int" length="8" display="0"  /> 

	<item id="TJSL" alias="推荐数量" virtual="true" length="10" fixed="true"/>
	<item id="WZSL" alias="物资数量" type="long" length="18"  not-null="1"/>
	<item id="SLKS" alias="申领科室" type="long" length="18"  queryable="true">
		<dic id="phis.dictionary.wl_hsqx" filter="['and',['eq',['$','item.properties.YGID'],['$','%user.userId']],['ne',['$','item.properties.LOGOFF'],['s','1']]]" autoLoad="true" /> 
	</item>

	<item id="SLSJ" alias="申领时间" type="date"  defaultValue="%server.date.date" />
  
	<item id="ZBLB" alias="账簿类别" type="int" length="8" display="0"/>
	<item id="DJXH" alias="单据序号" type="long" length="18" display="0"/>
  
	<item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
	<item id="BZXX" alias="备注信息" length="250"  colspan="2" xtype="textarea"/>
	<item id="ZDBZ" alias="字典标志" type="int" length="1" display="0"/>
	<item id="JHBZ" alias="计划标志" type="int" length="1" display="0"/>
</entry>
