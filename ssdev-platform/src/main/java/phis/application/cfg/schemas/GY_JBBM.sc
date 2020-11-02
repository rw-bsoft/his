<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_JBBM" alias="疾病编码库">
  <item id="JBXH" alias="疾病序号" display="0" type="long" length="18" not-null="1" generator="assigned" pkey="true">
    <key>
      <rule name="increaseId" type="increase" length="12" startPos="22543"/>
    </key>
  </item>
  <item id="ICD9" alias="ICD编码" type="string" display="0" length="20" width="150"/>
  <item id="ICD10" alias="ICD编码" type="string" length="20" width="150" not-null="1" queryable="true"/>
  <item id="DMLB" alias="代码类别" display="0" type="int" defaultValue="10" length="2" not-null="1"/>
  <item id="JBMC" alias="疾病名称" type="string" length="255" width="250" not-null="1"/>
  <item id="JBLB" alias="疾病类别" display="0" type="long" length="18"/>
  <item id="PYDM" alias="拼音码"  type="string" length="8" queryable="true" selected="true" width="120" target="JBMC" codeType="py"/> 	
  <item id="QTXM" alias="统计码" display="0" type="string" length="20" width="120"/>
  <item id="FJBM" alias="附加编码" display="0" type="string" length="20"/>
  <item id="XBXZ" alias="性别限制" display="0" type="int" length="1"/>
  <item id="YXZY" alias="允许治愈" display="0" type="int" length="1"/>
  <item id="YXHZ" alias="允许好转" display="0" type="int" length="1"/>
  <item id="YXWY" alias="允许未愈" display="0" type="int" length="1"/>
  <item id="YXSW" alias="允许死亡" display="0" type="int" length="1"/>
  <item id="YXQT" alias="允许其它" display="0" type="int" length="1"/>
  <item id="KZFS" alias="控制方式" display="0" type="int" length="1"/>
  <item id="JBPB" alias="疾病判别" type="string" length="500" width="250">
  	<dic id="phis.dictionary.diseaseKind" render="LovCombo" />
  </item>
  <item id="JBBGK" alias="疾病报告卡" type="string" length="2" width="200" colspan="2">
  	<dic id="phis.dictionary.diseaseReportType"/>
  </item>
	<item id="BZXX" alias="备注信息" display="2" type="string" length="524288000" xtype="htmleditor" colspan="2" anchor="100%"/>
</entry>
