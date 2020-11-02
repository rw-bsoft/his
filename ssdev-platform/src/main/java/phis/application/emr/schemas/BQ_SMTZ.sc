<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BQ_SMTZ" alias="生命体征">
  <item id="CJH" alias="采集号" type="long" length="18" not-null="1" generator="assigned" pkey="true">
  	 <key>
            <rule name="increaseId" type="increase" startPos="24" />
     </key>
  </item>
  <item id="CJZH" alias="采集组号" length="18" not-null="1"/>
  <item id="XMH" alias="项目号"  type="string" length="18"/>
  <item id="JHBZ" alias="计划标志" length="1" not-null="1"/>
  <item id="CJSJ" alias="采集时间" type="timestamp" not-null="1"/>
  <item id="ZYH" alias="住院号" length="18" not-null="1"/>
  <item id="BRKS" alias="病人科室" length="4"/>
  <item id="BRBQ" alias="病人病区" length="4"/>
  <item id="BRCH" alias="病人床号" type="string" length="6"/>
  <item id="TZNR" alias="生命体征内容" type="string" length="60"/>
  <item id="XMXB" alias="项目下标" type="string" length="20"/>
  <item id="FCBZ" alias="复测标志" length="1" not-null="1"/>
  <item id="FCGL" alias="复测关联" length="18"/>
  <item id="TWDXS" alias="体温单显示" length="1"/>
  <item id="JLSJ" alias="记录时间" type="timestamp" not-null="1"/>
  <item id="JLGH" alias="记录人员" type="string" length="10" not-null="1"/>
  <item id="ZFBZ" alias="作废标志" length="1" not-null="1"/>
  <item id="BZXX" alias="备注信息" type="string" length="255"/>
  <item id="YCBZ" alias="异常标志" length="1" not-null="1"/>
  <item id="JGID" alias="机构ID" length="20" />
  <item id="BZLX" alias="备注类型" type="long" length="22"/>
</entry>
