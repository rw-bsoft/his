<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_ZFFP" alias="发票作废">
  <item id="MZXH" alias="门诊序号" length="12" type="long" display="0" not-null="1" generator="assigned" pkey="true" />
  <item id="JGID" alias="机构ID" type="string" display="0" length="8" not-null="1"/>
  <item id="FPHM" alias="发票号码" type="string" length="20"/>
  <item ref="b.BRXM"/>
  <item ref="b.BRXZ"/>
  <item ref="b.SFRQ" width="160"/>
  <item ref="b.CZGH" alias="原收费员">
  	<dic id="phis.dictionary.doctor"></dic>
  </item>
  <item ref="b.ZJJE"/>
  <item id="CZGH" alias="操作工号" type="string" display="0" length="10" />
  <item id="JZRQ" alias="结账日期" type="date" display="0"/>
  <item id="MZLB" alias="门诊类别" type="long" display="0" length="18"/>
  <item id="HZRQ" alias="汇总日期" type="date" display="0"/>
  <item id="ZFRQ" alias="作废日期" type="date" display="0"/>
  <relations>
		<relation type="parent" entryName="phis.application.ivc.schemas.MS_MZXX" >
			<join parent="MZXH" child="MZXH" />
		</relation>	
	</relations>
</entry>
