<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.pub.schemas.PUB_ICD10" alias="ICD10">
	<item id="icd10" alias="ICD10"  type="string" length="20" pkey="true" width="150" />
	<item id="diseaseName" alias="疾病名称" type="string" length="200"   width="300"/>
  	<item id="pydm" alias="拼音代码" type="string" length="20"   width="150">
  		<set type="exp" run="server">['py',['$','r.diseaseName']]</set>
  	</item>
</entry>	