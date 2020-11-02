<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="BQ_SMTZ" alias="生命体征" sort="CJSJ desc">
  <item id="CJH" alias="采集号" type="long" length="18" display="0" generator="assigned" pkey="true"/>
  <item id="CJSJ" alias="采集时间" type="timestamp" width="120" />
  <item id="XMXB" alias="项目下标" type="string" length="20" display="0"/>
  <item ref="b.XMMC" width="140"/>
  <item id="TZNR" alias="体征内容" type="string" length="60" width="110" />
  <item id="XMH" alias="项目号" type="string" length="18" display="0"/>
  <item id="BZXX" alias="备注信息" type="string" length="18" display="0"/>
  <relations>
		<relation type="parent" entryName="phis.application.emr.schemas.BQ_TZXM" >
			<join parent="XMH" child="XMH"></join>
		</relation>
  </relations>
</entry>
