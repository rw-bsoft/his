<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.wl.schemas.View_TjxmMatch" alias="体检项目匹配" sort="a.XMZHM desc">
  <item id="XMZHM" pkey="true" alias="项目组合码" type="string"
    width="160" length="16" not-null="1" display="1"
    generator="assigned">
    <key>
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
    </key>
  </item>
  <item id="XMBH" alias="项目编码" type="string" fixed="true" />
  <item id="XMMC" alias="项目名称" type="string" width="120" queryable="true" fixed="true" />
  <item id="EHRMATCH" alias="健康档案匹配" type="string" >
  	<dic id="chis.dictionary.ehrmatch"/>
  </item>
  <item id="KSBM" alias="科室编码" type="string" fixed="true" />
  <item id="KSMC" alias="科室名称" type="string"  fixed="true" />
  <item id="UNITID" alias="机构" type="string" width="120" fixed="true" >
  	<dic id="chis.@manageUnit" includeParentMinLen="6" onlySelectLeaf="true" lengthLimit="9" querySliceType="0" render="Tree" rootVisible = "true" parentKey="%user.manageUnit.id"/>
  </item>
</entry>