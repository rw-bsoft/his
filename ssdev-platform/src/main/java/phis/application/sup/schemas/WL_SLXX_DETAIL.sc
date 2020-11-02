<?xml version="1.0" encoding="UTF-8"?>

<entry id="phis.application.sup.schemas.WL_SLXX_DETAIL" tableName="WL_SLXX" alias="申领信息(WL_SLXX)">
    <item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
        <key>
            <rule name="increaseId" type="increase" startPos="24" />
        </key>
    </item>
    <item id="WZXH" alias="物资序号" type="long" length="18" display="0"/>
  
    <item id="WZMC" alias="物资名称" length="60" not-null="1" width="140"/>
    <item id="WZGG" alias="物资规格" length="60" not-null="1"/>
    <item id="WZDW" alias="物资单位" length="10" not-null="1"/>
    <item id="TJSL" alias="推荐数量" type="double" width="90" virtual="true" fixed="true" length="18" precision="2"/>
    <item id="WZSL" alias="申领数量" type="double" length="18" precision="2" not-null="1"/>
    <item id="SLSJ" alias="申领时间" type="date"/>
    
    <item id="SLSL" alias="申领数量" type="double" length="18" precision="2" virtual="true" fixed="true" display="0"/>
    <item id="SLXH" alias="申领序号" type="long" length="18" virtual="true" fixed="true" display="0"/>
    <item id="WFSL" alias="未发数量" type="double" length="18" precision="2" virtual="true" fixed="true" display="0"/>
    <item id="GLFS" alias="管理方式" fixed="true" virtual="true">
        <dic>
            <item key="1" text="库存管理"/>
            <item key="2" text="科室管理"/>
            <item key="3" text="台账管理"/>
        </dic>
    </item>
  
    <item id="JGID" alias="机构ID" type="string" length="20" display="0" defaultValue="%user.manageUnit.id"/>
    <item id="LZZH" alias="流转单号" length="30" display="0"/>
    <item id="KFXH" alias="所属库房" type="int" length="8" not-null="1" defaultValue="%user.properties.treasuryId" display="0"> 
        <dic id="treasury"  autoLoad="true" />
    </item>
    <item id="SLKS" alias="申领科室" type="long" length="18" display="0">
        <dic id="department"  autoLoad="true" searchField="PYDM">
        </dic>
    </item>
    <item id="SLGH" alias="申领工号" length="10" display="0">
        <dic id="doctor"  autoLoad="true" searchField="PYDM">
        </dic>
    </item>
    <item id="SLZT" alias="申领状态" type="int" length="1" defaultValue="-1" display="0">
        <dic>
            <item key="-1" text="新增"/>
            <item key="0"  text="提交"/>
            <item key="1"  text="制单"/>
        </dic>
    </item>
    <item id="ZBLB" alias="账簿类别" type="int" length="8" display="0"/>
    <item id="DJXH" alias="单据序号" type="long" length="18" display="0"/>
    <item id="BZXX" alias="备注信息" length="250" display="0"/>
    <item id="ZDBZ" alias="字典标志" type="int" length="1" display="0"/>
    <item id="JHBZ" alias="计划标志" type="int" length="1" display="0"/>
</entry>
