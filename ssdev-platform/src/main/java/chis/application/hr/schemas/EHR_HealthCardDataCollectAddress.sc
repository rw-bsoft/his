<?xml version="1.0" encoding="UTF-8"?>
<entry alias="地址信息">
  <item id="province" alias="省" type="string" not-null="1" group="户籍地址">
    <dic id="chis.dictionary.areaGrid"  parentKey="0" sliceType="3"/>
  </item>
  <item id="city" alias="市" type="int" not-null="1" group="户籍地址">
    <dic id="chis.dictionary.areaGrid"/>
  </item>
  <item id="region" alias="区" type="string" not-null="1" group="户籍地址">
    <dic id="chis.dictionary.areaGrid"/>
  </item>
  <item id="country" alias="乡(镇、街道)" type="string" not-null="1" length="20"  group="户籍地址"/>
  <item id="village" alias="村(居委会)" type="string" not-null="1" length="20"  group="户籍地址"/>
  
  
  <item id="province2" alias="省" type="string" not-null="0" group="居住地址(&lt;font color=\'red\'&gt;居住地址与户籍地址不相同时填写&lt;/font&gt;)">
    <dic id="chis.dictionary.areaGrid"  parentKey="0" sliceType="3"/>
  </item>
  <item id="city2" alias="市" type="int" not-null="0" group="居住地址(&lt;font color=\'red\'&gt;居住地址与户籍地址不相同时填写&lt;/font&gt;)">
    <dic id="chis.dictionary.areaGrid"/>
  </item>
  <item id="region2" alias="区" type="string" group="居住地址(&lt;font color=\'red\'&gt;居住地址与户籍地址不相同时填写&lt;/font&gt;)">
    <dic id="chis.dictionary.areaGrid"/>
  </item>
  <item id="country2" alias="乡(镇、街道)" type="string" length="20"  group="居住地址(&lt;font color=\'red\'&gt;居住地址与户籍地址不相同时填写&lt;/font&gt;)"/>
  <item id="village2" alias="村(居委会)" type="string" length="20"  group="居住地址(&lt;font color=\'red\'&gt;居住地址与户籍地址不相同时填写&lt;/font&gt;)"/>
  <item id="payMode" alias="医疗费用&lt;br&gt;支付方式" type="string">
  	<dic id="chis.dictionary.payMode"></dic>
  </item>
</entry>





